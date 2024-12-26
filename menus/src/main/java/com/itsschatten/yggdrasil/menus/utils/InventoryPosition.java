package com.itsschatten.yggdrasil.menus.utils;

import com.google.common.collect.ImmutableList;
import com.itsschatten.yggdrasil.menus.types.PaginatedMenu;
import org.jetbrains.annotations.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a position in a {@link MenuInventory}
 *
 * @param row    The row of this position.
 * @param column The column of this position.
 */
public record InventoryPosition(@Range(from = 0, to = 5) int row, @Range(from = 0, to = 8) int column) {

    /**
     * List of all middle positions in an inventory, used to center a {@link PaginatedMenu}.
     */
    @Unmodifiable
    @NotNull
    public static ImmutableList<InventoryPosition> MIDDLE_POSITIONS = ImmutableList.of(
            of(1, 1), of(1, 2), of(1, 3), of(1, 4), of(1, 5), of(1, 6), of(1, 7),
            of(2, 1), of(2, 2), of(2, 3), of(2, 4), of(2, 5), of(2, 6), of(2, 7),
            of(3, 1), of(3, 2), of(3, 3), of(3, 4), of(3, 5), of(3, 6), of(3, 7),
            of(4, 1), of(4, 2), of(4, 3), of(4, 4), of(4, 5), of(4, 6), of(4, 7));

    /**
     * List of all edge positions, ignoring the top and bottom-edge cells.
     */
    @Unmodifiable
    @NotNull
    public static ImmutableList<InventoryPosition> SIDE_POSITIONS = ImmutableList.of(
            of(0, 0), of(1, 0), of(2, 0),
            of(3, 0), of(4, 0), of(5, 0),
            of(0, 8), of(1, 8), of(2, 8),
            of(3, 8), of(4, 8), of(5, 8)
    );

    /**
     * Obtain a new {@link InventoryPosition} based on the provided row and column.
     *
     * @param row    The row for this position.
     * @param column The column for this position.
     * @return A new {@link InventoryPosition}
     */
    @Contract("_, _ -> new")
    public static @NotNull InventoryPosition of(@Range(from = 0, to = 5) final int row, @Range(from = 0, to = 8) final int column) {
        return new InventoryPosition(row, column);
    }

    /**
     * Get a full row of {@link InventoryPosition} based on the provided row.
     *
     * @param row The row to get the positions for.
     * @return A {@link List} of {@link InventoryPosition positions} of the provided row.
     */
    public static @NotNull @UnmodifiableView List<InventoryPosition> ofRow(@Range(from = 0, to = 5) final int row) {
        final List<InventoryPosition> toSend = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            toSend.add(of(row, i));
        }

        return Collections.unmodifiableList(toSend);
    }

    /**
     * Get a full column of {@link InventoryPosition} based on the provided colum.
     *
     * @param column The column to get the positions for.
     * @return A {@link List} of {@link InventoryPosition positions} of the provided column.
     */
    public static @NotNull @UnmodifiableView List<InventoryPosition> ofColumn(@Range(from = 0, to = 8) final int column) {
        final List<InventoryPosition> toSend = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            toSend.add(of(i, column));
        }

        return Collections.unmodifiableList(toSend);
    }

    /**
     * Create an inventory slot based on the slot number.
     *
     * @param slot A slot in the range of 0 → 53.
     * @return Returns a new inventory position.
     */
    @Contract("_ -> new")
    public static @NotNull InventoryPosition fromSlot(@Range(from = 0, to = 53) int slot) {
        return fromAnySlot(slot);
    }

    /**
     * Create an inventory slot based on the slot number.
     *
     * @param slot A slot.
     * @return Returns a new inventory position.
     */
    @Contract("_ -> new")
    public static @NotNull InventoryPosition fromAnySlot(int slot) {
        return InventoryPosition.of(slot / 9, slot % 9);
    }

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "InventoryPosition{" +
                "row=" + row +
                ", column=" + column +
                ", effective_pos=" + getEffectiveSlot() +
                '}';
    }

    /**
     * @return Get the effective slot position based on the row and column.
     */
    public int getEffectiveSlot() {
        return (9 * row) + column;
    }

}
