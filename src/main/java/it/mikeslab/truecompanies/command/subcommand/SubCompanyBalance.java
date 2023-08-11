package it.mikeslab.truecompanies.command.subcommand;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import it.mikeslab.truecompanies.Perms;
import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.util.CompanyUtils;
import it.mikeslab.truecompanies.menu.selector.CompanySelectorMenu;
import it.mikeslab.truecompanies.object.Company;
import it.mikeslab.truecompanies.object.Group;
import it.mikeslab.truecompanies.util.language.LangKey;
import it.mikeslab.truecompanies.util.language.Language;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Map;

@CommandAlias("azienda|company")
@RequiredArgsConstructor
public class SubCompanyBalance extends BaseCommand {

    private final TrueCompanies instance;

    @Subcommand("deposita|deposit")
    @Syntax("<amount>")
    @Description("Deposit money into a company's balance")
    public void onDepositCommand(Player player, int amount) {
        new CompanySelectorMenu(instance).show(player).thenAccept(company -> {

            player.closeInventory();

            if (company == null) {
                return;
            }

            Group playerGroup = getGroup(company, player.getName());
            if (!playerGroup.canDeposit) {
                player.sendMessage(Language.getString(LangKey.COMPANY_DEPOSIT_NO_PERMS, true));
                return;
            }

            // Assuming you can check the player's balance and withdraw money from them
            if (!TrueCompanies.getEcon().has(player, amount)) {
                player.sendMessage(Language.getString(LangKey.DEPOSIT_NOT_ENOUGH_MONEY, true));
                return;
            }



            CompanyUtils companyUtils = instance.getCompanyUtils();
            companyUtils.updateBalance(company, amount);
            TrueCompanies.getEcon().withdrawPlayer(player, amount); // Assuming this method exists
            player.sendMessage(Language.getString(LangKey.DEPOSITED_MONEY, true, Map.of(
                    "%amount%", amount + "",
                    "%company%", company.getDisplayName())));
        });
    }

    @Subcommand("preleva|withdraw")
    @Syntax("<amount>")
    @Description("Withdraw money from a company's balance")
    public void onWithdrawCommand(Player player, int amount) {
        new CompanySelectorMenu(instance).show(player).thenAccept(company -> {

            player.closeInventory();

            if (company == null) {
                return;
            }

            Group playerGroup = getGroup(company, player.getName());
            if (!playerGroup.canWithdraw) {
                player.sendMessage(Language.getString(LangKey.COMPANY_WITHDRAW_NO_PERMS, true));
                return;
            }

            if (company.getBalance() < amount) {
                player.sendMessage(Language.getString(LangKey.WITHDRAW_NOT_ENOUGH_MONEY, true));
                return;
            }

            player.closeInventory();

            CompanyUtils companyUtils = instance.getCompanyUtils();
            companyUtils.updateBalance(company, -amount);
            TrueCompanies.getEcon().depositPlayer(player, amount); // Assuming this method exists
            player.sendMessage(Language.getString(LangKey.WITHDREW_MONEY, true, Map.of(
                    "%amount%", amount + "",
                    "%company%", company.getDisplayName())));
        });
    }

    @Subcommand("bilancio|bilancio")
    @Description("View a company's balance")
    public void onBalanceCommand(Player player) {
        new CompanySelectorMenu(instance).show(player).thenAccept(company -> {
            if (company == null) {
                player.closeInventory();
                return;
            }

            if(!company.getEmployees().containsKey(player.getName()) && !player.hasPermission(Perms.ADMIN)) {
                player.closeInventory();
                player.sendMessage(Language.getString(LangKey.NOT_EMPLOYED_HERE, true));
                return;
            }

            player.closeInventory();
            player.sendMessage(Language.getString(LangKey.COMPANY_BALANCE, true, Map.of(
                    "%company%", company.getDisplayName(),
                    "%balance%", company.getBalance() + "")));
        });
    }

    private Group getGroup(Company company, String playerName) {
        int groupId = company.getEmployees().get(playerName);
        return company.getGroups().get(groupId);
    }
}
