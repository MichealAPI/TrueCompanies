package it.mikeslab.truecompanies.util.database.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.object.Company;
import it.mikeslab.truecompanies.util.error.SentryDiagnostic;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class JSONDatabase implements Database {
    private final File dataFolder;
    private final File jsonFile;
    private final Logger logger;
    private final Gson gson;

    /**
     * Constructs a JSONDatabase instance.
     */
    public JSONDatabase() {
        this.dataFolder = TrueCompanies.getInstance().getDataFolder();
        this.jsonFile = new File(dataFolder, "companies.json");
        this.logger = TrueCompanies.getInstance().getLogger();
        this.gson = new Gson();
    }

    @Override
    public void connect() {
        if (!jsonFile.exists()) {
            try {
                jsonFile.createNewFile();
            } catch (IOException e) {
                SentryDiagnostic.capture(e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void disconnect() {
        CompanyCache companyCache = TrueCompanies.getInstance().getCompanyCache();
        saveCompanies(companyCache.getAllCompanies());
    }

    @Override
    public void deleteCompany(int id) {
        List<Company> companies = retrieveCompanies();
        companies.removeIf(company -> company.getId() == id);
        saveCompanies(companies);
    }

    @Override
    public void updateCompany(Company company) {
        List<Company> companies = retrieveCompanies();
        int index = findCompanyIndex(companies, company.getId());
        if (index != -1) {
            companies.set(index, company);
            saveCompanies(companies);
        } else {
            logger.warning("Cannot update company. Company with ID " + company.getId() + " not found.");
        }
    }

    @Override
    public void createCompany(Company company) {
        List<Company> companies = retrieveCompanies();
        companies.add(company);
        saveCompanies(companies);
    }

    @Override
    public void saveCompanies(List<Company> companies) {
        try (FileWriter writer = new FileWriter(jsonFile)) {
            gson.toJson(companies, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Company> retrieveCompanies() {
        if (!jsonFile.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(jsonFile)) {
            Type companyListType = new TypeToken<ArrayList<Company>>(){}.getType();
            return gson.fromJson(reader, companyListType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int findCompanyIndex(List<Company> companies, int companyId) {
        for (int i = 0; i < companies.size(); i++) {
            Company company = companies.get(i);
            if (company.getId() == companyId) {
                return i;
            }
        }
        return -1;
    }
}
