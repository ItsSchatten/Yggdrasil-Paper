package com.itsschatten.yggdrasil.menus.utils;

import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.Button;
import com.itsschatten.yggdrasil.items.ItemCreator;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * This class is extended by {@link Menu} to provide ease-of-use to many methods within {@link MenuInventory}.
 *
 * @see MenuInventory
 */
@Setter
@Getter
public abstract class AbstractMenuInventory {

    /**
     * The {@link MenuInventory inventory} instance for this Menu.
     * --- SETTER ---
     * Set the {@link MenuInventory}.
     *
     * @param inventory The inventory to set.
     * --- GETTER ---
     * Gets the {@link MenuInventory} that is associated with this class.
     * @return Returns the inventory.
     */
    MenuInventory inventory = null;

    /**
     * Gets the {@link Inventory Bukkit inventory} the {@link MenuInventory} is handling.
     *
     * @return An instance of Bukkit's {@link Inventory}.
     */
    public final Inventory getBukkitInventory() {
        return getInventory().getBukkitInventory();
    }

    /**
     * Used to form the {@link MenuInventory}.
     *
     * @return Return's the newly created {@link MenuInventory}
     * @see MenuInventory
     */
    @NotNull
    public abstract MenuInventory formInventory();

    /**
     * Gets an {@link InventoryPosition} from the raw slot.
     *
     * @param slot The slot we should use to get this position.
     * @return A new {@link InventoryPosition} based on the provided slot.
     */
    public final @NotNull InventoryPosition getPositionFromSlot(int slot) {
        final int row = slot / inventory.getColumns();
        final int column = slot % inventory.getColumns();
        return InventoryPosition.of(row, column);
    }

    /**
     * Gets an item based on an {@link InventoryPosition}.
     *
     * @param position The position we should use to get the item.
     * @return The {@link ItemStack} that is currently in the provided position or null otherwise.
     * @see Inventory#getItem(int)
     */
    public final ItemStack getItemFromPosition(final @NotNull InventoryPosition position) {
        return getBukkitInventory().getItem(position.getEffectiveSlot());
    }

    /**
     * Check if the provided {@link InventoryPosition} is currently occupied by a {@link Button}.
     * <p>
     * As this is an abstract method, this is overwritten in the {@link Menu} class.
     *
     * @param position The position that we wish to check.
     * @return Returns <code>true</code> if the slot is taken by a registered button, <code>false</code> otherwise.
     * @see Menu#isSlotTakenByButton(InventoryPosition)
     */
    public abstract boolean isSlotTakenByButton(final InventoryPosition position);

    /**
     * Check if the provided {@link InventoryPosition} is currently occupied by an item.
     *
     * @param position The position we wish to check.
     * @return Returns <code>true</code> if any item takes the slot, <code>false</code> otherwise.
     * @see MenuInventory#isSlotTaken(InventoryPosition)
     */
    public final boolean isSlotTaken(InventoryPosition position) {
        return inventory.isSlotTaken(position);
    }

    /**
     * Fill an inventory with the provided {@link ItemStack}
     *
     * @param creator The stack we should use to fill the inventory.
     */
    public final void fill(final ItemCreator creator) {
        inventory.fill(creator);
    }

    /**
     * Fill an inventory with the provided {@link ItemStack}
     *
     * @param stack The stack we should use to fill the inventory.
     */
    public final void fill(final ItemStack stack) {
        inventory.fill(stack);
    }

    /**
     * Fill an entire row (ignoring registered {@link Button buttons}) with an item.
     *
     * @param row     The row in the inventory we would like to fill.
     * @param creator The {@link ItemCreator item creator} we should use to fill the row.
     */
    public final void setRow(@Range(from = 0, to = 5) final int row, @NotNull final ItemCreator creator) {
        inventory.setRow(row, creator);
    }

    /**
     * Fill an entire row (ignoring registered {@link Button buttons}) with a {@link Button}.
     *
     * @param row    The row in the inventory we would like to fill.
     * @param button The {@link Button} we should use to fill the row.
     */
    public final void setRow(@Range(from = 0, to = 5) final int row, @NotNull final Button button) {
        inventory.setRow(row, button.getItem());
    }

    /**
     * Fill an entire row (ignoring registered {@link Button buttons} with an {@link ItemStack}
     *
     * @param row   The row in the inventory we would like to fill.
     * @param stack The {@link ItemStack} we want to use
     */
    public final void setRow(@Range(from = 0, to = 5) final int row, final ItemStack stack) {
        inventory.setRow(row, stack);
    }

    /**
     * Fill an entire column (ignoring registered {@link Button buttons}) with a {@link Button}.
     *
     * @param column The column in the inventory we would like to fill.
     * @param button The {@link Button} we should use to fill the column.
     */
    public final void setColumn(@Range(from = 0, to = 8) final int column, @NotNull final Button button) {
        inventory.setColumn(column, button.getItem());
    }

    /**
     * Fill an entire column (ignoring registered {@link Button buttons} with an {@link ItemStack}
     *
     * @param column  The column in the inventory we would like to fill.
     * @param creator The {@link ItemCreator} we want to use to make an {@link ItemStack}
     */
    public final void setColumn(@Range(from = 0, to = 8) final int column, @NotNull final ItemCreator creator) {
        inventory.setColumn(column, creator);
    }

    /**
     * Fill an entire column (ignoring registered {@link Button buttons} with an {@link ItemStack}
     *
     * @param column The column in the inventory we would like to fill.
     * @param stack  The {@link ItemStack} we want to use
     */
    public final void setColumn(@Range(from = 0, to = 8) final int column, final ItemStack stack) {
        inventory.setColumn(column, stack);
    }

