package com.itsschatten.yggdrasil.menus.types.interfaces;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Signifies we should run a check before allowing a drag to succeed.
 */
public interface CheckDrag {

    /**
     * Used to determine if we can drag.
     *
     * @param newItems The map of dragged items.
     * @return <code>true</code> if we should allow the drag, <code>false</code> if we should not allow the drag.
     */
    boolean canDrag(final Map<Integer, ItemStack> newItems);
}
