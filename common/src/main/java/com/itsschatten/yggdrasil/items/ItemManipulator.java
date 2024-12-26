package com.itsschatten.yggdrasil.items;

import org.bukkit.inventory.ItemStack;

/**
 * A functional interface executed with {@link Manipulator#apply(Object)} where the object is an {@link ItemStack}.
 * <br>
 * Manipulators are called after all other meta options have been updated and set.
 */
@FunctionalInterface
public interface ItemManipulator extends Manipulator<ItemStack> {
}
