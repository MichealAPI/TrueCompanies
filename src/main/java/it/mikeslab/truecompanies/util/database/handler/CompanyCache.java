package it.mikeslab.truecompanies.util.database.handler;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.object.Company;
import it.mikeslab.truecompanies.object.Employee;

import java.util.List;
import java.util.UUID;

public class CompanyCache {
    private final Cache<Integer, Company> cache;

    public CompanyCache() {
        Database database = TrueCompanies.getInstance().getDatabase();

        cache = Caffeine.newBuilder()
                .build();

        List<Company> companies = database.retrieveCompanies();

        for (Company company : companies) {
            cache.put(company.getId(), company);
        }
    }

    public Company getCompany(int id) {
        return cache.getIfPresent(id);
    }

    public Employee getEmployee(Company company, UUID playerUUID) {
        return company.getEmployees().stream()
                .filter(employee -> employee.getPlayerUUID().equals(playerUUID))
                .findFirst()
                .orElse(null);
    }

    public void putCompany(Company company) {
        cache.put(company.getId(), company);
    }

    public void removeCompany(int id) {
        cache.invalidate(id);
    }

    public void clearCache() {
        cache.invalidateAll();
    }

    public List<Company> getAllCompanies() {
        // Implement the logic to retrieve all companies from the database
        Database database = TrueCompanies.getInstance().getDatabase();
        return database.retrieveCompanies();
    }
}
