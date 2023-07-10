package it.mikeslab.truecompanies;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.MessageType;
import it.mikeslab.truecompanies.util.database.DatabaseType;
import it.mikeslab.truecompanies.util.database.handler.CompanyCache;
import it.mikeslab.truecompanies.util.database.handler.Database;
import it.mikeslab.truecompanies.util.database.handler.MongoDBAccess;
import it.mikeslab.truecompanies.util.database.handler.MySQLAccess;
import it.mikeslab.truecompanies.util.language.Language;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class TrueCompanies extends JavaPlugin {

    @Getter private static TrueCompanies instance;
    private Database database;
    private CompanyCache companyCache;



    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        setupLanguages();
        setupCommandFramework();

        setupDatabase();
    }

    @Override
    public void onDisable() {

    }


    private void setupCommandFramework() {
        BukkitCommandManager commandManager = new BukkitCommandManager(this);
        //commandManager.registerCommand(new CmdATM());

        commandManager.enableUnstableAPI("help");
        commandManager.setFormat(MessageType.HELP, ChatColor.GREEN, ChatColor.GOLD);
    }


    private void setupLanguages() {
        Language.initialize(this, getConfig().getString("language"));
    }

    private void setupDatabase() {
        DatabaseType requestedDatabase = DatabaseType.valueOf(getConfig().getString("database.type"));


        switch (requestedDatabase) {
            case MONGO:
                database = new MongoDBAccess();
                break;
            case MYSQL:
                database = new MySQLAccess();
                break;
        }

        database.connect();

        loadCache();
    }

    private void loadCache() {
        this.companyCache = new CompanyCache();
    }

}
