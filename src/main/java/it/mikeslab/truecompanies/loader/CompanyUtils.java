package it.mikeslab.truecompanies.loader;

import it.mikeslab.truecompanies.object.Company;
import it.mikeslab.truecompanies.object.Group;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class CompanyUtils {
    private final CompanyLoader companyLoader;

    public void hireEmployee(String companyId, String username, int rank) {
        Company company = companyLoader.getCompany(companyId);
        if (company == null) return;

        company.getEmployees().put(username, rank);
        saveCompanyToFile(companyId, company);

        return;
    }

    public void fireEmployee(String companyId, String username) {
        Company company = companyLoader.getCompany(companyId);
        if (company == null) return;

        company.getEmployees().remove(username);
        saveCompanyToFile(companyId, company);

        //todo execution command here
    }

    public void changeEmployeeRank(String companyId, String username, int newRank) {
        Company company = companyLoader.getCompany(companyId);
        if (company == null) return;

        if (company.getEmployees().containsKey(username)) {
            company.getEmployees().put(username, newRank);
            saveCompanyToFile(companyId, company);
        }
    }

    //todo here
    public boolean transferOwnership(String companyId, String newOwner) {
        Company company = companyLoader.getCompany(companyId);
        if (company == null) return false;

        // Assuming Company class has a setOwner method
        int newOwnerOldGroup = company.getEmployees().get(newOwner);
        company.getEmployees().put(newOwner, 1);

        for(String employee : company.getEmployees().keySet()) {
            if(company.getEmployees().get(employee) == 1) {
                company.getEmployees().put(employee, newOwnerOldGroup);
            }
        }


        saveCompanyToFile(companyId, company);

        return true;
    }

    public void updateBalance(String companyId, int amount) {
        Company company = companyLoader.getCompany(companyId);
        if (company == null) return;

        int newBalance = company.getBalance() + amount;
        company.setBalance(newBalance);
        saveCompanyToFile(companyId, company);

    }

    public boolean updateGroupPermissions(String companyId, Group group) {
        Company company = companyLoader.getCompany(companyId);
        System.out.println("Company is null: " + (company == null));

        if (company == null) return false;

        // Assuming Company class has a setGroup method
        company.getGroups().put(group.getId(), group);

        System.out.println("Group id: " + group.getId());
        System.out.println("Group tag: " + group.getTag());

        saveCompanyToFile(companyId, company);

        return true;
    }

    private void saveCompanyToFile(String companyId, Company company) {
        File companyFile = new File(companyLoader.companiesDirectory, companyId + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(companyFile);

        // Assuming you have setters and getters for all the attributes of a Company
        config.set("balance", company.getBalance());

        config.set("data", null);
        config.set("groups", null);

        for(String employeeName : company.getEmployees().keySet()) {
            config.set("data." + employeeName + ".group", company.getEmployees().get(employeeName));
        }

        for(Map.Entry<Integer, Group> groupEntry : company.getGroups().entrySet()) {
            System.out.println(groupEntry.getKey() + " " + groupEntry.getValue().getTag());
            config.set("groups." + groupEntry.getKey() + ".tag", groupEntry.getValue().getTag());
            config.set("groups." + groupEntry.getKey() + ".permissions.can-hire", groupEntry.getValue().canHire);
            config.set("groups." + groupEntry.getKey() + ".permissions.can-fire", groupEntry.getValue().canFire);
            config.set("groups." + groupEntry.getKey() + ".permissions.can-demote", groupEntry.getValue().canDeposit);
            config.set("groups." + groupEntry.getKey() + ".permissions.can-withdraw", groupEntry.getValue().canWithdraw);
            config.set("groups." + groupEntry.getKey() + ".permissions.can-deposit", groupEntry.getValue().canDeposit);
            config.set("groups." + groupEntry.getKey() + ".permissions.can-promote", groupEntry.getValue().canPromote);
        }

        try {
            config.save(companyFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
