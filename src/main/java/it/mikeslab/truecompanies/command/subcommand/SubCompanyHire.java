package it.mikeslab.truecompanies.command.subcommand;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.event.CompanyFireEvent;
import it.mikeslab.truecompanies.event.CompanyHireEvent;
import it.mikeslab.truecompanies.util.CompanyUtils;
import it.mikeslab.truecompanies.menu.selector.CompanySelectorMenu;
import it.mikeslab.truecompanies.menu.selector.GroupSelectorMenu;
import it.mikeslab.truecompanies.menu.selector.PlayerSelectorMenu;
import it.mikeslab.truecompanies.object.Company;
import it.mikeslab.truecompanies.object.Group;
import it.mikeslab.truecompanies.util.language.LangKey;
import it.mikeslab.truecompanies.util.language.Language;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

@CommandAlias("company")
@RequiredArgsConstructor
public class SubCompanyHire extends BaseCommand {

    private final TrueCompanies instance;

    @Subcommand("hire")
    @Description("Hire an employee for a company")
    public void onHireCommand(Player player) {
        new CompanySelectorMenu(instance).show(player).thenAccept(company -> {
            if (company == null) {
                player.closeInventory();
                return;
            }

            Group senderGroup = getSenderGroup(company, player);

            if (!senderGroup.canHire) {
                player.sendMessage(Language.getString(LangKey.HIRE_EMPLOYEES_NO_PERMS, true));
                return;
            }

            new PlayerSelectorMenu(instance).show(player, Language.getString(LangKey.SELECT_TO_HIRE, false), Optional.of(company), Optional.of(true)).thenAccept(selectedEmployee -> {
                if (selectedEmployee == null) {
                    player.closeInventory();
                    return;
                }

                CompanyUtils companyUtils = instance.getCompanyUtils();

                new GroupSelectorMenu(instance).show(player, company).thenAccept(group -> {
                    if (group == null) {
                        player.closeInventory();
                        return;
                    }

                    // Calling Hire event
                    CompanyHireEvent event = new CompanyHireEvent(player, Bukkit.getPlayer(selectedEmployee), company, group);
                    Bukkit.getPluginManager().callEvent(event);

                    if(event.isCancelled()) return;

                    companyUtils.hireEmployee(company, selectedEmployee, group.getId());

                    player.closeInventory();
                    player.sendMessage(Language.getString(LangKey.YOU_HIRED, true, Map.of("%player%", selectedEmployee, "%company%", company.getDisplayName(), "%group%", group.getTag())));

                    notifyTargetOfHiring(selectedEmployee, company.getDisplayName(), group);
                });
            });
        });
    }

    private Group getSenderGroup(Company company, Player player) {
        int senderGroupID = company.getEmployees().get(player.getName());
        return company.getGroups().get(senderGroupID);
    }

    private void notifyTargetOfHiring(String selectedEmployee, String companyName, Group group) {
        Player target = Bukkit.getPlayer(selectedEmployee);

        if (target != null) {
            target.sendMessage(Language.getString(LangKey.YOU_HAVE_BEEN_HIRED, true, Map.of("%company%", companyName, "%group%", group.getTag())));
        }
    }
}
