package it.mikeslab.truecompanies.command.subcommand;

import co.aikar.commands.annotation.*;
import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.api.event.CompanyFireEvent;
import it.mikeslab.truecompanies.inventory.InventoryConfirm;
import it.mikeslab.truecompanies.object.Company;
import it.mikeslab.truecompanies.object.Employee;
import it.mikeslab.truecompanies.util.database.handler.CompanyCache;
import it.mikeslab.truecompanies.util.language.LangKey;
import it.mikeslab.truecompanies.util.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;

@CommandAlias("company")
public class SubCompanyFire {

    private final TrueCompanies plugin;

    public SubCompanyFire(TrueCompanies plugin) {
        this.plugin = plugin;
    }

    @Subcommand("fire")
    @CommandCompletion("@players @companies")
    @Syntax("<employee> <companyID>")
    @Description("Fire an employee from a company")
    public void onFireCommand(Player player, OfflinePlayer target, int companyID) {
        CompanyCache companyCache = plugin.getCompanyCache();
        Company company = companyCache.getCompany(companyID);

        if (company == null) {
            player.sendMessage(Language.getString(LangKey.COMPANY_NOT_FOUND, Map.of("{companyID}", String.valueOf(companyID))));
            return;
        }

        Employee employer = companyCache.getEmployee(company, player.getUniqueId());
        Employee employee = companyCache.getEmployee(company, target.getUniqueId());

        if (employer == null) {
            player.sendMessage(Language.getString(LangKey.SUBJECT_NOT_EMPLOYED, Map.of(
                    "{player}", player.getName(),
                    "{company}", company.getName())));
            return;
        }

        if (employee == null) {
            player.sendMessage(Language.getString(LangKey.TARGET_NOT_EMPLOYED, Map.of(
                    "{player}", target.getName(),
                    "{company}", company.getName())));
            return;
        }

        if (!canFireEmployee(employer, employee)) {
            player.sendMessage(Language.getString(LangKey.YOU_DO_NOT_HAVE_PERMISSION, Map.of(
                    "{player}", player.getName(),
                    "{company}", company.getName())));
            return;
        }

        InventoryConfirm confirmMenu = new InventoryConfirm();
        confirmMenu.show(player).thenAccept(result -> {
            if (result) {
                fireEmployee(company, employee, target.getName(), player);
            }
        });
    }

    private boolean canFireEmployee(Employee employer, Employee employee) {
        return (employee.canFire && employer.getPlayerUUID() != employee.getPlayerUUID())
                || employer.canFire;
    }

    private void fireEmployee(Company company, Employee employee, String employeeName, Player player) {
        CompanyFireEvent event = new CompanyFireEvent(player, employee.getPlayerUUID(), company.getId());
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        company.fireEmployee(employee.getPlayerUUID());
        player.sendMessage(Language.getString(LangKey.FIRED_SUCCESSFULLY, Map.of(
                "{player}", employeeName,
                "{company}", company.getName())));
    }
}
