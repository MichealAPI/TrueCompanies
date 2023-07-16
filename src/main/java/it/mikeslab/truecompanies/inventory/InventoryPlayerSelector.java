package it.mikeslab.truecompanies.inventory;

import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.object.Company;
import it.mikeslab.truecompanies.object.Employee;
import it.mikeslab.truecompanies.util.error.SentryDiagnostic;
import it.mikeslab.truecompanies.util.itemstack.ItemStackUtil;
import it.mikeslab.truecompanies.util.language.LangKey;
import it.mikeslab.truecompanies.util.language.Language;
import it.mikeslab.truecompanies.util.sign.SignMenuAPI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class InventoryPlayerSelector {
    private final CompletableFuture<UUID> completableFuture = new CompletableFuture<>();
    @Getter @Setter
    private Company company;
    private static final String[] SETUP = {
            "ddddddddd",
            "ddddddddd",
            "   a b  c",
    };

    public CompletableFuture<UUID> show(Player target, String title, SelectorType selectorType, String filter) {
        InventoryGui gui = new InventoryGui(TrueCompanies.getInstance(), title, SETUP);

        gui.setFiller(ItemStackUtil.getFiller(Material.GRAY_STAINED_GLASS_PANE));

        try {
            List<StaticGuiElement> elements = queryPlayers(target, selectorType, filter);
            elements.forEach(gui::addElement);

            gui.addElement(new GuiPageElement('a', ItemStackUtil.createStack(Material.REDSTONE, Language.getString(LangKey.PREVIOUS_PAGE)), GuiPageElement.PageAction.PREVIOUS));
            gui.addElement(new GuiPageElement('b', ItemStackUtil.createStack(Material.ARROW, Language.getString(LangKey.NEXT_PAGE)), GuiPageElement.PageAction.NEXT));

            if (TrueCompanies.getInstance().isProtocolLibEnabled()) {
                gui.addElement(new StaticGuiElement('c', ItemStackUtil.createStack(Material.OAK_SIGN, Language.getString(LangKey.SEARCH), Language.getString(LangKey.SEARCH_LORE)), click -> {
                    openFilterInput(target, title, selectorType);
                    return true;
                }));
            }

            gui.setCloseAction(close -> {
                completableFuture.complete(null);
                return true;
            });

            return completableFuture;
        } catch (Exception e) {
            SentryDiagnostic.capture(e);
            completableFuture.completeExceptionally(e);
            return completableFuture;
        }
    }

    private List<StaticGuiElement> queryPlayers(Player sender, SelectorType selectorType, String filter) {
        List<StaticGuiElement> elements = new ArrayList<>();

        switch (selectorType) {
            case ONLINE:
                elements.addAll(Bukkit.getOnlinePlayers().stream()
                        .filter(player -> !player.getUniqueId().equals(sender.getUniqueId()))
                        .filter(player -> filter == null || player.getName().toLowerCase().contains(filter.toLowerCase()))
                        .map(this::createPlayerElement).toList());
                break;
            case EMPLOYEES:
                elements.addAll(company.getEmployees().stream()
                        .map(Employee::getPlayerUUID)
                        .filter(playerUUID -> !playerUUID.equals(sender.getUniqueId()))
                        .map(Bukkit::getOfflinePlayer)
                        .filter(offlinePlayer -> filter == null || offlinePlayer.getName().toLowerCase().contains(filter.toLowerCase()))
                        .map(this::createPlayerElement).toList());

                break;
            case EXCEPT_EMPLOYEES:
                List<UUID> employees = company.getEmployees().stream()
                        .map(Employee::getPlayerUUID).toList();

                elements.addAll(Bukkit.getOnlinePlayers().stream()
                        .filter(player -> filter == null || player.getName().toLowerCase().contains(filter.toLowerCase()))
                        .filter(player -> !employees.contains(player.getUniqueId()))
                        .map(this::createPlayerElement).toList());
                break;
        }

        return elements;
    }

    private StaticGuiElement createPlayerElement(OfflinePlayer player) {
        ItemStack playerHead = ItemStackUtil.createStack(
                player,
                "&a" + player.getName(),
                Language.getString(LangKey.CLICK_TO_SELECT));

        return new StaticGuiElement('d', playerHead, click -> {
            completableFuture.complete(player.getUniqueId());
            click.getGui().close();
            return true;
        });
    }

    public void openFilterInput(Player target, String title, SelectorType selectorType) {
        SignMenuAPI.builder()
                .action(event -> {
                    String filter = event.getLines().get(0);

                    if (filter.isEmpty()) {
                        show(target, title, selectorType, null);
                    } else {
                        show(target, title, selectorType, filter);
                    }
                })
                .withLines(List.of("", "", "^^^^^^^^^^^^^^^^", Language.getString(LangKey.ENTER_QUERY)))
                .plugin(TrueCompanies.getInstance())
                .uuid(target.getUniqueId())
                .build()
                .open();
    }
}
