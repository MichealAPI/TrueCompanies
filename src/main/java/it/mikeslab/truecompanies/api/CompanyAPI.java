package it.mikeslab.truecompanies.api;

import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.loader.CompanyLoader;
import it.mikeslab.truecompanies.object.Company;
import it.mikeslab.truecompanies.object.Group;
import it.mikeslab.truecompanies.util.CompanyUtils;
import org.bukkit.entity.Player;

/**
 * Provides an API for managing companies, employees, and groups.
 */
public final class CompanyAPI {

    private final CompanyUtils companyUtils;
    private final CompanyLoader companyLoader;

    /**
     * Constructs the CompanyAPI with dependencies.
     *
     * @param companyUtils   Utility class for company operations
     * @param companyLoader  Utility class for loading companies
     */
    public CompanyAPI(CompanyUtils companyUtils, CompanyLoader companyLoader) {
        this.companyUtils = companyUtils;
        this.companyLoader = companyLoader;
    }

    /**
     * Fires an employee from a company.
     *
     * @param company           The company to remove the employee from
     * @param employeeUsername  The username of the employee
     */
    public void fireEmployee(Company company, String employeeUsername) {
        companyUtils.fireEmployee(company, employeeUsername);
    }

    /**
     * Hires an employee for a company.
     *
     * @param company           The company to add the employee to
     * @param employeeUsername  The username of the new employee
     * @param group             The rank or group ID for the new employee
     */
    public void hireEmployee(Company company, String employeeUsername, int group) {
        companyUtils.hireEmployee(company, employeeUsername, group);
    }

    /**
     * Changes the group or rank of an existing employee.
     *
     * @param company           The company the employee belongs to
     * @param employeeUsername  The username of the employee
     * @param newGroup          The new rank or group ID for the employee
     */
    public void changeEmployeeGroup(Company company, String employeeUsername, int newGroup) {
        companyUtils.changeEmployeeGroup(company, employeeUsername, newGroup);
    }

    /**
     * Transfers the ownership of a company from an old owner to a new one.
     *
     * @param company   The company being transferred
     * @param newOwner  The new owner
     * @param oldOwner  The current owner
     */
    public void transferCompanyOwnership(Company company, Player newOwner, Player oldOwner) {
        companyUtils.transferCompanyOwnership(company, newOwner, oldOwner);
    }

    /**
     * Updates the balance of a company.
     *
     * @param company  The company to update
     * @param amount   The amount to change, can be positive (add) or negative (subtract)
     */
    public void updateBalance(Company company, double amount) {
        companyUtils.updateBalance(company, amount);
    }

    /**
     * Updates the permissions associated with a group.
     *
     * @param company  The company the group belongs to
     * @param group    The group to update permissions for
     */
    public void updateGroupPermissions(Company company, Group group) {
        companyUtils.updateGroupPermissions(company, group);
    }

    /**
     * Retrieves a company based on its ID.
     *
     * @param companyID  The unique identifier of the company
     * @return           The Company object, or null if not found
     */
    public Company getCompany(String companyID) {
        return companyLoader.getCompany(companyID);
    }

    /**
     * Factory method to instantiate the API with the default dependencies.
     *
     * @return  An instance of CompanyAPI
     */
    public static CompanyAPI createDefaultAPI() {
        TrueCompanies instance = TrueCompanies.getInstance();
        return new CompanyAPI(instance.getCompanyUtils(), instance.getCompanyLoader());
    }
}
