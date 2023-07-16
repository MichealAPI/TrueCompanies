package it.mikeslab.truecompanies.util.database.handler;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.object.Company;
import it.mikeslab.truecompanies.object.Employee;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a MySQL database handler for the TrueCompanies plugin.
 */
public class MySQLDatabase implements Database {
    private final String host;
    private final String password;
    private final String database;
    private final String username;
    private final int port;

    private HikariDataSource dataSource;
    private Logger logger;

    /**
     * Constructs a MySQLDatabase instance.
     */
    public MySQLDatabase() {
        JavaPlugin instance = TrueCompanies.getInstance();
        ConfigurationSection section = instance.getConfig().getConfigurationSection("database.mysql");

        this.host = section.getString("host");
        this.password = section.getString("password");
        this.database = section.getString("database");
        this.username = section.getString("username");
        this.port = section.getInt("port");

        this.logger = instance.getLogger();
    }

    /**
     * Connects to the MySQL database and initializes the required tables.
     */
    @Override
    public void connect() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database);
        config.setUsername(this.username);
        config.setPassword(this.password);

        this.dataSource = new HikariDataSource(config);

        initializeTables();
    }

    /**
     * Disconnects from the MySQL database and releases resources.
     */
    @Override
    public void disconnect() {
        CompanyCache companyCache = TrueCompanies.getInstance().getCompanyCache();
        saveCompanies(companyCache.getAllCompanies());

        this.dataSource.close();
        this.dataSource = null;
    }

    /**
     * Deletes a company with the specified ID from the database.
     *
     * @param id The ID of the company to delete.
     */
    @Override
    public void deleteCompany(int id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM companies WHERE id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to delete company with ID " + id, e);
        }
    }

    /**
     * Updates the data of a company in the database.
     *
     * @param company The company object to update.
     */
    @Override
    public void updateCompany(Company company) {
        try (Connection connection = dataSource.getConnection()) {
            updateCompanyData(connection, company);
            updateEmployees(connection, company);
            removeFiredEmployees(connection, company);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to update company with ID " + company.getId(), e);
        }
    }

    /**
     * Creates a new company in the database.
     *
     * @param company The company object to create.
     */
    @Override
    public void createCompany(Company company) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO companies (name, owner, balance) VALUES (?, ?, ?)")) {
            statement.setString(1, company.getName());
            statement.setString(2, company.getOwnerUUID().toString());
            statement.setInt(3, company.getBalance());
            statement.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to create company " + company.getName(), e);
        }
    }

    /**
     * Saves a list of companies to the database.
     *
     * @param companies The list of companies to save.
     */
    @Override
    public void saveCompanies(List<Company> companies) {
        try (Connection connection = dataSource.getConnection()) {
            for (Company company : companies) {
                updateCompanyData(connection, company);
                updateEmployees(connection, company);
                removeFiredEmployees(connection, company);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to save companies", e);
        }
    }


    /**
     * Retrieves all companies from the database.
     *
     * @return An array of Company objects.
     */
    @Override
    public List<Company> retrieveCompanies() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM companies");
             ResultSet resultSet = statement.executeQuery()) {
            List<Company> companies = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                Company company = fetchCompanyData(connection, id);
                if (company != null) {
                    companies.add(company);
                }
            }
            return companies;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to retrieve companies", e);
        }
        return null;
    }

    /**
     * Initializes the required tables in the MySQL database.
     */
    private void initializeTables() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            String companyTable = "CREATE TABLE IF NOT EXISTS companies (" +
                    "id INT NOT NULL AUTO_INCREMENT," +
                    "name VARCHAR(255) NOT NULL," +
                    "owner VARCHAR(255) NOT NULL," +
                    "balance INT NOT NULL," +
                    "PRIMARY KEY (id)" +
                    ");";

            String employeesTable = "CREATE TABLE IF NOT EXISTS employees (" +
                    "id INT NOT NULL AUTO_INCREMENT," +
                    "company INT NOT NULL," +
                    "uuid VARCHAR(255) NOT NULL," +
                    "canFire BOOLEAN NOT NULL," +
                    "canHire BOOLEAN NOT NULL," +
                    "canWithdraw BOOLEAN NOT NULL," +
                    "canDeposit BOOLEAN NOT NULL," +
                    "PRIMARY KEY (id)" +
                    ");";

            statement.execute(companyTable);
            statement.execute(employeesTable);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to initialize database tables", e);
        }
    }

    /**
     * Updates the company data in the database.
     *
     * @param connection The database connection.
     * @param company    The company object to update.
     * @throws SQLException If an SQL error occurs.
     */
    private void updateCompanyData(Connection connection, Company company) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE companies SET name = ?, owner = ?, balance = ? WHERE id = ?")) {
            statement.setString(1, company.getName());
            statement.setString(2, company.getOwnerUUID().toString());
            statement.setInt(3, company.getBalance());
            statement.setInt(4, company.getId());
            statement.executeUpdate();
        }
    }

    /**
     * Updates the employees of a company in the database.
     *
     * @param connection The database connection.
     * @param company    The company object.
     * @throws SQLException If an SQL error occurs.
     */
    private void updateEmployees(Connection connection, Company company) throws SQLException {
        try (PreparedStatement selectStatement = connection.prepareStatement("SELECT COUNT(*) FROM employees WHERE playerUUID = ?")) {
            try (PreparedStatement updateStatement = connection.prepareStatement("UPDATE employees SET companyID = ?, salary = ?, canFire = ?, canHire = ?, canWithdraw = ?, canDeposit = ? WHERE playerUUID = ? AND companyID = ?")) {
                try (PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO employees (companyID, playerUUID, salary, canFire, canHire, canWithdraw, canDeposit) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    for (Employee employee : company.getEmployees()) {
                        selectStatement.setString(1, employee.getPlayerUUID().toString());
                        ResultSet resultSet = selectStatement.executeQuery();
                        if (resultSet.next() && resultSet.getInt(1) > 0) {
                            updateStatement.setInt(1, employee.getCompanyID());
                            updateStatement.setInt(2, employee.getSalary());
                            updateStatement.setBoolean(3, employee.canFire);
                            updateStatement.setBoolean(4, employee.canHire);
                            updateStatement.setBoolean(5, employee.canWithdraw);
                            updateStatement.setBoolean(6, employee.canDeposit);
                            updateStatement.setString(7, employee.getPlayerUUID().toString());
                            updateStatement.setInt(8, employee.getCompanyID());
                            updateStatement.executeUpdate();
                        } else {
                            insertStatement.setInt(1, employee.getCompanyID());
                            insertStatement.setString(2, employee.getPlayerUUID().toString());
                            insertStatement.setInt(3, employee.getSalary());
                            insertStatement.setBoolean(4, employee.canFire);
                            insertStatement.setBoolean(5, employee.canHire);
                            insertStatement.setBoolean(6, employee.canWithdraw);
                            insertStatement.setBoolean(7, employee.canDeposit);
                            insertStatement.executeUpdate();
                        }
                    }
                }
            }
        }
    }

    /**
     * Removes the fired employees of a company from the database.
     *
     * @param connection The database connection.
     * @param company    The company object.
     * @throws SQLException If an SQL error occurs.
     */
    private void removeFiredEmployees(Connection connection, Company company) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM employees WHERE playerUUID = ? AND companyID = ?")) {
            for (UUID firedEmployee : company.getFiredEmployees()) {
                statement.setString(1, firedEmployee.toString());
                statement.setInt(2, company.getId());
                statement.executeUpdate();
            }
        }
    }

    /**
     * Fetches the data of a company from the database.
     *
     * @param connection The database connection.
     * @param id         The ID of the company.
     * @return The retrieved Company object, or null if not found.
     * @throws SQLException If an SQL error occurs.
     */
    private Company fetchCompanyData(Connection connection, int id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM companies WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("name");
                    UUID ownerUUID = UUID.fromString(resultSet.getString("owner"));
                    int balance = resultSet.getInt("balance");

                    List<Employee> employees = fetchEmployees(connection, id);

                    return Company.builder()
                            .id(id)
                            .name(name)
                            .ownerUUID(ownerUUID)
                            .balance(balance)
                            .employees(employees)
                            .build();
                }
            }
        }
        return null;
    }

    /**
     * Fetches the employees of a company from the database.
     *
     * @param connection The database connection.
     * @param companyId  The ID of the company.
     * @return A list of Employee objects.
     * @throws SQLException If an SQL error occurs.
     */
    private List<Employee> fetchEmployees(Connection connection, int companyId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM employees WHERE company = ?")) {
            statement.setInt(1, companyId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Employee> employees = new ArrayList<>();
                while (resultSet.next()) {
                    UUID playerUUID = UUID.fromString(resultSet.getString("uuid"));
                    int employeeCompanyId = resultSet.getInt("company");
                    int salary = resultSet.getInt("salary");
                    boolean canFire = resultSet.getBoolean("canFire");
                    boolean canHire = resultSet.getBoolean("canHire");
                    boolean canWithdraw = resultSet.getBoolean("canWithdraw");
                    boolean canDeposit = resultSet.getBoolean("canDeposit");

                    Employee employee = new Employee(playerUUID, employeeCompanyId, salary);
                    employee.setCanFire(canFire);
                    employee.setCanHire(canHire);
                    employee.setCanWithdraw(canWithdraw);
                    employee.setCanDeposit(canDeposit);

                    employees.add(employee);
                }
                return employees;
            }
        }
    }
}
