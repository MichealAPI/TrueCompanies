package it.mikeslab.truecompanies.menu.perms;

import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.GuiStateElement;
import de.themoep.inventorygui.InventoryGui;
import it.mikeslab.truecompanies.TrueCompanies;
import it.mikeslab.truecompanies.menu.element.CustomizableGuiElement;
import it.mikeslab.truecompanies.menu.selector.CompanySelectorMenu;
import it.mikeslab.truecompanies.menu.selector.GroupSelectorMenu;
import it.mikeslab.truecompanies.object.Company;
import it.mikeslab.truecompanies.object.Group;
import it.mikeslab.truecompanies.util.CompanyUtils;
import it.mikeslab.truecompanies.util.error.SentryDiagnostic;
import it.mikeslab.truecompanies.util.language.LangKey;
import it.mikeslab.truecompanies.util.language.Language;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class CompanyPermsInventory {

    private final TrueCompanies instance;

    private final FileConfiguration menuConfiguration;
    private final CompanyUtils companyUtils;

    private final String[] setup = {
        "         ",
        "o aaaaaa ",
        "   b c   ",
    };



    public CompanyPermsInventory(TrueCompanies instance) {
        this.instance = instance;

        this.companyUtils = instance.getCompanyUtils();
        this.menuConfiguration = instance.getMenuConfiguration();
    }


    public void show(Player target, Group subjectGroup, String title, Company company) {
        try {

            checkPrerequisites(target, subjectGroup, title, company);

            String inventoryID = "company-perms";
            InventoryGui gui = new InventoryGui(instance, title, setup);

            GuiElementGroup group = new GuiElementGroup('a');

            for (GroupPermission perm : GroupPermission.values()) {

                ItemStack enabledItem = new ItemStack(Material.LIME_WOOL);
                ItemStack disabledItem = new ItemStack(Material.RED_WOOL);
                String enabledDesc = Language.getString(LangKey.PERMS_CLICK_TO_ENABLE, false, Map.of("%perm%", perm.getName()));
                String disabledDesc = Language.getString(LangKey.PERMS_CLICK_TO_DISABLE, false, Map.of("%perm%", perm.getName()));


                GuiStateElement guiStateElement = new GuiStateElement(
                        'a',
                        new GuiStateElement.State(
                                change -> {
                                    perm.toggleStatus(subjectGroup);
                                },
                                perm.getName().toLowerCase() + "-true",
                                enabledItem,
                                enabledDesc
                        ),
                        new GuiStateElement.State(
                                change -> {
                                    perm.toggleStatus(subjectGroup);
                                },
                                perm.getName().toLowerCase() + "-false",
                                disabledItem,
                                disabledDesc
                        )
                );

                if (perm.getStatus(perm, subjectGroup)) {
                    guiStateElement.setState(perm.getName().toLowerCase() + "-true");
                } else {
                    guiStateElement.setState(perm.getName().toLowerCase() + "-false");
                }

                group.addElement(guiStateElement);
            }
            gui.addElement(group);

            gui.addElement(new CustomizableGuiElement(menuConfiguration).create('o', inventoryID, "perms-menu-info-element"));

            gui.addElement(new GuiPageElement('b', new ItemStack(Material.REDSTONE), GuiPageElement.PageAction.PREVIOUS, Language.getString(LangKey.PREVIOUS_PAGE, false)));
            gui.addElement(new GuiPageElement('c', new ItemStack(Material.ARROW), GuiPageElement.PageAction.NEXT, Language.getString(LangKey.NEXT_PAGE, false)));

            gui.setCloseAction(close -> {
                if (companyUtils.updateGroupPermissions(company, subjectGroup)) {
                    target.sendMessage(Language.getString(LangKey.PERMS_UPDATED, true));
                } else {
                    target.sendMessage(Language.getString(LangKey.ERROR_UPDATING_PERMS, true));
                }
                return false;
            });

            gui.show(target);
        } catch (Exception e) {
            e.printStackTrace();
            SentryDiagnostic.capture(e);
        }
    }



    public void checkPrerequisites(Player target, Group subjectGroup, String title, Company company) {
        // Selects company
        if(company == null) {
            new CompanySelectorMenu(instance).show(target).thenAccept(selectedCompany -> {
                if(selectedCompany == null) {
                    return;
                }
                show(target, subjectGroup, title, selectedCompany);
            });
        }

        // Selects group
        if(subjectGroup == null) {
            new GroupSelectorMenu(instance).show(target, company).thenAccept(group -> {
                if(group == null) {
                    return;
                }
                show(target, group, title, company);
            });
        }

    }






}
