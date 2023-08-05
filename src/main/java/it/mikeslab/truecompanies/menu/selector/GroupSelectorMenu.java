package it.mikeslab.truecompanies.menu.selector;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.object.Company;
import it.mikeslab.truecompanies.object.Group;
import it.mikeslab.truecompanies.util.language.LangKey;
import it.mikeslab.truecompanies.util.language.Language;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class GroupSelectorMenu {

    private final TrueCompanies instance;
    private final String[] setup = {
            "         ",
            "abbbbbbbc",
            "         ",
    };

    public GroupSelectorMenu(TrueCompanies instance) {
        this.instance = instance;
    }

    public CompletableFuture<Group> show(Player target, Company company) {
        CompletableFuture<Group> result = new CompletableFuture<>();
        InventoryGui gui = new InventoryGui(instance, Language.getString(LangKey.SELECT_A_GROUP, false), setup);

        GuiElementGroup elementGroup = new GuiElementGroup('b');
        List<Group> groups = queryGroups(target, company);

        for(Group group : groups) {
            StaticGuiElement element = generateGroupAssociatedElement(group);

            element.setAction(click -> {
                result.complete(group);
                return true;
            });

            elementGroup.addElement(element);
        }

        gui.addElement(elementGroup);

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


    public StaticGuiElement generateGroupAssociatedElement(Group group) {
        ItemStack groupItemStack = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta itemMeta = groupItemStack.getItemMeta();

        itemMeta.setDisplayName(ChatColor.GOLD + group.getTag());

        String yesString = Language.getString(LangKey.YES, false);
        String noString = Language.getString(LangKey.NO, false);

        List<String> lore = new ArrayList<>();

        lore.add(Language.getString(LangKey.PERMISSIONS, false));
        lore.add(Language.getString(LangKey.CAN_WITHDRAW, false, Map.of("%can_withdraw%", group.canWithdraw ? yesString : noString)));
        lore.add(Language.getString(LangKey.CAN_DEPOSIT, false, Map.of("%can_deposit%", group.canDeposit ? yesString : noString)));
        lore.add(Language.getString(LangKey.CAN_HIRE, false, Map.of("%can_hire%", group.canHire ? yesString : noString)));
        lore.add(Language.getString(LangKey.CAN_FIRE, false, Map.of("%can_fire%", group.canFire ? yesString : noString)));
        lore.add(Language.getString(LangKey.CAN_DEMOTE, false, Map.of("%can_demote%", group.canDemote ? yesString : noString)));
        lore.add(Language.getString(LangKey.CAN_PROMOTE, false, Map.of("%can_promote%", group.canPromote ? yesString : noString)));
        lore.add(" ");

        itemMeta.setLore(lore);
        groupItemStack.setItemMeta(itemMeta);

        return new StaticGuiElement('b', groupItemStack);
    }


    public List<Group> queryGroups(Player target, Company company) {
        Map<Integer, Group> groups = company.getGroups();

        int targetGroupLevel = company.getEmployees().get(target.getName());
        List<Group> queriedGroups = new ArrayList<>();

        for(Map.Entry<Integer, Group> group : groups.entrySet()) {

            if(group.getKey() <= targetGroupLevel) continue;

            queriedGroups.add(group.getValue());
        }

        return queriedGroups;
    }



}

