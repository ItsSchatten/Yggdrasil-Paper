package com.itsschatten.yggdrasil.menus.utils;

import com.itsschatten.yggdrasil.StringUtil;
import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.Button;
import com.itsschatten.yggdrasil.menus.buttons.interfaces.AlternativeDisplayItem;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a MenuInventory.
 * <p>
 * This class is based mostly on MinusKube's SmartInvs.
 * <a href="https://github.com/MinusKube/SmartInvs/blob/master/src/main/java/fr/minuskube/inv/content/InventoryContents.java">Class on GitHub</a>
 * </p>
 *
 * @param <T> A generic class that can be a {@link MenuHolder}.
 * @see Menu
 */
public abstract class MenuInventory<T extends MenuHolder> implements InventoryHolder {

    /**
     * The number of rows for this inventory.
     */
    @Getter
    @Accessors(fluent = true)
    private final int rows;

    /**
     * The columns of this inventory. Should always be nine, as we don't use other inventory types.
     */
    @Getter
    @Accessors(fluent = true)
    private final int columns = 9;

    /**
     * The contents of this inventory.
     */
    private final ItemStack[][] contents;

    /**
     * The actual {@link Inventory}.
     */
    private final Inventory bukkitInventory;

    /**
     * The title of this inventory.
     */
    @Getter
    private final String title;

    /**
     * The viewer of this inventory.
     */
    private T holder;

    /**
     * Constructs a new MenuInventory.
     *
     * @param size  The full size of the inventory must be a multiple of nine.
     * @param title The title of this inventory.
     */
    public MenuInventory(int size, String title) {
        this.title = title;
        this.rows = size / 9;
        this.contents = new ItemStack[size / 9][9];
        this.bukkitInventory = Bukkit.createInventory(this, size, StringUtil.color(this.title));
    }

    /**
     * Returns the {@link T} of this class.
     *
     * @return The {@link T} that holds this class.
     */
    public final @NotNull T holder() {
        return holder;
    }

    /**
     * Set the {@link MenuHolder} instance for this menu.
     *
     * @param holder The {@link T} to set.
     */
    public final void holder(@NotNull T holder) {
        this.holder = holder;
    }

    /**
     * Returns the size of the inventory.
     *
     * @return The size of the inventory, always a multiple of 9.
     */
    public final int getSize() {
        // Because we define rows using the size and store that variable,
        // we must multiply the rows by 9 to get the appropriate size of the inventory.
        return rows * 9;
    }

    /**
     * Get the underlying Bukkit inventory.
     *
     * @return The Bukkit {@link Inventory}.
     */
    @NotNull
    @Override
    public final Inventory getInventory() {
        return bukkitInventory;
    }

    /**
     * Determine if an {@link InventoryPosition} is currently taken by a {@link Button}.
     *
     * @param position The position to check.
     * @return Returns {@code true} if, and only if, a {@link Button} was found to be in the same position as the one provided.
     */
    public abstract boolean isSlotTakenByButton(InventoryPosition position);

