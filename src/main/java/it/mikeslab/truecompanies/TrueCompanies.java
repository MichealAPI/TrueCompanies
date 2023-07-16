package it.mikeslab.truecompanies;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.MessageType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import it.mikeslab.truecompanies.util.database.DatabaseType;
import it.mikeslab.truecompanies.util.database.handler.CompanyCache;
import it.mikeslab.truecompanies.util.database.handler.Database;
import it.mikeslab.truecompanies.util.database.handler.JSONDatabase;
import it.mikeslab.truecompanies.util.database.handler.MySQLDatabase;
import it.mikeslab.truecompanies.util.language.Language;
import it.mikeslab.truecompanies.util.error.SentryDiagnostic;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class TrueCompanies extends JavaPlugin {

    @Getter private static TrueCompanies instance;
    private ProtocolManager protocolManager;
    private Database database;
    private CompanyCache companyCache;


    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;

        if(getConfig().getBoolean("send-stacktraces-to-sentry")) {
            SentryDiagnostic.initSentry();
        }

        setupLanguages();
        setupCommandFramework();

        setupDatabase();
        database.connect();

        if (isProtocolLibEnabled())
            protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onDisable() {
        database.disconnect();
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
            case JSON -> database = new JSONDatabase();
            case MYSQL -> database = new MySQLDatabase();
        }

        database.connect();

        loadCache();
    }

    private void loadCache() {
        this.companyCache = new CompanyCache();
    }


    public boolean isProtocolLibEnabled() {
        return getServer().getPluginManager().isPluginEnabled("ProtocolLib");
    }
}
