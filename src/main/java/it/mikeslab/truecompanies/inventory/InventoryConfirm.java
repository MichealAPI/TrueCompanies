package it.mikeslab.truecompanies.inventory;

import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.util.itemstack.ItemStackUtil;
import it.mikeslab.truecompanies.util.language.LangKey;
import it.mikeslab.truecompanies.util.language.Language;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class InventoryConfirm {

    private final String[] setup = {
            "         ",
            "   a b   ",
            "         ",
    };


    public CompletableFuture<Boolean> show(Player player) {
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        InventoryGui inventoryGui = new InventoryGui(TrueCompanies.getInstance(), Language.getString(LangKey.CONFIRM_MENU), setup);

        inventoryGui.setFiller(ItemStackUtil.getFiller(Material.GRAY_STAINED_GLASS_PANE));

        inventoryGui.addElement(new StaticGuiElement('a', ItemStackUtil.createStack(
                Material.LIME_WOOL,
                Language.getString(LangKey.CONFIRM))
                , click -> {
            click.getGui().close();
            completableFuture.complete(true);
            return true;
        }));

        inventoryGui.addElement(new StaticGuiElement('b', ItemStackUtil.createStack(
                Material.RED_WOOL,
                Language.getString(LangKey.CANCEL))
                , click -> {
            click.getGui().close();
            completableFuture.complete(false);
            return true;
        }));

        inventoryGui.setCloseAction(close -> {
            completableFuture.complete(false);
            return true;
        });

        inventoryGui.show(player);
        return completableFuture;
    }


}
