package com.itsschatten.yggdrasil.menus.utils;

import com.itsschatten.yggdrasil.StringUtil;
import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.Button;
import com.itsschatten.yggdrasil.menus.buttons.interfaces.AlternativeDisplayItem;
import com.itsschatten.yggdrasil.menus.types.PageMenu;
import com.itsschatten.yggdrasil.menus.types.PaginatedMenu;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a MenuInventory.
 * <p>
 * This class is based mostly on MinusKube's SmartInvs.
 * <a href="https://github.com/MinusKube/SmartInvs/blob/master/src/main/java/fr/minuskube/inv/content/InventoryContents.java">Class on GitHub</a>
 * <p>
 * This is "implemented" in {@link AbstractMenuInventory}, with that class being implemented in {@link Menu}
 *
 * @see AbstractMenuInventory
 * @see Menu
 */
public class MenuInventory implements InventoryHolder {

    /**
     * The number of rows for this inventory.
     */
    @Getter
    private final int rows;

    /**
     * The columns of this inventory. Should always be nine, as we don't use other inventory types.
     */
    @Getter
    private final int columns = 9;

    /**
     * The contents of this inventory.
     */
    private final ItemStack[][] contents;

    /**
     * The actual {@link Inventory}.
     */
    @Getter(AccessLevel.PUBLIC)
    private final Inventory bukkitInventory;

    /**
     * The title of this inventory.
     */
    @Getter
    @Setter
    private String title;

    /**
     * The viewer of this inventory.
     */
    @Setter
    private IMenuHolder viewer;

    /**
     * The menu that this inventory belongs to.
     */
    @Getter
    @Setter
    private Menu menu;

    /**
     * An animate task, used in animating the title of the inventory.
     */
    @Getter
    private int animateTask;

    /**
     * Constructs a new MenuInventory.
     *
     * @param size  The full size of the inventory must be a multiple of nine.
     * @param menu  The menu this inventory belongs to.
     * @param title The title of this inventory.
     */
    private MenuInventory(int size, @NotNull Menu menu, String title) {
        this.title = title;
        this.rows = size / 9;
        this.contents = new ItemStack[size / 9][9];
        this.menu = menu;
        this.bukkitInventory = Bukkit.createInventory(this, size, StringUtil.color(this.title));
    }

    /**
     * Creates an inventory from the size provided.
     *
     * @param size  The size of the inventory.
     * @param menu  The menu this instance belongs too.
     * @param title The title of the menu.
     * @return A new instance of {@link MenuInventory}
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull MenuInventory of(int size, Menu menu, String title) {
        return new MenuInventory(size, menu, title);
    }

    /**
     * Creates an inventory of the rows provided.
     *
     * @param rows  The rows that this menu should contain.
     * @param menu  The menu this instance belongs too.
     * @param title The title of the menu.
     * @return A new instance of {@link MenuInventory}
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull MenuInventory ofRows(int rows, Menu menu, String title) {
        return of(9 * rows, menu, title);
    }

    /**
     * Get the underlying Bukkit inventory.
     *
     * @return The Bukkit {@link Inventory}.
     */
    @NotNull
    @Override
    public Inventory getInventory() {
        return getBukkitInventory();
    }

    /**
     * Clears the animate task.
     */
    public final void clearAnimateTask() {
        this.animateTask = 0;
    }

    /**
     * Check if an {@link InventoryPosition} is taken by a {@link Button}.
     *
     * @param position The {@link InventoryPosition} to check.
     * @return <code>true</code> of a {@link Button} is found in the position, <code>false</code> otherwise.
     * @see Menu#isSlotTakenByButton(InventoryPosition)
     */
    public final boolean isSlotTakenByButton(final InventoryPosition position) {
        if (menu instanceof PageMenu paged) {
            return menu.isSlotTakenByButton(position) || paged.isSlotTakenByPageButton(position);
        }

        if (menu instanceof PaginatedMenu<?> paged) {
            return menu.isSlotTakenByButton(position) || paged.isSlotTakenByPageButton(position);
        }

        return menu.isSlotTakenByButton(position);
    }

