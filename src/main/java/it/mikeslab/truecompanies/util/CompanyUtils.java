package it.mikeslab.truecompanies.util;

import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.loader.CompanyLoader;
import it.mikeslab.truecompanies.menu.general.ConfirmMenu;
import it.mikeslab.truecompanies.object.Company;
import it.mikeslab.truecompanies.object.Group;
import it.mikeslab.truecompanies.util.language.LangKey;
import it.mikeslab.truecompanies.util.language.Language;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Utility class for operations related to Companies.
 */
@RequiredArgsConstructor
public class CompanyUtils {

    private final CompanyLoader companyLoader;

    /**
     * Hires an employee to the specified company.
     *
     * @param company        The company.
     * @param playerUsername The username of the player to be hired.
     * @param groupID        The group ID for the hired player.
     */
    public void hireEmployee(Company company, String playerUsername, int groupID) {
        updateUserStatus(company, playerUsername, groupID, ActionType.HIRE);
    }

    /**
     * Fires an employee from the specified company.
     *
     * @param company  The company.
     * @param username The username of the player to be fired.
     */
    public void fireEmployee(Company company, String username) {
        updateUserStatus(company, username, null, ActionType.FIRE);
    }

    /**
     * Updates user status in a company (Hire/Fire).
     *
     * @param company  The company.
     * @param username The username of the player.
     * @param groupID  The group ID for the action. Can be null for fire action.
     * @param action   The type of action.
     */
    private void updateUserStatus(Company company, String username, Integer groupID, ActionType action) {
        if (action == ActionType.HIRE) {
            company.getEmployees().put(username, groupID);
        } else {
            company.getEmployees().remove(username);
        }
        saveCompanyToFile(company);

        Group group = groupID != null ? company.getGroups().get(groupID) : null;

        List<String> commands = (action == ActionType.HIRE) ? group.getHireCommands() : company.getFireCommands();
        executeCommands(commands, username);

        LangKey key = (action == ActionType.HIRE) ? LangKey.HIRED_TITLE : LangKey.FIRED_TITLE;
        String title = StringUtils.capitalize(Language.getString(key, true));
        String subTitle = company.getDisplayName() + (group != null ? " | " + group.getTag() : "");

        sendTitle(username, subTitle, title);
    }

    private void sendTitle(String playerUsername, String subTitle, String title) {
        if(Bukkit.getPlayer(playerUsername) == null) return;

        Player player = Bukkit.getPlayer(playerUsername);

        player.sendTitle(title, subTitle);
        player.playSound(player.getLocation(), "entity.player.levelup", 1, 1);
    }


    private void executeCommands(List<String> commands, String targetUsername) {

        for(String command : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", targetUsername));
        }

    }



    public void changeEmployeeGroup(Company company, String employeeUsername, int newGroupID) {

        int oldRank = company.getEmployees().get(employeeUsername);
        Group oldGroup = company.getGroups().get(oldRank);

        boolean isPromotion = oldGroup.getId() < newGroupID;

        Group group = company.getGroups().get(newGroupID);

        if (company.getEmployees().containsKey(employeeUsername)) {
            company.getEmployees().put(employeeUsername, newGroupID);
            saveCompanyToFile(company);
        }

        executeCommands(isPromotion ? group.getPromoteCommands() : group.getDemoteCommands(), employeeUsername);

        String title, subTitle;
        subTitle = oldGroup.getTag() + " > " + group.getTag();

        if(isPromotion) {
            title = StringUtils.capitalize(Language.getString(LangKey.PROMOTED_TITLE, true));

            this.sendTitle(employeeUsername, subTitle, title);
        } else {
            title = StringUtils.capitalize(Language.getString(LangKey.DEMOTED_TITLE, true));

            this.sendTitle(employeeUsername, subTitle, title);
        }



    }

    public void givePaycheck(Company company, double payCheckAmount, Player sender, OfflinePlayer target) {

        if(company.getBalance() < payCheckAmount) {
            sender.closeInventory();
            sender.sendMessage(Language.getString(LangKey.WITHDRAW_NOT_ENOUGH_MONEY, true));
            return;
        }

        new ConfirmMenu(TrueCompanies.getInstance()).show(sender,
                Language.getString(LangKey.GIVE_PAYCHECK_MENU, false,
                        Map.of(
                                "%player%", target.getName())
                )).thenAccept(result -> {

                    if (result) {
                        updateBalance(company, -payCheckAmount);

                        TrueCompanies.getEcon().depositPlayer(target, payCheckAmount);

                        sender.sendMessage(Language.getString(LangKey.PAYCHECK_RECEIVED, true, Map.of(
                                "%player%", target.getName(),
                                "%company%", company.getDisplayName(),
                                "%amount%", String.valueOf(payCheckAmount)
                        )));

                        if(target.isOnline()) {
                            target.getPlayer().sendMessage(Language.getString(LangKey.PAYCHECK_RECEIVED, true, Map.of(
                                    "%company%", company.getDisplayName(),
                                    "%amount%", String.valueOf(payCheckAmount)
                            )));
                        }
                    }
        });


    }

