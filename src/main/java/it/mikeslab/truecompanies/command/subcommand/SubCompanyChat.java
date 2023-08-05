package it.mikeslab.truecompanies.command.subcommand;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.util.format.ChatColor;
import it.mikeslab.truecompanies.util.language.LangKey;
import it.mikeslab.truecompanies.util.language.Language;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@CommandAlias("company")
public class SubCompanyChat extends BaseCommand {


    @Getter private static Map<String, String> inChat;
    private final TrueCompanies instance;


    public SubCompanyChat(TrueCompanies instance) {
        this.instance = instance;
        inChat = new HashMap<>();
    }

    @Subcommand("chat")
    @Syntax("<companyID>")
    public void onChatCommand(Player player, String companyID) {

        if(inChat.containsKey(player.getName())) {
            player.sendMessage(Language.getString(LangKey.YOU_ALREADY_IN_COMPANY_CHAT, true));
            return;
        }

        if(instance.getCompanyLoader().getCompany(companyID) == null) {
            player.sendMessage(Language.getString(LangKey.COMPANY_DOESNT_EXISTS, true));
            return;
        }

        inChat.put(player.getName(), companyID);

        String companyName = instance.getCompanyLoader().getCompany(companyID).getDisplayName();
        player.sendMessage(Language.getString(LangKey.JOINED_COMPANY_CHAT, true, Map.of("%company%", companyName)));
    }


    @Subcommand("leavechat")
    public void onLeaveChatCommand(Player player) {

        if(inChat.containsKey(player.getName())) {
            String companyName = instance.getCompanyLoader().getCompany(inChat.get(player.getName())).getDisplayName();
            inChat.remove(player.getName());
            player.sendMessage(Language.getString(LangKey.LEFT_COMPANY_CHAT, true, Map.of("%company%", companyName)));
            return;
        }

        player.sendMessage(Language.getString(LangKey.NOT_IN_COMPANY_CHAT, true));
    }





}