    /**
     * Utility method to check if an {@link InventoryPosition} is taken by an {@link ItemStack}.
     *
     * @param position The position to check.
     * @return <code>true</code> if something is in the position, <code>false</code> otherwise.
     */
    public final boolean isSlotTaken(InventoryPosition position) {
        for (int row = 0; row < getRows(); row++) {
            for (int column = 0; column < getColumns(); column++) {
                if (contents[row][column] != null && contents[row][column].getType() != Material.AIR) {
                    if (position.equals(InventoryPosition.of(row, column))) return true;
                }
            }
        }

        return false;
    }

    /**
     * Add an array of {@link ItemStack}s to the inventory using an {@link ItemCreator}.
     *
     * @param items The array of {@link ItemCreator}
     */
    public final void addItems(final ItemCreator @NotNull ... items) {
        for (final ItemCreator builder : items) {
            addItem(builder.make());
        }
    }

    /**
     * Fill an inventory with the provided {@link ItemStack}.
     *
     * @param creator The stack we should use to fill the inventory.
     */
    public final void fill(final @NotNull ItemCreator creator) {
        fill(creator.make());
    }

    /**
     * Fill an inventory with the provided {@link ItemStack}
     *
     * @param stack The stack we should use to fill the inventory.
     */
    public final void fill(final ItemStack stack) {
        for (int row = 0; row < getRows(); row++) {
            for (int column = 0; column < getColumns(); column++) {
                final InventoryPosition pos = InventoryPosition.of(row, column);
                if (/*!isSlotTaken(pos) &&*/ !isSlotTakenByButton(pos)) {
                    contents[row][column] = stack;

                    if (viewer != null) {
                        updateInv(row, column, stack);
                    }
                }
            }
        }
    }

    /**
     * Fill an entire row (ignoring registered {@link Button buttons}) with an item.
     *
     * @param row     The row in the inventory we would like to fill.
     * @param creator The {@link ItemCreator item creator} we should use to fill the row.
     */
    public final void setRow(final int row, @NotNull final ItemCreator creator) {
        setRow(row, creator.make());
    }

    /**
     * Fill an entire row (ignoring registered {@link Button buttons} with an {@link ItemStack}
     *
     * @param row   The row in the inventory we would like to fill.
     * @param stack The {@link ItemStack} we want to use
     */
    public final void setRow(final int row, final ItemStack stack) {
        if (row >= contents.length) {
            return;
        }

        for (int column = 0; column < 9; column++) {
            set(row, column, stack);
        }
    }

    /**
     * Fill an entire column (ignoring registered {@link Button buttons} with an {@link ItemStack}.
     *
     * @param column The column in the inventory we would like to fill.
     * @param stack  The {@link ItemStack} we want to use
     */
    public final void setColumn(final int column, final ItemStack stack) {
        for (int row = 0; row < contents.length; row++) {
            set(row, column, stack);
        }
    }

    /**
     * Fill an entire column (ignoring registered {@link Button buttons} with an {@link ItemStack}.
     *
     * @param column  The column in the inventory we would like to fill.
     * @param creator The {@link ItemCreator} we want to use to make an {@link ItemStack}
     */
    public final void setColumn(final int column, @NotNull final ItemCreator creator) {
        setColumn(column, creator.make());
    }

    /**
     * Set the border of the inventory with an item, this ignores {@link Button registered buttons}.
     *
     * @param stack The {@link ItemStack} we want to use
     */
    public final void setBorder(final ItemStack stack) {
        setRectangle(0, 0, rows - 1, columns - 1, stack);
    }

    /**
     * Set the border of the inventory with an item, this ignores {@link Button registered buttons}.
     *
     * @param creator The {@link ItemCreator} we want to use
     */
    public final void setBorder(@NotNull final ItemCreator creator) {
        setBorder(creator.make());
    }

    /**
     * Fills a rectangle border in the inventory.
     *
     * @param fromRow    The row we want to start from.
     * @param fromColumn The column we want to start from.
     * @param toRow      The row we want to go to.
     * @param toColumn   The column we want to go to.
     * @param stack      The {@link ItemStack} we want to use
     */
    public final void setRectangle(final int fromRow, final int fromColumn, final int toRow, final int toColumn, final ItemStack stack) {
        for (int row = fromRow; row <= toRow; row++) {
            for (int column = fromColumn; column <= toColumn; column++) {
                if (row != fromRow && row != toRow && column != fromColumn && column != toColumn) continue;

                if (viewer != null && menu != null) {
                    if (isSlotTakenByButton(InventoryPosition.of(row, column))) continue;
                }

                set(row, column, stack);
            }
        }
    }

