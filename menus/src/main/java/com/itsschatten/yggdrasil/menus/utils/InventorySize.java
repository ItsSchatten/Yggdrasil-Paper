package com.itsschatten.yggdrasil.menus.utils;

import org.jetbrains.annotations.Range;

/**
 * Storage class for generic inventory sizes.
 */
public final class InventorySize {

    /**
     * A double chest.
     */
    public static final int FULL = 54;

    /**
     * A single chest.
     */
    public static final int HALF = 27;

    /**
     * Return the size of a chest with the number of rows.
     *
     * @param row A number between 1 and 5, inclusive.
     * @return Returns the {@code row} multiplied by {@code 9}.
     */
    public static int of(@Range(from = 1, to = 5) int row) {
        return 9 * row;
    }

}
