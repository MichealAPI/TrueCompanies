package it.mikeslab.truecompanies.command.subcommand;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.menu.general.ConfirmMenu;
import it.mikeslab.truecompanies.menu.selector.CompanySelectorMenu;
import it.mikeslab.truecompanies.util.CompanyUtils;
import it.mikeslab.truecompanies.util.language.LangKey;
import it.mikeslab.truecompanies.util.language.Language;
import org.bukkit.entity.Player;

import java.util.Map;

@CommandAlias("company")
public class SubCompanyOwnership extends BaseCommand {

    private final TrueCompanies instance;

    public SubCompanyOwnership(TrueCompanies instance) {
        this.instance = instance;
    }


    @Subcommand("transferownership")
    @Syntax("<player>")
    public void onTransferOwnershipCommand(Player player, OnlinePlayer target) {

        new CompanySelectorMenu(instance).show(player).thenAccept(company -> {
            if (company == null) {
                player.closeInventory();
                return;
            }

            if (!company.getOwner().equals(player.getName())) {
                player.sendMessage(Language.getString(LangKey.NOT_OWNER, true));
                return;
            }

            new ConfirmMenu(instance).show(player, Language.getString(LangKey.TRANSFER_OWNERSHIP_MENU, false, Map.of(
                    "%player%", target.getPlayer().getName(),
                    "%company%", company.getDisplayName()
            ))).thenAccept(confirm -> {
                if (!confirm) {
                    player.sendMessage(Language.getString(LangKey.TRANSFER_OWNERSHIP_CANCELLED, true));
                    return;
                }

                CompanyUtils companyUtils = instance.getCompanyUtils();

                companyUtils.transferCompanyOwnership(company, target.getPlayer(), player);
            });



        });





    }



}
