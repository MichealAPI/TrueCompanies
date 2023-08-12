package it.mikeslab.truecompanies.command.subcommand;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.event.CompanyFireEvent;
import it.mikeslab.truecompanies.menu.selector.CompanySelectorMenu;
import it.mikeslab.truecompanies.menu.selector.PlayerSelectorMenu;
import it.mikeslab.truecompanies.object.Group;
import it.mikeslab.truecompanies.util.CompanyUtils;
import it.mikeslab.truecompanies.util.language.LangKey;
import it.mikeslab.truecompanies.util.language.Language;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

@CommandAlias("company")
@RequiredArgsConstructor
public class SubCompanyFire extends BaseCommand {

    private final TrueCompanies instance;

    @Subcommand("fire")
    @Description("Fire an employee from a company")
    public void onFireCommand(Player player) {
        CompanyUtils companyUtils = instance.getCompanyUtils();

        new CompanySelectorMenu(instance).show(player).thenAccept(company -> {

            if(company == null) {
                player.closeInventory();
                return;
            }

            int senderGroupID = company.getEmployees().get(player.getName());
            Group senderGroup = company.getGroups().get(senderGroupID);

            if(!senderGroup.canFire) {
                player.sendMessage(Language.getString(LangKey.FIRE_EMPLOYEES_NO_PERMS, true));
                return;
            }


            new PlayerSelectorMenu(instance).show(player, Language.getString(LangKey.SELECT_TO_FIRE, false), Optional.of(company), Optional.of(false))
                    .thenAccept(selectedEmployee -> {

                        if(selectedEmployee == null) {
                            player.closeInventory();
                            return;
                        }

                        // Calling Fire event
                        CompanyFireEvent event = new CompanyFireEvent(player, Bukkit.getOfflinePlayer(selectedEmployee), company);
                        Bukkit.getPluginManager().callEvent(event);

                        if(event.isCancelled()) return;


                        companyUtils.fireEmployee(company, selectedEmployee);

                        player.closeInventory();
                        player.sendMessage(Language.getString(LangKey.YOU_HAVE_FIRED, true, Map.of(
                                "%player%", selectedEmployee,
                                "%company%", company.getDisplayName())));

                        Player target = Bukkit.getPlayer(selectedEmployee);

                        if(target != null) {
                            target.sendMessage(Language.getString(LangKey.YOU_HAVE_BEEN_FIRED, true, Map.of(
                                    "%company%", company.getDisplayName())));
                        }
                    });
        });
    }

    @Subcommand("leave")
    @Description("Leave a company")
    public void onLeaveCommand(Player player) {
        CompanyUtils companyUtils = instance.getCompanyUtils();

        new CompanySelectorMenu(instance).show(player).thenAccept(company -> {

            if (company == null) {
                player.closeInventory();
                return;
            }

            companyUtils.fireEmployee(company, player.getName());
            player.sendMessage(Language.getString(LangKey.YOU_HAVE_BEEN_FIRED, true, Map.of(
                    "%company%", company.getDisplayName())));
        });
    }

}
