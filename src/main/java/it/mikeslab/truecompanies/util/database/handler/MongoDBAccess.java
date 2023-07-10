package it.mikeslab.truecompanies.util.database.handler;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.object.Company;
import org.bson.Document;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MongoDBAccess implements Database {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    @Override
    public void connect() {
        FileConfiguration config = TrueCompanies.getInstance().getConfig();
        String connectionString = config.getString("database.mongodb.connection-string");
        String databaseName = config.getString("database.mongodb.database");
        String collectionName = config.getString("database.mongodb.collection");

        try {
            MongoClientURI uri = new MongoClientURI(connectionString);
            mongoClient = new MongoClient(uri);
            database = mongoClient.getDatabase(databaseName);
            collection = database.getCollection(collectionName);
            System.out.println("Connected to MongoDB database");
        } catch (Exception e) {
            System.out.println("Failed to connect to MongoDB database: " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        try {
            if (mongoClient != null) {
                mongoClient.close();
                System.out.println("Disconnected from MongoDB database");
            }
        } catch (Exception e) {
            System.out.println("Failed to disconnect from MongoDB database: " + e.getMessage());
        }
    }

    @Override
    public void deleteCompany(int id) {
        Document query = new Document("_id", id);

        try {
            collection.deleteOne(query);
            System.out.println("Company deleted from MongoDB database");
        } catch (Exception e) {
            System.out.println("Failed to delete company from MongoDB database: " + e.getMessage());
        }
    }

    @Override
    public void updateCompany(Company company) {
        Document query = new Document("_id", company.getId());
        Document update = new Document("$set", createDocumentFromCompany(company));

        try {
            collection.updateOne(query, update);
            System.out.println("Company updated in MongoDB database");
        } catch (Exception e) {
            System.out.println("Failed to update company in MongoDB database: " + e.getMessage());
        }
    }

    @Override
    public void createCompany(Company company) {
        Document doc = createDocumentFromCompany(company);

        try {
            collection.insertOne(doc);
            System.out.println("Company created in MongoDB database");
        } catch (Exception e) {
            System.out.println("Failed to create company in MongoDB database: " + e.getMessage());
        }
    }

    @Override
    public void saveCompanies(List<Company> companies) {
        List<Document> documents = new ArrayList<>();

        for (Company company : companies) {
            Document doc = createDocumentFromCompany(company);
            documents.add(doc);
        }

        try {
            collection.insertMany(documents);
            System.out.println("Companies saved to MongoDB database");
        } catch (Exception e) {
            System.out.println("Failed to save companies to MongoDB database: " + e.getMessage());
        }
    }

    @Override
    public Company retrieveCompany(int id) {
        Document query = new Document("_id", id);

        try {
            Document doc = collection.find(query).first();
            if (doc != null) {
                return createCompanyFromDocument(doc);
            }
        } catch (Exception e) {
            System.out.println("Failed to retrieve company from MongoDB database: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Company[] retrieveCompanies() {
        List<Company> companies = new ArrayList<>();

        try {
            for (Document doc : collection.find()) {
                Company company = createCompanyFromDocument(doc);
                companies.add(company);
            }
        } catch (Exception e) {
            System.out.println("Failed to retrieve companies from MongoDB database: " + e.getMessage());
        }

        return companies.toArray(new Company[0]);
    }

    private Company createCompanyFromDocument(Document doc) {
        int id = doc.getInteger("_id");
        String name = doc.getString("name");
        String ownerString = doc.getString("owner");
        UUID owner = UUID.fromString(ownerString);
        List<String> employeeStrings = doc.getList("employees", String.class);
        UUID[] employees = new UUID[employeeStrings.size()];
        for (int i = 0; i < employeeStrings.size(); i++) {
            employees[i] = UUID.fromString(employeeStrings.get(i));
        }
        int balance = doc.getInteger("balance");
        int salary = doc.getInteger("salary");

        return Company.builder()
                .id(id)
                .name(name)
                .owner(owner)
                .employees(employees)
                .balance(balance)
                .salary(salary)
                .build();
    }

    private Document createDocumentFromCompany(Company company) {
        return new Document("_id", company.getId())
                .append("name", company.getName())
                .append("owner", company.getOwner().toString())
                .append("employees", serializeUUIDArray(company.getEmployees()))
                .append("balance", company.getBalance())
                .append("salary", company.getSalary());
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
