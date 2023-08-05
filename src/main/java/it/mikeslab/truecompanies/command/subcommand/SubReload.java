package it.mikeslab.truecompanies.command.subcommand;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import it.mikeslab.truecompanies.Perms;
import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.util.language.LangKey;
import it.mikeslab.truecompanies.util.language.Language;
import org.bukkit.entity.Player;

@CommandAlias("company")
public class SubReload {

    private final TrueCompanies instance;

    public SubReload(TrueCompanies instance) {
        this.instance = instance;
    }


    @Subcommand("reload")
    @CommandPermission(Perms.RELOAD)
    public void onReloadCommand(Player player) {

        player.sendMessage(Language.getString(LangKey.PLUGIN_RELOADING, true));
        try {

            Language.reload();
            instance.reloadConfig();

        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(Language.getString(LangKey.PLUGIN_RELOAD_ERROR, true));
        }

        player.sendMessage(Language.getString(LangKey.PLUGIN_RELOADED, true));
    }



}
