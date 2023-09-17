package it.mikeslab.truecompanies.command.subcommand;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import it.mikeslab.truecompanies.TrueCompanies;
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

            selectEmployeeToManage(player, company);
        });
    }

    private Group getGroup(Company company, String playerName) {
        int senderGroupID = company.getEmployees().get(playerName);
        return company.getGroups().get(senderGroupID);
    }

    private void selectEmployeeToManage(Player player, Company company) {
        new PlayerSelectorMenu(instance).show(
                player,
                Language.getString(LangKey.SELECT_A_PLAYER_MANAGE, false),
                Optional.of(company), Optional.of(false)
        ).thenAccept(selectedEmployee -> {
            if (selectedEmployee == null) {
                player.closeInventory();
                return;
            }

            manageSelectedEmployee(player, company, selectedEmployee);
        });
    }

    private void manageSelectedEmployee(Player player, Company company, String selectedEmployee) {

        new GroupSelectorMenu(instance).show(player, company).thenAccept(group -> {
            Group employerGroup = getGroup(company, player.getName());
            Group currentGroup = getGroup(company, selectedEmployee);

            if (group == null) {
                player.closeInventory();
                return;
            }

            if(currentGroup.getId() == group.getId()) {
                player.sendMessage(Language.getString(LangKey.CANNOT_PROMOTE_DEMOTE_SAME_RANK, true));
                player.closeInventory();
                return;
            }

            boolean isPromotion = currentGroup.getId() > group.getId();

            if ((isPromotion && !employerGroup.canPromote) || (!isPromotion && !employerGroup.canDemote)) {
                sendPermissionError(player, isPromotion);
                return;
            }

            player.closeInventory();

            CompanyUtils companyUtils = instance.getCompanyUtils();
            companyUtils.changeEmployeeGroup(company, selectedEmployee, group.getId());

            String actionMessage = isPromotion ? Language.getString(LangKey.PROMOTED, false) : Language.getString(LangKey.DEMOTED, false);
            player.sendMessage(Language.getString(LangKey.MANAGE_YOU_HAVE, true, Map.of("%action%", actionMessage,
                            "%player%", selectedEmployee,
                            "%company%", company.getDisplayName(),
                            "%group%", group.getTag()
            )));

            notifyTargetOfPromotion(selectedEmployee, group, company.getDisplayName(), isPromotion);
        });
    }

    private void sendPermissionError(Player player, boolean isPromotion) {
        String actionMessage = isPromotion ? Language.getString(LangKey.PROMOTE, false) : Language.getString(LangKey.DEMOTE, false);
        player.sendMessage(Language.getString(LangKey.MANAGE_EMPLOYEES_NO_PERMS, true).replace("%action%", actionMessage));
        player.closeInventory();
    }

    private void notifyTargetOfPromotion(String selectedEmployee, Group group, String companyName, boolean isPromotion) {
        Player target = Bukkit.getPlayer(selectedEmployee);

        if (target != null) {
            String actionMessage = isPromotion ? Language.getString(LangKey.PROMOTED, false) : Language.getString(LangKey.DEMOTED, false);
            target.sendMessage(Language.getString(LangKey.MANAGE_YOU_HAVE_BEEN, true, Map.of(
                    "%action%", actionMessage,
                    "%company%", companyName,
                    "%group%", group.getTag()
            )));
        }
    }
}