    /**
     * Utility method to check if an {@link InventoryPosition} is taken by any {@link ItemStack}.
     *
     * @param position The position to check.
     * @return <code>true</code> if something is in the position, <code>false</code> otherwise.
     */
    public final boolean isSlotTaken(InventoryPosition position) {
        if (isSlotTakenByButton(position)) {
            return true;
        }
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
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
     * Add an array of {@link ItemStack}s to the inventory using an {@link ItemCreator.ItemCreatorBuilder}.
     *
     * @param items The array of {@link ItemCreator}
     */
    public final void addItems(final ItemCreator.ItemCreatorBuilder @NotNull ... items) {
        for (final ItemCreator.ItemCreatorBuilder builder : items) {
            addItem(builder.build().make());
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
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                final InventoryPosition pos = InventoryPosition.of(row, column);
                if (!isSlotTakenByButton(pos)) {
                    contents[row][column] = stack;

                    if (holder != null) {
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
     * Fill an entire row (ignoring registered {@link Button buttons}) with an item.
     *
     * @param row     The row in the inventory we would like to fill.
     * @param creator The {@link ItemCreator item creator} we should use to fill the row.
     */
    public final void setRow(final int row, @NotNull final ItemCreator.ItemCreatorBuilder creator) {
        setRow(row, creator.build().make());
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
     * Fill an entire column (ignoring registered {@link Button buttons} with an {@link ItemStack}.
     *
     * @param column  The column in the inventory we would like to fill.
     * @param creator The {@link ItemCreator} we want to use to make an {@link ItemStack}
     */
    public final void setColumn(final int column, @NotNull final ItemCreator.ItemCreatorBuilder creator) {
        setColumn(column, creator.build().make());
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
     * Set the border of the inventory with an item, this ignores {@link Button registered buttons}.
     *
     * @param creator The {@link ItemCreator} we want to use
     */
    public final void setBorder(@NotNull final ItemCreator.ItemCreatorBuilder creator) {
        setBorder(creator.build().make());
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

                if (holder != null) {
                    if (isSlotTakenByButton(InventoryPosition.of(row, column))) continue;
                }

                set(row, column, stack);
            }
        }
    }

    /**
     * Fills a rectangle border in the inventory.
     *
     * @param fromRow    The row we want to start from.
     * @param fromColumn The column we want to start from.
     * @param toRow      The row we want to go to.
     * @param toColumn   The column we want to go to.
     * @param builder    The {@link com.itsschatten.yggdrasil.items.ItemCreator.ItemCreatorBuilder} we want to use.
     */
    public final void setRectangle(final int fromRow, final int fromColumn, final int toRow, final int toColumn, final ItemCreator.@NotNull ItemCreatorBuilder builder) {
        setRectangle(fromColumn, fromColumn, toRow, toColumn, builder.build().make());
    }

    /**
     * Fills a rectangle border in the inventory.
     *
     * @param fromRow    The row we want to start from.
     * @param fromColumn The column we want to start from.
     * @param toRow      The row we want to go to.
     * @param toColumn   The column we want to go to.
     * @param creator    The {@link com.itsschatten.yggdrasil.items.ItemCreator} we want to use.
     */
    public final void setRectangle(final int fromRow, final int fromColumn, final int toRow, final int toColumn, final @NotNull ItemCreator creator) {
        setRectangle(fromRow, fromColumn, toRow, toColumn, creator.make());
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
     * @param user The {@link T} that should be viewing this inventory.
     */
    @ApiStatus.Internal
    protected final void display(@NotNull final T user) {
        for (int row = 0; row < contents.length; row++) {
            for (int column = 0; column < contents[row].length; column++) {
                if (contents[row][column] != null) setItem(columns * row + column, contents[row][column]);
            }
        }

        holder = user;
        user.player().openInventory(bukkitInventory);
    }

    /**
     * Show the menu.
     *
     * @param user The {@link T} that should be shown this menu.
     */
    @ApiStatus.Internal
    protected final void show(@NotNull final T user) {
        for (int row = 0; row < contents.length; row++) {
            for (int column = 0; column < contents[row].length; column++) {
                if (contents[row][column] != null) setItem(columns * row + column, contents[row][column]);
            }
        }

        user.player().openInventory(bukkitInventory);
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
     *
     * @param row     The row to set this stack in.
     * @param column  The column to set this stack in.
     * @param creator The {@link ItemCreator} to use to create the {@link ItemStack}.
     * @see #forceSet(int, int, ItemCreator)
     */
    public final void forceSet(final int row, final int column, @NotNull final ItemCreator.ItemCreatorBuilder creator) {
        forceSet(row, column, creator.build().make());
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
    public final void forceSet(final int row, final int column, @NotNull final Button<T> button) {
        if (button.getPermission() != null)
            forceSet(row, column, holder.hasPermission(button.getPermission()) ?
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
     * Set a slot in the inventory to an {@link ItemStack}.
     *
     * @param position The position to set this item in.
     * @param creator  The {@link ItemCreator} to use to create the {@link ItemStack}.
     * @see #forceSet(int, int, ItemCreator)
     */
    public final void forceSet(@NotNull final InventoryPosition position, @NotNull final ItemCreator.ItemCreatorBuilder creator) {
        forceSet(position.row(), position.column(), creator.build().make());
    }

    /**
     * Forcefully sets a {@link Button} to an {@link InventoryPosition}.
     *
     * @param position The position to set this button too.
     * @param button   {@link Button The button} to set at this location
     * @see Button
     */
    public final void forceSet(final InventoryPosition position, @NotNull final Button<T> button) {
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
     *
     * @param row     The row to set this stack in.
     * @param column  The column to set this stack in.
     * @param creator The {@link ItemCreator} to use to create the {@link ItemStack}.
     */
    public final void set(final int row, final int column, @NotNull final ItemCreator.ItemCreatorBuilder creator) {
        set(row, column, creator.build().make());
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
    public final void set(final int row, final int column, @NotNull final Button<T> button) {
        if (button.getPermission() != null)
            set(row, column, holder.hasPermission(button.getPermission()) ? button.getItem() : null);
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
     * @param creator  The {@link ItemCreator} to use to create the {@link ItemStack}.
     * @see #set(int, int, ItemCreator)
     */
    public final void set(@NotNull final InventoryPosition position, @NotNull final ItemCreator.ItemCreatorBuilder creator) {
        set(position.row(), position.column(), creator.build().make());
    }

    /**
     * Set a slot in the inventory to an {@link ItemStack}.
     *
     * @param position The position to set this item in.
     * @param button   The {@link Button} to get the {@link ItemStack} from.
     * @see #set(int, int, ItemStack)
     */
    public final void set(final InventoryPosition position, @NotNull final Button<T> button) {
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
        if (holder == null) {
            return;
        }

        bukkitInventory.setItem(columns * row + column, stack);
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
