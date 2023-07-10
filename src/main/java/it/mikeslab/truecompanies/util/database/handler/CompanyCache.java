package it.mikeslab.truecompanies.util.database.handler;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.object.Company;

import java.util.ArrayList;
import java.util.List;

public class CompanyCache {
    private final Cache<Integer, Company> cache;
    private final List<Company> modifiedCompanies;

    public CompanyCache() {
        cache = Caffeine.newBuilder().build(this::loadCompany);
        modifiedCompanies = new ArrayList<>();
    }

    public Company getCompany(int id) {
        return cache.getIfPresent(id);
    }

    private Company loadCompany(int id) {
        Company company = TrueCompanies.getInstance().getDatabase().retrieveCompany(id);
        if (company != null) {
            modifiedCompanies.add(company);
        }
        return company;
    }

    public void updateCompany(Company company) {
        cache.put(company.getId(), company);
        modifiedCompanies.add(company);
    }

    public void createCompany(Company company) {
        cache.put(company.getId(), company);
        modifiedCompanies.add(company);
    }

    public void deleteCompany(int id) {
        cache.invalidate(id);
    }

    public void saveModifiedCompanies() {
        TrueCompanies.getInstance().getDatabase().saveCompanies(modifiedCompanies);
        modifiedCompanies.clear();
    }
}
