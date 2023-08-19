package it.mikeslab.truecompanies;

import co.aikar.commands.BukkitCommandManager;
import it.mikeslab.truecompanies.command.CompanyCommand;
import it.mikeslab.truecompanies.command.subcommand.*;
import it.mikeslab.truecompanies.listener.ChatListener;
import it.mikeslab.truecompanies.loader.CompanyLoader;
import it.mikeslab.truecompanies.util.CompanyUtils;
import it.mikeslab.truecompanies.util.language.Language;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public final class TrueCompanies extends JavaPlugin {

    @Getter private static Economy econ = null;
    @Getter private static TrueCompanies instance;

    private FileConfiguration menuConfiguration;
    private CompanyLoader companyLoader;
    private CompanyUtils companyUtils;

    @Override
    public void onEnable() {

        if (!setupEconomy()) {
            Bukkit.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.loadConfigurationFiles();
        this.loadListeners();
        this.registerCommands();

        this.loadCompanies();

        // This Static instance is only in use inside the CompanyAPI class
        // to generate a default API instance
        instance = this;
    }



    private void loadConfigurationFiles() {
        saveDefaultConfig();

        Language.initialize(this, getConfig().getString("language"));

        // Inventories Language
        String inventoryLangPath = "inventories.yml";
        File inventoryLangFile = new File(getDataFolder(), inventoryLangPath);
        Language.generateFile(inventoryLangFile, "inventories.yml");

        this.menuConfiguration = YamlConfiguration.loadConfiguration(inventoryLangFile);
    }

    private void loadListeners() {
        this.getServer().getPluginManager().registerEvents(new ChatListener(this), this);
    }

    private void registerCommands() {
        BukkitCommandManager commandManager = new BukkitCommandManager(this);

        // Main command
        commandManager.registerCommand(new CompanyCommand());

        // Subs
        commandManager.registerCommand(new SubCompanyHire(this));
        commandManager.registerCommand(new SubCompanyFire(this));
        commandManager.registerCommand(new SubCompanyBalance(this));
        commandManager.registerCommand(new SubCompanyManage(this));
        commandManager.registerCommand(new SubCompanyChat(this));
        commandManager.registerCommand(new SubCompanyPerms(this));
        commandManager.registerCommand(new SubCompanyOwnership(this));
        commandManager.registerCommand(new SubCompanyPaychecks(this));

        commandManager.registerCommand(new SubReload(this));

        commandManager.enableUnstableAPI("help");
    }

    private void loadCompanies() {
        this.companyLoader = new CompanyLoader(this.getDataFolder());
        this.companyLoader.loadAllCompanies();

        this.companyUtils = new CompanyUtils(companyLoader);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