    /**
     * Set the border of the inventory with an item, this ignores {@link Button registered buttons}.
     *
     * @param stack The {@link ItemStack} we want to use
     */
    public final void setBorder(final ItemStack stack) {
        inventory.setBorder(stack);
    }

    /**
     * Set the border of the inventory with an item, this ignores {@link Button registered buttons}.
     *
     * @param creator The {@link ItemCreator} we want to use
     */
    public final void setBorder(@NotNull final ItemCreator creator) {
        inventory.setBorder(creator);
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
    public final void setRectangle(final int fromRow, @Range(from = 0, to = 8) final int fromColumn, final int toRow, @Range(from = 0, to = 8) final int toColumn, final ItemStack stack) {
        inventory.setRectangle(fromRow, fromColumn, toRow, toColumn, stack);
    }

    /**
     * Fills a rectangle border in the inventory.
     *
     * @param fromRow    The row we want to start from.
     * @param fromColumn The column we want to start from.
     * @param toRow      The row we want to go to.
     * @param toColumn   The column we want to go to.
     * @param creator    The {@link ItemCreator} we want to use
     */
    public final void setRectangle(final int fromRow, @Range(from = 0, to = 8) final int fromColumn, final int toRow, @Range(from = 0, to = 8) final int toColumn, @NotNull final ItemCreator creator) {
        setRectangle(fromRow, fromColumn, toRow, toColumn, creator.make());
    }

    /**
     * Set a slot in the inventory to an {@link ItemStack}
     *
     * @param row    The row to set this stack in.
     * @param column The column to set this stack in.
     * @param stack  The {@link ItemStack}.
     */
    public final void set(final int row, @Range(from = 0, to = 8) final int column, final ItemStack stack) {
        inventory.set(row, column, stack);
    }

    /**
     * Set a slot in the inventory to an {@link ItemStack}
     *
     * @param row     The row to set this stack in.
     * @param column  The column to set this stack in.
     * @param creator The {@link ItemCreator} to use to create the {@link ItemStack}.
     */
    public final void set(final int row, @Range(from = 0, to = 8) final int column, final ItemCreator creator) {
        inventory.set(row, column, creator);
    }

    /**
     * Set a slot in the inventory to an {@link ItemStack}.
     *
     * @param position The position to set this item in.
     * @param stack    The {@link ItemStack}.
     * @see #set(int, int, ItemStack)
     */
    public final void set(final @NotNull InventoryPosition position, final ItemStack stack) {
        inventory.set(position, stack);
    }

    /**
     * Set a slot in the inventory to an {@link ItemStack}.
     *
     * @param position The position to set this item in.
     * @param creator  The {@link ItemCreator} to use to create the {@link ItemStack}.
     * @see #set(int, int, ItemCreator)
     */
    public final void set(final @NotNull InventoryPosition position, final ItemCreator creator) {
        inventory.set(position, creator);
    }

    /**
     * Set a slot in the inventory to an {@link ItemStack}.
     *
     * @param position The position to set this item in.
     * @param button   The {@link Button} to use to get the {@link ItemStack} from.
     * @see #set(int, int, ItemCreator)
     * @see Button#getItem()
     */
    public final void set(final @NotNull InventoryPosition position, final Button button) {
        inventory.set(position, button);
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
        inventory.forceSet(row, column, stack);
    }

    /**
     * Set a slot in the inventory to an {@link ItemStack}.
     *
     * @param row     The row to set this stack in.
     * @param column  The column to set this stack in.
     * @param creator The {@link ItemCreator} to use to create the {@link ItemStack}.
     * @see #forceSet(int, int, ItemCreator)
     * @see MenuInventory#forceSet(InventoryPosition, Button)
     */
    public final void forceSet(final int row, final int column, final ItemCreator creator) {
        inventory.forceSet(row, column, creator);
    }

    /**
     * Set a slot in the inventory to an {@link ItemStack}.
     *
     * @param position The position to set this item in.
     * @param stack    The  {@link ItemStack}.
     * @see #forceSet(int, int, ItemStack)
     * @see MenuInventory#forceSet(InventoryPosition, ItemStack)
     */
    public final void forceSet(final @NotNull InventoryPosition position, final ItemStack stack) {
        inventory.forceSet(position, stack);
    }

    /**
     * Set a slot in the inventory to an {@link ItemStack}.
     *
     * @param position The position to set this item in.
     * @param creator  The {@link ItemCreator} to use to create the {@link ItemStack}.
     * @see #forceSet(int, int, ItemCreator)
     * @see MenuInventory#forceSet(InventoryPosition, ItemCreator)
     */
    public final void forceSet(final @NotNull InventoryPosition position, final ItemCreator creator) {
        inventory.forceSet(position, creator);
    }

    /**
     * Forcefully sets a {@link Button} to an {@link InventoryPosition}.
     *
     * @param position The position to set this button too.
     * @param button   {@link Button The button} to set at this location
     * @see Button
     * @see MenuInventory#forceSet(InventoryPosition, Button)
     */
    public final void forceSet(final @NotNull InventoryPosition position, final Button button) {
        inventory.forceSet(position, button);
    }

    /**
     * Sets the {@link InventoryPosition} to {@code null}.
     *
     * @param position The position to set.
     */
    public final void clearSlot(final @NotNull InventoryPosition position) {
        inventory.clearSlot(position);
    }

    /**
     * Sets the {@link InventoryPosition} to {@code null}.
     *
     * @param row    The row to set this stack in.
     * @param column The column to set this stack in.
     */
    public final void clearSlot(final int row, final int column) {
        inventory.clearSlot(row, column);
    }

}
