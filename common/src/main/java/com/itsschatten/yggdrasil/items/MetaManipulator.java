package com.itsschatten.yggdrasil.items;

import org.bukkit.inventory.meta.ItemMeta;

/**
 * A functional interface executed with {@link Manipulator#apply(Object)} where the object is an {@link ItemMeta} instance. Type checking should be conducted within.
 * <br>
 * Manipulators are called after all other meta options have been updated and set, it is recommended to use {@link ItemOptions} and the methods within {@link ItemCreator}.
 */
@FunctionalInterface
public interface MetaManipulator extends Manipulator<ItemMeta> {
}
