package it.mikeslab.truecompanies.util.itemstack;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

@UtilityClass
public class ItemStackUtil {

    /*
    *   Returns a GUI filler ItemStack with the given material
    */
    public ItemStack getFillerFromMaterial(Material material) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(" ");
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    public ItemStack getPlayerHead(String playerName) {

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

        if(offlinePlayer.getName() == null) {
            return new ItemStack(Material.PLAYER_HEAD);
        }

        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setOwningPlayer(offlinePlayer);
        itemStack.setItemMeta(meta);
        return itemStack;
    }








}
