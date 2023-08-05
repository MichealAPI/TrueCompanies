package it.mikeslab.truecompanies.menu.selector;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.loader.CompanyLoader;
import it.mikeslab.truecompanies.object.Company;
import it.mikeslab.truecompanies.object.Group;
import it.mikeslab.truecompanies.util.format.ChatColor;
import it.mikeslab.truecompanies.util.language.LangKey;
import it.mikeslab.truecompanies.util.language.Language;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CompanySelectorMenu {

    private final TrueCompanies instance;
    private final CompanyLoader companyLoader;
    private final String[] setup = {
            "         ",
            "abbbbbbbc",
            "         ",
    };


    public CompanySelectorMenu(TrueCompanies instance) {
        this.instance = instance;
        this.companyLoader = instance.getCompanyLoader();
    }


    public CompletableFuture<Company> show(Player target) {
        CompletableFuture<Company> result = new CompletableFuture<>();
        InventoryGui gui = new InventoryGui(instance, Language.getString(LangKey.SELECT_A_COMPANY, false), setup);

        GuiElementGroup group = new GuiElementGroup('b');

        for(Company company : companyLoader.getCompanies(target.getName())) {
            StaticGuiElement element = generateCompanyAssociatedElement(company, target);

            element.setAction(click -> {
                result.complete(company);
                return true;
            });

            group.addElement(element);
        }

        gui.addElement(group);


        // previous and next page
        gui.addElement(new GuiPageElement('a', new ItemStack(Material.REDSTONE), GuiPageElement.PageAction.PREVIOUS, Language.getString(LangKey.PREVIOUS_PAGE, false)));
        gui.addElement(new GuiPageElement('c', new ItemStack(Material.ARROW), GuiPageElement.PageAction.NEXT, Language.getString(LangKey.NEXT_PAGE, false)));

        gui.setCloseAction(close -> {
            result.complete(null);
            return false;
        });

        gui.show(target);

        return result;
    }


    private StaticGuiElement generateCompanyAssociatedElement(Company company, Player target) {

        int targetGroupID = company.getEmployees().get(target.getName());
        Group targetGroup = company.getGroups().get(targetGroupID);
        ItemStack companyItemStack = new ItemStack(Material.BOOK);
        ItemMeta meta = companyItemStack.getItemMeta();

        meta.setDisplayName(ChatColor.color(company.getDisplayName()));

        String yesString = Language.getString(LangKey.YES, false);
        String noString = Language.getString(LangKey.NO, false);

        List<String> lore = new ArrayList<>();
        lore.add(Language.getString(LangKey.CLICK_TO_SELECT_COMPANY, false));
        lore.add(" ");
        lore.add(Language.getString(LangKey.COMPANY_ID, false, Map.of("%id%", company.getId())));
        lore.add(Language.getString(LangKey.COMPANY_DESCRIPTION, false, Map.of("%description%", company.getDescription())));
        lore.add(Language.getString(LangKey.COMPANY_OWNER, false, Map.of("%owner%", company.getOwner())));
        lore.add(Language.getString(LangKey.COMPANY_BALANCE, false, Map.of("%balance%", company.getBalance() + "")));
        lore.add(Language.getString(LangKey.COMPANY_EMPLOYEES, false, Map.of("%employees%", company.getEmployees().size() + "")));
        lore.add(Language.getString(LangKey.YOUR_GROUP, false, Map.of("%group%", targetGroup.getTag())));
        lore.add(" ");
        lore.add(Language.getString(LangKey.PERMISSIONS, false));
        lore.add(Language.getString(LangKey.CAN_WITHDRAW, false, Map.of("%can_withdraw%", targetGroup.canWithdraw ? yesString : noString)));
        lore.add(Language.getString(LangKey.CAN_DEPOSIT, false, Map.of("%can_deposit%", targetGroup.canDeposit ? yesString : noString)));
        lore.add(Language.getString(LangKey.CAN_HIRE, false, Map.of("%can_hire%", targetGroup.canHire ? yesString : noString)));
        lore.add(Language.getString(LangKey.CAN_FIRE, false, Map.of("%can_fire%", targetGroup.canFire ? yesString : noString)));
        lore.add(Language.getString(LangKey.CAN_DEMOTE, false, Map.of("%can_demote%", targetGroup.canDemote ? yesString : noString)));
        lore.add(Language.getString(LangKey.CAN_PROMOTE, false, Map.of("%can_promote%", targetGroup.canPromote ? yesString : noString)));

        lore.add(" ");

        meta.setLore(lore);
        companyItemStack.setItemMeta(meta);

        return new StaticGuiElement('b', companyItemStack);
    }
}








