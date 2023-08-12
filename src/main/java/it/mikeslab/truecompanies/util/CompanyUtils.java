package it.mikeslab.truecompanies.util;

import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.event.CompanyFireEvent;
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

@RequiredArgsConstructor
public class CompanyUtils {
    private final CompanyLoader companyLoader;

    public void hireEmployee(Company company, String username, int rank) {

        company.getEmployees().put(username, rank);
        saveCompanyToFile(company.getId(), company);

        Group group = company.getGroups().get(rank);

        executeCommands(group.getHireCommands(), username);

        String title = StringUtils.capitalize(Language.getString(LangKey.HIRED_TITLE, true));
        String subTitle = company.getDisplayName() + " | " + group.getTag();

        this.sendTitle(username, subTitle, title);

    }

    private void sendTitle(String username, String subTitle, String title) {
        if(Bukkit.getPlayer(username) == null) return;

        Player player = Bukkit.getPlayer(username);

        player.sendTitle(title, subTitle);
        player.playSound(player.getLocation(), "entity.player.levelup", 1, 1);
    }

    public void fireEmployee(Company company, String username) {

        company.getEmployees().remove(username);
        saveCompanyToFile(company.getId(), company);

        executeCommands(company.getFireCommands(), username);

        String title = StringUtils.capitalize(Language.getString(LangKey.FIRED_TITLE, true));
        String subTitle = company.getDisplayName();

        this.sendTitle(username, subTitle, title);
    }


    private void executeCommands(List<String> commands, String targetName) {

        for(String command : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", targetName));
        }

    }



    public void changeEmployeeGroup(Company company, String employeeUsername, int newGroup) {

        int oldRank = company.getEmployees().get(employeeUsername);
        Group oldGroup = company.getGroups().get(oldRank);

        boolean isPromotion = oldGroup.getId() < newGroup;

        Group group = company.getGroups().get(newGroup);

        if (company.getEmployees().containsKey(employeeUsername)) {
            company.getEmployees().put(employeeUsername, newGroup);
            saveCompanyToFile(company.getId(), company);
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

    public void givePaycheck(Company company, TrueCompanies instance, double payCheckAmount, Player sender, OfflinePlayer target) {

        if(company.getBalance() < payCheckAmount) {
            sender.closeInventory();
            sender.sendMessage(Language.getString(LangKey.WITHDRAW_NOT_ENOUGH_MONEY, true));
            return;
        }

        new ConfirmMenu(instance).show(sender,
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

        saveCompanyToFile(company.getId(), company);

    }


    public void updateBalance(Company company, double amount) {

        if (company == null) return;

        double newBalance = company.getBalance() + amount;
        company.setBalance(newBalance);
        saveCompanyToFile(company.getId(), company);

    }

    public boolean updateGroupPermissions(Company company, Group group) {
        if (company == null) return false;

        // Assuming Company class has a setGroup method
        company.getGroups().put(group.getId(), group);

        saveCompanyToFile(company.getId(), company);

        return true;
    }

    private void saveCompanyToFile(String companyId, Company company) {
        File companyFile = new File(companyLoader.companiesDirectory, companyId + ".yml");
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
}
