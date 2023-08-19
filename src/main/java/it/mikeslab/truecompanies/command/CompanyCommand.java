package it.mikeslab.truecompanies.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.HelpCommand;
import it.mikeslab.truecompanies.util.format.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("azienda")
public class CompanyCommand extends BaseCommand {

    @Default
    public void onCommand(Player sender) {
        sender.sendMessage(ChatColor.color("&aPlug-in made by MikesLab"));
        sender.sendMessage(ChatColor.color("&6Use /company help for a list of commands"));
    }

    @HelpCommand
    @CommandAlias("help")
    public void onHelpCommand(CommandHelp commandHelp) {
        commandHelp.showHelp();
    }



}