    public void transferCompanyOwnership(Company company, Player newOwner, Player oldOwner) {
        // Assuming Company class has a setOwner method
        int newOwnerOldGroup = company.getEmployees().getOrDefault(newOwner.getName(), -1);

        if (newOwnerOldGroup != -1) {
            String oldGroupTag = company.getGroups().get(newOwnerOldGroup).getTag();

            for (String employee : company.getEmployees().keySet()) {

                if (company.getEmployees().get(employee) == 1) {
                    System.out.println("Employee is owner");
                    company.getEmployees().put(employee, newOwnerOldGroup);

                    oldOwner.sendMessage(Language.getString(LangKey.MANAGE_YOU_HAVE_BEEN, true, Map.of(
                            "%action%", Language.getString(LangKey.DEMOTED, false),
                            "%player%", newOwner.getName(),
                            "%company%", company.getDisplayName(),
                            "%group%", oldGroupTag
                    )));

                    newOwner.sendMessage(Language.getString(LangKey.MANAGE_YOU_HAVE, true, Map.of(
                            "%action%", Language.getString(LangKey.DEMOTED, false),
                            "%player%", oldOwner.getName(),
                            "%company%", company.getDisplayName(),
                            "%group%", oldGroupTag
                    )));
                }
            }
        } else {
            company.getEmployees().remove(oldOwner.getName());

            oldOwner.sendMessage(Language.getString(LangKey.YOU_HAVE_BEEN_FIRED, true, Map.of(
                    "%player%", newOwner.getName(),
                    "%company%", company.getDisplayName()
            )));

            newOwner.sendMessage(Language.getString(LangKey.YOU_HAVE_FIRED, true, Map.of(
                    "%player%", oldOwner.getName(),
                    "%company%", company.getDisplayName()
            )));
        }

        company.getEmployees().put(newOwner.getName(), 1);

        oldOwner.sendMessage(Language.getString(LangKey.TRANSFER_OWNERSHIP_SUCCESS, true, Map.of(
                "%player%", newOwner.getPlayer().getName(),
                "%company%", company.getDisplayName()
        )));


        newOwner.getPlayer().sendMessage(Language.getString(LangKey.TRANSFER_OWNERSHIP_NEWOWNER, true, Map.of(
                "%player%", newOwner.getPlayer().getName(),
                "%company%", company.getDisplayName()
        )));

        saveCompanyToFile(company);

    }


    public void updateBalance(Company company, double amount) {

        if (company == null) return;

        double newBalance = company.getBalance() + amount;
        company.setBalance(newBalance);
        saveCompanyToFile(company);

    }

    public boolean updateGroupPermissions(Company company, Group group) {
        if (company == null) return false;

        // Assuming Company class has a setGroup method
        company.getGroups().put(group.getId(), group);

        saveCompanyToFile(company);

        return true;
    }

    private void saveCompanyToFile(Company company) {
        File companyFile = new File(companyLoader.companiesDirectory, company.getId() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(companyFile);

        // Assuming you have setters and getters for all the attributes of a Company
        config.set("balance", company.getBalance());

        config.set("data", null);

        for(String employeeName : company.getEmployees().keySet()) {
            config.set("data." + employeeName + ".group", company.getEmployees().get(employeeName));
        }

        for(Map.Entry<Integer, Group> groupEntry : company.getGroups().entrySet()) {

            config.set("groups." + groupEntry.getKey() + ".permissions.can-hire", groupEntry.getValue().canHire);
            config.set("groups." + groupEntry.getKey() + ".permissions.can-fire", groupEntry.getValue().canFire);
            config.set("groups." + groupEntry.getKey() + ".permissions.can-demote", groupEntry.getValue().canDeposit);
            config.set("groups." + groupEntry.getKey() + ".permissions.can-withdraw", groupEntry.getValue().canWithdraw);
            config.set("groups." + groupEntry.getKey() + ".permissions.can-deposit", groupEntry.getValue().canDeposit);
            config.set("groups." + groupEntry.getKey() + ".permissions.can-promote", groupEntry.getValue().canPromote);
            config.set("groups." + groupEntry.getKey() + ".permissions.can-give-paychecks", groupEntry.getValue().canPaychecks);
        }

        try {
            config.save(companyFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private enum ActionType {
        HIRE,
        FIRE
    }
}
