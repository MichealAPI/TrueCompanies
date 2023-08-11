package it.mikeslab.truecompanies.util.itemstack;

import it.mikeslab.truecompanies.util.format.ChatColor;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ItemStackUtil {

    public ItemStack createStack(Material material, String displayName, String... lore) {
        ItemStack itemStack = new ItemStack(material);
        return getItemStack(itemStack, displayName, lore);
    }

    private ItemStack getItemStack(ItemStack itemStack, String displayName, String[] lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(ChatColor.color(displayName));
        List<String> loreList = new ArrayList<>();

        for(String line : lore) {
            loreList.add(ChatColor.color(line));
        }

        itemMeta.setLore(loreList);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


    public ItemStack getFiller(Material material) {
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
