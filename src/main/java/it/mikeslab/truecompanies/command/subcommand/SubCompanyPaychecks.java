package it.mikeslab.truecompanies.command.subcommand;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.menu.selector.CompanySelectorMenu;
import it.mikeslab.truecompanies.menu.selector.PlayerSelectorMenu;
import it.mikeslab.truecompanies.object.Group;
import it.mikeslab.truecompanies.util.CompanyUtils;
import it.mikeslab.truecompanies.util.language.LangKey;
import it.mikeslab.truecompanies.util.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

@CommandAlias("company")
public class SubCompanyPaychecks extends BaseCommand {

    private final TrueCompanies instance;

    public SubCompanyPaychecks(TrueCompanies instance) {
        this.instance = instance;
    }


    @Subcommand("givepaycheck")
    @Syntax("<amount>")
    public void onPaychecksCommand(Player player, double amount) {

        new CompanySelectorMenu(instance).show(player).thenAccept(company -> {

            if(company == null) {
                return;
            }

            int groupID = company.getEmployees().get(player.getName());
            Group group = company.getGroups().get(groupID);

            if(!group.canPaychecks) {
                player.sendMessage(Language.getString(LangKey.CANNOT_GIVE_PAYCHECKS, true));
                player.closeInventory();
                return;
            }


            new PlayerSelectorMenu(instance).show(
                    player,
                    Language.getString(LangKey.SELECT_A_PLAYER_MANAGE, false),
                    Optional.of(company),
                    Optional.of(false)
            ).thenAccept(employee -> {

                if(employee == null) {
                    return;
                }


                CompanyUtils companyUtils = instance.getCompanyUtils();
                companyUtils.givePaycheck(company, amount, player, Bukkit.getOfflinePlayer(employee));
            });

        });



    }



}
