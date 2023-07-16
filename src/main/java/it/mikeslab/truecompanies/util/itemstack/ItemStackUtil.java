package it.mikeslab.truecompanies.util.itemstack;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import it.mikeslab.truecompanies.util.format.ChatColor;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class ItemStackUtil {
    private final LoadingCache<CustomHeadEnum, ItemStack> customHeadCache = Caffeine.newBuilder()
            .build(customHeadEnum -> getCustomHead(customHeadEnum.getValue()));


    public ItemStack createStack(Material material, String displayName, String... lore) {
        ItemStack itemStack = new ItemStack(material);
        return getItemStack(itemStack, displayName, lore);
    }


    public ItemStack createStack(CustomHeadEnum customHeadEnum, String displayName, String... lore) {
        ItemStack itemStack = customHeadCache.getIfPresent(customHeadEnum);
        return getItemStack(itemStack, displayName, lore);
    }

    public ItemStack createStack(OfflinePlayer offlinePlayer, String displayName, String... lore) {
        ItemStack itemStack = getPlayerHead(offlinePlayer);
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

    private ItemStack getPlayerHead(OfflinePlayer player) {
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setOwningPlayer(player);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private ItemStack getCustomHead(final String val) {
        final ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (!val.isEmpty()) {
            final SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
            final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
            gameProfile.getProperties().put("textures", new Property("textures", translateValue(val)));
            try {
                final Field profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, gameProfile);
            } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException ex3) {
                ex3.printStackTrace();
            }

            head.setItemMeta(skullMeta);
        }
        return head;
    }

    private String translateValue(String val) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/")
                .append(val)
                .append("\"}}}");
        return Base64.getEncoder().encodeToString(builder.toString().getBytes(StandardCharsets.UTF_8));
    }








}
