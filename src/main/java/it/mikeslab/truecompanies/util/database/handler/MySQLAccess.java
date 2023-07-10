package it.mikeslab.truecompanies.util.database.handler;

import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.object.Company;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MySQLAccess implements Database {
    private Connection connection;

    @Override
    public void connect() {
        FileConfiguration config = TrueCompanies.getInstance().getConfig();
        String url = "jdbc:mysql://" + config.getString("database.mysql.host") +
                ":" + config.getInt("database.mysql.port") +
                "/" + config.getString("database.mysql.database");
        String username = config.getString("database.mysql.username");
        String password = config.getString("database.mysql.password");

        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to MySQL database");
        } catch (SQLException e) {
            System.out.println("Failed to connect to MySQL database: " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from MySQL database");
            }
        } catch (SQLException e) {
            System.out.println("Failed to disconnect from MySQL database: " + e.getMessage());
        }
    }

    @Override
    public void deleteCompany(int id) {
        String sql = "DELETE FROM companies WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("Company deleted from MySQL database");
        } catch (SQLException e) {
            System.out.println("Failed to delete company from MySQL database: " + e.getMessage());
        }
    }

    @Override
    public void updateCompany(Company company) {
        String sql = "UPDATE companies SET name = ?, owner = ?, employees = ?, balance = ?, salary = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, company.getName());
            statement.setString(2, company.getOwner().toString());
            statement.setString(3, serializeUUIDArray(company.getEmployees()));
            statement.setInt(4, company.getBalance());
            statement.setInt(5, company.getSalary());
            statement.setInt(6, company.getId());
            statement.executeUpdate();
            System.out.println("Company updated in MySQL database");
        } catch (SQLException e) {
            System.out.println("Failed to update company in MySQL database: " + e.getMessage());
        }
    }

    @Override
    public void createCompany(Company company) {
        String sql = "INSERT INTO companies (name, owner, employees, balance, salary) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, company.getName());
            statement.setString(2, company.getOwner().toString());
            statement.setString(3, serializeUUIDArray(company.getEmployees()));
            statement.setInt(4, company.getBalance());
            statement.setInt(5, company.getSalary());
            statement.executeUpdate();
            System.out.println("Company created in MySQL database");
        } catch (SQLException e) {
            System.out.println("Failed to create company in MySQL database: " + e.getMessage());
        }
    }

    @Override
    public void saveCompanies(List<Company> companies) {
        String query = "REPLACE INTO companies (id, name, owner, employees, balance, salary) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (Company company : companies) {
                statement.setInt(1, company.getId());
                statement.setString(2, company.getName());
                statement.setString(3, company.getOwner().toString());
                statement.setString(4, serializeUUIDArray(company.getEmployees()));
                statement.setInt(5, company.getBalance());
                statement.setInt(6, company.getSalary());
                statement.addBatch();
            }
            statement.executeBatch();
            System.out.println("Companies saved to MySQL database");
        } catch (SQLException e) {
            System.out.println("Failed to save companies to MySQL database: " + e.getMessage());
        }
    }

    @Override
    public Company retrieveCompany(int id) {
        String sql = "SELECT * FROM companies WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return createCompanyFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve company from MySQL database: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Company[] retrieveCompanies() {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT * FROM companies";

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                Company company = createCompanyFromResultSet(resultSet);
                companies.add(company);
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve companies from MySQL database: " + e.getMessage());
        }

        return companies.toArray(new Company[0]);
    }

    private Company createCompanyFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        UUID owner = UUID.fromString(resultSet.getString("owner"));
        UUID[] employees = deserializeUUIDArray(resultSet.getString("employees"));
        int balance = resultSet.getInt("balance");
        int salary = resultSet.getInt("salary");
        return Company.builder()
                .id(id)
                .name(name)
                .owner(owner)
                .employees(employees)
                .balance(balance)
                .salary(salary)
                .build();
    }

    private String serializeUUIDArray(UUID[] uuids) {
        StringBuilder builder = new StringBuilder();
        for (UUID uuid : uuids) {
            builder.append(uuid.toString()).append(",");
        }
        return builder.toString();
    }

    private UUID[] deserializeUUIDArray(String serializedUUIDs) {
        String[] uuidStrings = serializedUUIDs.split(",");
        UUID[] uuids = new UUID[uuidStrings.length];
        for (int i = 0; i < uuidStrings.length; i++) {
            uuids[i] = UUID.fromString(uuidStrings[i]);
        }
        return uuids;
    }
}
