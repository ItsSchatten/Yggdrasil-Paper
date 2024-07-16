package com.itsschatten.yggdrasil.menus.types;

import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.premade.CloseButton;
import com.itsschatten.yggdrasil.menus.buttons.premade.InfoButton;
import com.itsschatten.yggdrasil.menus.buttons.premade.ReturnButton;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.menus.utils.MenuInventory;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Range;

import java.util.Objects;

/**
 * Standard menu, nothing special about this one.
 */
public abstract class StandardMenu extends Menu {

    @Getter
    private final Menu parent;

    Integer rows;

    private Integer size;

    @Getter
    @Setter
    private String title;

    /**
     * Standard implementation of {@link Menu}
     *
     * @param parent The parent (or previous) menu.
     */
    public StandardMenu(Menu parent) {
        super();
        this.parent = parent;
    }

    /**
     * Set the rows for this menu.
     *
     * @param rows The number of rows for the inventory.
     */
    public void setRows(@Range(from = 1, to = 6) Integer rows) {
        this.rows = rows;
    }

    /**
     * Gets the size of the inventory
     *
     * @return The size of the inventory.
     */
    public final @Range(from = 9, to = 54) int getSize() {
        if (rows == null)
            Validate.isTrue(size != null, "Size must be set in order to call getSize() in: " + this);
        return rows != null && (rows > 0) ? rows * 9 : size;
    }

    /**
     * Sets the size of this Menu.
     *
     * @param size The size to set for this menu, must be a multiple of nine.
     */
    public void setSize(@Range(from = 9, to = 54) final int size) {
        if (size % 9 != 0) {
            throw new UnsupportedOperationException("Size must be multiple of 9.");
        }
        this.size = size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MenuInventory formInventory() {
        Objects.requireNonNull(title, "Title is not set in " + this + "!");

        final MenuInventory inv;
        if (rows != null && rows > 0) {
            setSize(rows * 9);
            inv = MenuInventory.ofRows(rows, this, title);
        } else {
            Objects.requireNonNull(size, "Size must be set for " + this + " if no rows are not provided!");
            inv = MenuInventory.of(size, this, title);
        }
        inv.setViewer(getViewer());
        inv.setMenu(this);
        setInventory(inv);

        registerPreMadeButtons();
        drawExtra();
        drawButtons();
        return inv;
    }

    /**
     * Used to draw extra things to the menu that aren't necessarily menu buttons.
     */
    public void drawExtra() {
    }

    /**
     * Registers all pre-made buttons and sets them in the inventory if required.
     */
    protected final void registerPreMadeButtons() {
        if (addInfo()) {
            final InfoButton infoButton = new InfoButton(getInfoButtonMaterial(), getInfoButtonName(), getInfoButtonPosition(), getInfo());
            registerButtons(infoButton);
        }

        if (addClose()) {
            final CloseButton closeButton = new CloseButton(getCloseButtonLore(), getCloseButtonName(), getCloseButtonMaterial(), getCloseButtonPosition());
            registerButtons(closeButton);
        }

        if (addReturn()) {
            Objects.requireNonNull(parent, "Parent cannot be null if addReturn is set to 'true'!");
            final ReturnButton returnButton = new ReturnButton(getParent(), getReturnButtonPosition(), makeNewInstanceOfParent(), getReturnButtonMaterial(), getReturnButtonName(), getReturnButtonLore());
            registerButtons(returnButton);
        }

    }

    // Button things.

    /**
     * Close button material.
     *
     * @return {@link Material#BARRIER}, default return.
     */
    public Material getCloseButtonMaterial() {
        return Material.BARRIER;
    }

    /**
     * Return button material.
     *
     * @return {@link Material#SPECTRAL_ARROW}, default return.
     */
    public Material getReturnButtonMaterial() {
        return Material.SPECTRAL_ARROW;
    }

    /**
     * Info button material.
     *
     * @return {@link Material#NETHER_STAR}, default return.
     */
    public Material getInfoButtonMaterial() {
        return Material.NETHER_STAR;
    }

    /**
     * The name of the close button
     *
     * @return "Close" by default.
     */
    public String getCloseButtonName() {
        return "<red>Close";
    }

    /**
     * The name of the return button
     *
     * @return "{@literal <} Return to {@literal <menu name>}" by default.
     */
    public String getReturnButtonName() {
        return "<yellow>< Return to " + parent.getInventory().getTitle();
    }

    /**
     * The name of the close button
     *
     * @return "Information" by default.
     */
    public String getInfoButtonName() {
        return "<yellow>Information";
    }

    /**
     * The lore for the close button.
     *
     * @return Nothing by default.
     */
    public String[] getCloseButtonLore() {
        return new String[0];
    }

    /**
     * The lore for the return button.
     *
     * @return Nothing by default.
     */
    public String[] getReturnButtonLore() {
        return new String[0];
    }

    /**
     * The lore for the info button.
     *
     * @return Nothing by default.
     */
    public String[] getInfo() {
        return new String[0];
    }

    /**
     * Should the close button be added to the menu?
     *
     * @return <code>true</code> by default.
     */
    public boolean addClose() {
        return true;
    }

    /**
     * Should the return button be added to the menu?
     *
     * @return If the Menu's parent is null the button is shown.
     */
    public boolean addReturn() {
        return parent != null;
    }

    /**
     * Should the info button be added to the menu?
     *
     * @return If the lore for the button is not null and the length is greater than 0, the button will be shown.
     */
    public boolean addInfo() {
        return getInfo() != null && getInfo().length != 0;
    }

    /**
     * Set ig we should attempt to make a new instance of the parent class before returning.
     *
     * @return <code>false</code> by default
     * @deprecated May not work entirely due to how reflection works.
     */
    @ApiStatus.Experimental
    @Deprecated
    public boolean makeNewInstanceOfParent() {
        return false;
    }

    /**
     * @return The position of the info button
     */
    protected InventoryPosition getInfoButtonPosition() {
        return InventoryPosition.of(getInventory().getRows() - 1, getInventory().getColumns() - 9);
    }

    /**
     * @return The position of the return button
     */
    protected InventoryPosition getReturnButtonPosition() {
        return InventoryPosition.of(getInventory().getRows() - 1, getInventory().getColumns() - 2);

    }

    /**
     * @return The position of the close button
     */
    protected InventoryPosition getCloseButtonPosition() {
        return InventoryPosition.of(getInventory().getRows() - 1, getInventory().getColumns() - 1);
    }
}
