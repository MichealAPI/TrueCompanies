package it.mikeslab.truecompanies.loader;

import it.mikeslab.truecompanies.object.Company;
import it.mikeslab.truecompanies.object.Group;
import it.mikeslab.truecompanies.util.error.SentryDiagnostic;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyLoader {

    public final File companiesDirectory;
    private final Map<String, Company> companies = new HashMap<>();

    public CompanyLoader(File dataFolder) {
        this.companiesDirectory = new File(dataFolder, "companies");
        if (!companiesDirectory.exists()) {
            companiesDirectory.mkdirs();
        }
    }

    // Method to load all companies into memory
    public void loadAllCompanies() {
        File[] companyFiles = companiesDirectory.listFiles();

        if (companyFiles == null) return;

        for (File companyFile : companyFiles) {
            String id = companyFile.getName().replace(".yml", "");
            Company company = loadCompany(id);
            if (company != null) {
                companies.put(id, company);
            }
        }
    }

    public Company getCompany(String id) {
        return companies.getOrDefault(id, loadCompany(id)); // If the company is not loaded, load it, ables to load companies on the fly
    }

    public Company[] getCompanies(String userName) {
        return companies.values().stream()
                .filter(company -> company.getEmployees().containsKey(userName))
                .toArray(Company[]::new);
    }

    private Company loadCompany(String id) {
        try {

        File companyFile = new File(companiesDirectory, id + ".yml");

        if (!companyFile.exists()) {
            return null;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(companyFile);

        String displayName = config.getString("displayName");
        String description = config.getString("description");
        String chatFormat = config.getString("chatFormat");
        int balance = config.getInt("balance");

        Map<Integer, Group> groups = loadGroups(config);
        Map<String, Integer> employees = loadEmployees(config);

        List<String> fireCommands = config.getStringList("commands.on-fire");

        return new Company(id, displayName, description, balance, groups, employees, chatFormat, fireCommands);

        } catch (Exception e) {
            e.printStackTrace();
            SentryDiagnostic.capture(e);
            return null;
        }
    }

    private Map<Integer, Group> loadGroups(YamlConfiguration config) {
        try {

            Map<Integer, Group> groups = new HashMap<>();
            if (config.contains("groups")) {
                for (String groupId : config.getConfigurationSection("groups").getKeys(false)) {
                    String tag = config.getString("groups." + groupId + ".tag");
                    boolean canHire = config.getBoolean("groups." + groupId + ".permissions.can-hire");
                    boolean canFire = config.getBoolean("groups." + groupId + ".permissions.can-fire");
                    boolean canDeposit = config.getBoolean("groups." + groupId + ".permissions.can-deposit");
                    boolean canWithdraw = config.getBoolean("groups." + groupId + ".permissions.can-withdraw");
                    boolean canPromote = config.getBoolean("groups." + groupId + ".permissions.can-promote");
                    boolean canDemote = config.getBoolean("groups." + groupId + ".permissions.can-demote");
                    boolean canPaychecks = config.getBoolean("groups." + groupId + ".permissions.can-give-paychecks");

                    List<String> hireCommands = config.getStringList("groups." + groupId + ".commands.on-hire");
                    List<String> promoteCommands = config.getStringList("groups." + groupId + ".commands.on-promote");
                    List<String> demoteCommands = config.getStringList("groups." + groupId + ".commands.on-demote");


                    Group group = new Group(
                            canHire,
                            canFire,
                            canDeposit,
                            canWithdraw,
                            canPromote,
                            canDemote,
                            canPaychecks,
                            tag,
                            Integer.parseInt(groupId),
                            hireCommands,
                            promoteCommands,
                            demoteCommands
                    );

                    groups.put(Integer.parseInt(groupId), group);
                }
            }
            return groups;

        } catch (Exception e) {
            e.printStackTrace();
            SentryDiagnostic.capture(e);
            return null;
        }
    }

    private Map<String, Integer> loadEmployees(YamlConfiguration config) {
        Map<String, Integer> employees = new HashMap<>();

        if (config.contains("data")) {
            for (String username : config.getConfigurationSection("data").getKeys(false)) {
                int groupID = config.getInt("data." + username + ".group");
                employees.put(username, groupID);
            }
        }
        return employees;
    }
}