    /**
     * Utility method to add an item into the inventory.
     *
     * @param stack The {@link ItemStack} to add to the inventory.
     */
    public final void addItem(final ItemStack stack) {
        boolean added = false;
        for (int row = 0; row < contents.length; row++) {
            if (added) return;
            for (int column = 0; column < contents[row].length; column++) {
                final ItemStack content = contents[row][column];
                if (content == null) {
                    contents[row][column] = stack;
                    added = true;
                    break;
                }
            }
        }
    }

    /**
     * Display this inventory.
     *
     * @param user The {@link IMenuHolder} that should be viewing this inventory.
     */
    public final void display(@NotNull final IMenuHolder user) {
        for (int row = 0; row < contents.length; row++) {
            for (int column = 0; column < contents[row].length; column++) {
                if (contents[row][column] != null) setItem(columns * row + column, contents[row][column]);
            }
        }
        viewer = user;
        user.getBase().openInventory(bukkitInventory);
    }

    /**
     * Show the menu.
     *
     * @param user The {@link IMenuHolder} that should be shown this menu.
     */
    public final void show(@NotNull final IMenuHolder user) {
        for (int row = 0; row < contents.length; row++) {
            for (int column = 0; column < contents[row].length; column++) {
                if (contents[row][column] != null) setItem(columns * row + column, contents[row][column]);
            }
        }

        user.getBase().openInventory(bukkitInventory);
    }

    /**
     * Utility method to set an {@link ItemStack} into a Bukkit {@link Inventory}.
     *
     * @param position The position to set the item in.
     * @param stack    The {@link ItemStack}
     */
    private void setItem(final int position, final ItemStack stack) {
        bukkitInventory.setItem(position, stack);
    }

    /**
     * Forcefully set a slot in the inventory to an {@link ItemStack}
     *
     * @param row    The row to set this stack in.
     * @param column The column to set this stack in.
     * @param stack  The {@link ItemStack}.
     * @see MenuInventory#forceSet(InventoryPosition, Button)
     */
    public final void forceSet(final int row, final int column, final ItemStack stack) {
        if (row >= contents.length) {
            return;
        }

        if (column >= contents[row].length) {
            return;
        }

        if (column < 0) return;

        contents[row][column] = stack;
        updateInv(row, column, stack);
    }

    /**
     * Set a slot in the inventory to an {@link ItemStack}.
     *
     * @param row     The row to set this stack in.
     * @param column  The column to set this stack in.
     * @param creator The {@link ItemCreator} to use to create the {@link ItemStack}.
     * @see #forceSet(int, int, ItemCreator)
     */
    public final void forceSet(final int row, final int column, @NotNull final ItemCreator creator) {
        forceSet(row, column, creator.make());
    }

    /**
     * Set a slot in the inventory to an {@link ItemStack}.
     * <p>
     * If the button has a permission, it will also check if the viewer has permission.
     *
     * @param row    The row to set this stack in.
     * @param column The column to set this stack in.
     * @param button The {@link Button} to use to get the {@link ItemStack} from.
     * @see Button#getItem()
     */
    public final void forceSet(final int row, final int column, @NotNull final Button button) {
        if (button.getPermission() != null)
            forceSet(row, column, viewer.getBase().hasPermission(button.getPermission()) ?
                    button instanceof AlternativeDisplayItem alt ? alt.displayItem() : button.getItem()
                    : null);
        else
            forceSet(row, column, button instanceof AlternativeDisplayItem alt ? alt.displayItem() : button.getItem());
    }

    /**
     * Set a slot in the inventory to an {@link ItemStack}.
     *
     * @param position The position to set this item in.
     * @param stack    The  {@link ItemStack}.
     * @see #forceSet(int, int, ItemStack)
     */
    public final void forceSet(@NotNull final InventoryPosition position, final ItemStack stack) {
        forceSet(position.row(), position.column(), stack);
    }

