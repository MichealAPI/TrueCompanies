package it.mikeslab.truecompanies.command.subcommand;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.menu.selector.CompanySelectorMenu;
import it.mikeslab.truecompanies.menu.selector.GroupSelectorMenu;
import it.mikeslab.truecompanies.menu.selector.PlayerSelectorMenu;
import it.mikeslab.truecompanies.object.Company;
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
public class SubCompanyManage extends BaseCommand {

    private final TrueCompanies instance;

    @Subcommand("manage")
    @Description("Promote or Demote an employee in a company")
    public void onManageCommand(Player player) {
        new CompanySelectorMenu(instance).show(player).thenAccept(company -> {
            if (company == null) {
                player.closeInventory();
                return;
            }
            new PlayerSelectorMenu(instance).show(player, Language.getString(LangKey.SELECT_A_PLAYER_MANAGE, false), Optional.of(company), Optional.of(false)).thenAccept(selectedEmployee -> {
                if (selectedEmployee != null) {
                    manageSelectedEmployee(player, company, selectedEmployee);
                } else {
                    player.closeInventory();
                }
            });
        });
    }

    private void manageSelectedEmployee(Player player, Company company, String selectedEmployee) {
        new GroupSelectorMenu(instance).show(player, company).thenAccept(group -> {
            Group employerGroup = company.getGroups().get(company.getEmployees().get(player.getName()));
            Group currentGroup = company.getGroups().get(company.getEmployees().get(selectedEmployee));

            if (group == null || currentGroup.getId() == group.getId()) {
                player.sendMessage(Language.getString(LangKey.CANNOT_PROMOTE_DEMOTE_SAME_RANK, true));
                player.closeInventory();
                return;
            }

            boolean isPromotion = currentGroup.getId() > group.getId();
            if ((isPromotion && !employerGroup.canPromote) || (!isPromotion && !employerGroup.canDemote)) {
                player.sendMessage(Language.getString(LangKey.MANAGE_EMPLOYEES_NO_PERMS, true).replace("%action%", isPromotion ? Language.getString(LangKey.PROMOTE, false) : Language.getString(LangKey.DEMOTE, false)));
                player.closeInventory();
                return;
            }

            instance.getCompanyUtils().changeEmployeeGroup(company, selectedEmployee, group.getId());
            player.sendMessage(Language.getString(LangKey.MANAGE_YOU_HAVE, true, Map.of(
                    "%action%", Language.getString(isPromotion ? LangKey.PROMOTED : LangKey.DEMOTED, false),
                    "%player%", selectedEmployee,
                    "%company%", company.getDisplayName(),
                    "%group%", group.getTag()
            )));

            Player target = Bukkit.getPlayer(selectedEmployee);
            if (target != null) {
                target.sendMessage(Language.getString(LangKey.MANAGE_YOU_HAVE_BEEN, true, Map.of(
                        "%action%", Language.getString(isPromotion ? LangKey.PROMOTED : LangKey.DEMOTED, false),
                        "%company%", company.getDisplayName(),
                        "%group%", group.getTag()
                )));
            }
        });
    }
}
