package it.mikeslab.truecompanies.command.subcommand;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.menu.perms.CompanyPermsInventory;
import it.mikeslab.truecompanies.menu.selector.CompanySelectorMenu;
import it.mikeslab.truecompanies.menu.selector.GroupSelectorMenu;
import it.mikeslab.truecompanies.object.Company;
import it.mikeslab.truecompanies.object.Group;
import it.mikeslab.truecompanies.util.language.LangKey;
import it.mikeslab.truecompanies.util.language.Language;
import org.bukkit.entity.Player;

import java.util.Map;

@CommandAlias("azienda|company")
public class SubCompanyPerms extends BaseCommand {

    private final TrueCompanies instance;

    public SubCompanyPerms(TrueCompanies instance) {
        this.instance = instance;
    }


    @Subcommand("permessi|perms|permissions")
    public void onPermsCommand(Player player) {
        new CompanySelectorMenu(instance).show(player).thenAccept(company -> {

            if (company == null) {
                return;
            }
            
            if(!player.getName().equals(company.getOwner())) {
                player.sendMessage(Language.getString(LangKey.NOT_OWNER, true));
                return;
            }
            
            new GroupSelectorMenu(instance).show(player, company).thenAccept(group -> {
                
                if(group == null) {
                    return;
                }

                new CompanyPermsInventory(instance).show(player, group,
                        Language.getString(
                                LangKey.EDIT_PERMS_MENU,
                                false,
                                Map.of("%group%", group.getTag())
                        ), company);
            });
            
        });
    }

}
