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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.List;
import java.util.Objects;

/**
 * Standard menu, nothing special about this one.
 */
public abstract class StandardMenu extends Menu {

    /**
     * The parent of this menu.
     * --- GETTER ---
     * Gets the parent of the menu.
     *
     * @return A possibly {@code null} {@link Menu} instance.
     */
    @Nullable
    @Getter
    private final Menu parent;

    /**
     * The number of rows for this menu, for accurate size use {@link #getSize()}.
     */
    protected Integer rows;

    /**
     * The number of cells in this menu.
     */
    private Integer size;

    /**
     * The title of this menu.
     * --- GETTER ---
     * Get the title of this menu.
     *
     * @return A {@link String} that represents the title of this Menu.
     * --- SETTER ---
     * Set the title of this Menu.
     * @param title The title to set.
     */
    @Getter
    @Setter
    private String title;

    /**
     * Standard implementation of {@link StandardMenu}.
     *
     * @param parent The parent (or previous) {@link StandardMenu}.
     */
    public StandardMenu(@Nullable final Menu parent) {
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
    @NotNull
    @Override
    public MenuInventory formInventory() {
        Objects.requireNonNull(title, "Title is not set in " + this.getClass().getSimpleName() + "!");

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
        if (addInfo() && getInfoButton() != null) {
            registerButtons(getInfoButton().build());
        }

        if (addClose() && getCloseButton() != null) {
            registerButtons(getCloseButton().build());
        }

        if (addReturn() && getReturnButton() != null) {
            Objects.requireNonNull(parent, "Parent cannot be 'null' if the menu wants to add a return button!");
            registerButtons(getReturnButton().build());
        }

    }

    // Pre-made button things.

    /**
     * The {@link CloseButton}.
     *
     * @return Returns a {@link CloseButton} builder class.
     * @see CloseButton#builder()
     */
    @Nullable
    public CloseButton.CloseButtonBuilder getCloseButton() {
        return CloseButton.builder()
                .material(Material.BARRIER)
                .name("<red>Close")
                .position(InventoryPosition.of(getInventory().getRows() - 1, getInventory().getColumns() - 1));
    }

    /**
     * The {@link ReturnButton}.
     *
     * @return Returns a {@link ReturnButton} builder class.
     * @see ReturnButton#builder()
     */
    @Nullable
    public ReturnButton.ReturnButtonBuilder getReturnButton() {
        return ReturnButton.builder()
                .menuToReturn(parent)
                .material(Material.ARROW)
                .name("<yellow>< Return to " + (parent != null ? parent.getInventory().getTitle() : ""))
                .position(InventoryPosition.of(getInventory().getRows() - 1, getInventory().getColumns() - 2));
    }

    /**
     * The {@link InfoButton}.
     *
     * @return Returns the {@link InfoButton}.
     * @see InfoButton#builder()
     */
    @Nullable
    public InfoButton.InfoButtonBuilder getInfoButton() {
        return InfoButton.builder()
                .material(Material.NETHER_STAR)
                .name("<yellow>Information")
                .lore(getInfo())
                .position(InventoryPosition.of(getInventory().getRows() - 1, getInventory().getColumns() - 9));
    }

    /**
     * The lore for the info button.
     *
     * @return Nothing by default.
     */
    public List<String> getInfo() {
        return List.of();
    }

    /**
     * Should the close button be added to the menu?
     *
     * @return <code>false</code> by default.
     */
    public boolean addClose() {
        return false;
    }

    /**
     * Should the return button be added to the menu?
     *
     * @return If the Menu's parent is not {@code null} the button is shown.
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
        return getInfo() != null && !getInfo().isEmpty();
    }
}