    /**
     * Set a slot in the inventory to an {@link ItemStack}.
     *
     * @param position The position to set this item in.
     * @param creator  The {@link ItemCreator} to use to create the {@link ItemStack}.
     * @see #forceSet(int, int, ItemCreator)
     */
    public final void forceSet(@NotNull final InventoryPosition position, @NotNull final ItemCreator creator) {
        forceSet(position.row(), position.column(), creator.make());
    }

    /**
     * Forcefully sets a {@link Button} to an {@link InventoryPosition}.
     *
     * @param position The position to set this button too.
     * @param button   {@link Button The button} to set at this location
     * @see Button
     */
    public final void forceSet(final InventoryPosition position, @NotNull final Button button) {
        if (position == null) return;
        forceSet(position.row(), position.column(), button);
    }

    /**
     * Set a slot in the inventory to an {@link ItemStack}.
     *
     * @param row    The row to set this stack in.
     * @param column The column to set this stack in.
     * @param stack  The {@link ItemStack}.
     */
    public final void set(final int row, final int column, final ItemStack stack) {
        if (row >= contents.length) {
            return;
        }

        if (column >= contents[row].length) {
            return;
        }

        if (column < 0) return;

        if (isSlotTakenByButton(InventoryPosition.of(row, column))) {
            return;
        }

        contents[row][column] = stack;
        updateInv(row, column, stack);
    }

    /**
     * Set a slot in the inventory to an {@link ItemStack}.
     *
     * @param row     The row to set this stack in.
     * @param column  The column to set this stack in.
     * @param creator The {@link ItemCreator} to use to create the {@link ItemStack}.
     */
    public final void set(final int row, final int column, @NotNull final ItemCreator creator) {
        set(row, column, creator.make());
    }

    /**
     * Set a slot in the inventory to an {@link ItemStack}.
     * If the button has a permission, it will also check if the viewer has permission.
     *
     * @param row    The row to set this stack in.
     * @param column The column to set this stack in.
     * @param button The {@link Button} to use to get the {@link ItemStack} from.
     * @see Button#getItem()
     */
    public final void set(final int row, final int column, @NotNull final Button button) {
        if (button.getPermission() != null)
            set(row, column, viewer.getBase().hasPermission(button.getPermission()) ? button.getItem() : null);
        else set(row, column, button.getItem());
    }

    /**
     * Set a slot in the inventory to an {@link ItemStack}.
     *
     * @param position The position to set this item in.
     * @param stack    The {@link ItemStack}.
     * @see #set(int, int, ItemStack)
     */
    public final void set(@NotNull final InventoryPosition position, final ItemStack stack) {
        set(position.row(), position.column(), stack);
    }

    /**
     * Set a slot in the inventory to an {@link ItemStack}.
     *
     * @param position The position to set this item in.
     * @param creator  The {@link ItemCreator} to use to create the {@link ItemStack}.
     * @see #set(int, int, ItemCreator)
     */
    public final void set(@NotNull final InventoryPosition position, @NotNull final ItemCreator creator) {
        set(position.row(), position.column(), creator.make());
    }

    /**
     * Set a slot in the inventory to an {@link ItemStack}.
     *
     * @param position The position to set this item in.
     * @param button   The {@link Button} to get the {@link ItemStack} from.
     * @see #set(int, int, ItemStack)
     */
    public final void set(final InventoryPosition position, @NotNull final Button button) {
        if (position == null) return;
        set(position.row(), position.column(), button);
    }

    /**
     * Utility method to update the inventory that belongs to this class.
     *
     * @param row    The row we want to update.
     * @param column The column that we want to update.
     * @param stack  The {@link ItemStack} to update the slot with.
     */
    private void updateInv(final int row, final int column, final ItemStack stack) {
        if (viewer == null) {
            return;
        }

        if (menu == null) {
            return;
        }
        final Inventory inventory = getBukkitInventory();
        inventory.setItem(columns * row + column, stack);
    }

    /**
     * Sets a slot in the inventory to {@code null}.
     *
     * @param position The position to set.
     */
    public final void clearSlot(final @NotNull InventoryPosition position) {
        clearSlot(position.row(), position.column());
    }

    /**
     * Sets a slot in the inventory to {@code null}.
     *
     * @param row    The row to set this stack in.
     * @param column The column to set this stack in.
     */
    public final void clearSlot(final int row, final int column) {
        set(row, column, (ItemStack) null);
    }

}
