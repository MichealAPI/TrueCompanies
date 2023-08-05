package it.mikeslab.truecompanies.listener;

import it.mikeslab.truecompanies.Perms;
import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.command.subcommand.SubCompanyChat;
import it.mikeslab.truecompanies.object.Company;
import it.mikeslab.truecompanies.object.Group;
import it.mikeslab.truecompanies.util.format.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final TrueCompanies instance;

    public ChatListener(TrueCompanies instance) {
        this.instance = instance;
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String playerName = event.getPlayer().getName();

        if(!SubCompanyChat.getInChat().containsKey(playerName)) return;

        String companyID = SubCompanyChat.getInChat().get(playerName);
        Company company = instance.getCompanyLoader().getCompany(companyID);

        int subjectGroupID = company.getEmployees().get(playerName);
        Group subjectGroup = company.getGroups().get(subjectGroupID);

        event.setCancelled(true);

        String baseFormat = company.getChatFormat();
        baseFormat = baseFormat
                .replace("{name}", playerName)
                .replace("{group}", subjectGroup.getTag())
                .replace("{message}", event.getMessage());


        for(Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if(onlinePlayer.hasPermission(Perms.SPY_CHAT) || company.getEmployees().containsKey(onlinePlayer.getName())) {
                onlinePlayer.sendMessage(ChatColor.color(baseFormat));
            }
        }

    }



}
