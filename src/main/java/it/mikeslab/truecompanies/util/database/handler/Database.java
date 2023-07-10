package it.mikeslab.truecompanies.util.database.handler;

import it.mikeslab.truecompanies.object.Company;

import java.util.List;

public interface Database {
    void connect();
    void disconnect();

    void deleteCompany(int id);
    void updateCompany(Company company);
    void createCompany(Company company);
    void saveCompanies(List<Company> companies);

    Company retrieveCompany(int id);
    Company[] retrieveCompanies();
}
