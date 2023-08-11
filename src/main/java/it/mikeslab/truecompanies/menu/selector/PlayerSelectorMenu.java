package it.mikeslab.truecompanies.menu.selector;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.menu.element.CustomizableGuiElement;
import it.mikeslab.truecompanies.object.Company;
import it.mikeslab.truecompanies.util.error.SentryDiagnostic;
import it.mikeslab.truecompanies.util.format.ChatColor;
import it.mikeslab.truecompanies.util.itemstack.ItemStackUtil;
import it.mikeslab.truecompanies.util.language.LangKey;
import it.mikeslab.truecompanies.util.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class PlayerSelectorMenu {
    private final TrueCompanies instance;
    private final FileConfiguration menuConfiguration;
    private final String[] setup = {
            "         ",
            " aaaaaaa ",
            "baaaaaaa ",
            " aaaaaaa ",
            "   c d   "
    };

    // b = infobook
    // c = previous page
    // d = next page

    public PlayerSelectorMenu(TrueCompanies instance) {
        this.instance = instance;
        this.menuConfiguration = instance.getMenuConfiguration();
    }


    public CompletableFuture<String> show(Player target, String title, Optional<Company> optionalCompany, Optional<Boolean> exemptEmployees) {
        try {
            String inventoryID = "player-selector";

            Map<String, Integer> queriedPlayers = loadQueriedPlayers(target, optionalCompany, exemptEmployees);
            CompletableFuture<String> result = new CompletableFuture<>();

            InventoryGui gui = new InventoryGui(instance, title, setup);

            GuiElementGroup group = new GuiElementGroup('a');
                for (Map.Entry<String, Integer> entry : queriedPlayers.entrySet()) {
                    StaticGuiElement element = generatePlayerAssociatedElement(entry.getKey(), optionalCompany, exemptEmployees);

                    element.setAction(click -> {
                        click.getGui().close();
                        result.complete(entry.getKey());
                        return true;
                    });

                    group.addElement(element);
                }

                gui.addElement(group);


                // infoBook
                gui.addElement(new CustomizableGuiElement(menuConfiguration).create('b', inventoryID, "info-book"));

                // previous and next page
                gui.addElement(new GuiPageElement('c', new ItemStack(Material.REDSTONE), GuiPageElement.PageAction.PREVIOUS, Language.getString(LangKey.PREVIOUS_PAGE, false)));
                gui.addElement(new GuiPageElement('d', new ItemStack(Material.ARROW), GuiPageElement.PageAction.NEXT, Language.getString(LangKey.NEXT_PAGE, false)));


                gui.setCloseAction(close -> {
                    result.complete(null);
                    return false;
                });

                gui.show(target);
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                SentryDiagnostic.capture(e);
            }
        return CompletableFuture.completedFuture(null);
    }



    private StaticGuiElement generatePlayerAssociatedElement(String playerName, Optional<Company> optionalCompany, Optional<Boolean> exemptEmployees) {
        boolean useTags = optionalCompany.isPresent() && !exemptEmployees.orElse(false);

        ItemStack playerHead = ItemStackUtil.getPlayerHead(playerName);
        ItemMeta meta = playerHead.getItemMeta();

        if(useTags) {
            Company company = optionalCompany.get();
            int group = company.getEmployees().get(playerName);
            String tag = company.getGroups().get(group).getTag();
            meta.setDisplayName(ChatColor.color("&6" + playerName));

            List<String> loreLines = new ArrayList<>();
            loreLines.add(Language.getString(LangKey.GROUP, false, Map.of("%group%", tag)));

            meta.setLore(loreLines);
        } else {
            meta.setDisplayName(ChatColor.color("&6" + playerName));
        }

        List<String> lore = new ArrayList<>();
        lore.add(Language.getString(LangKey.SELECT_THIS_PLAYER, false));

        playerHead.setItemMeta(meta);

        return new StaticGuiElement('a', playerHead);
    }



    private Map<String, Integer> loadQueriedPlayers(Player target, Optional<Company> optionalCompany, Optional<Boolean> exemptEmployees) {
        Map<String, Integer> players = new HashMap<>();

        if (optionalCompany.isPresent()) {
            Company company = optionalCompany.get();

            if (exemptEmployees.orElse(false)) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (!onlinePlayer.getName().equals(target.getName()) && !company.getEmployees().containsKey(onlinePlayer.getName())) {
                        players.put(onlinePlayer.getName(), 0);
                    }
                }
            } else {
                // -1 is used for persons that are not in a group and have the permission to manage the company
                int subjectGroup = company.getEmployees().getOrDefault(target.getName(), -1);

                for (Map.Entry<String, Integer> employeeEntry : company.getEmployees().entrySet()) {
                    if (employeeEntry.getValue() > subjectGroup || subjectGroup == -1) {
                        players.put(employeeEntry.getKey(), employeeEntry.getValue());
                    }
                }
            }

        } else {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.equals(target)) {
                    players.put(onlinePlayer.getName(), 0);
                }
            }
        }

        return players;
    }
}
