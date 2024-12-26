package com.itsschatten.yggdrasil.items.manipulators;

import com.google.common.collect.ArrayListMultimap;
import com.itsschatten.yggdrasil.items.MetaManipulator;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * A prebuilt {@link MetaManipulator} that will add an empty map to the item's attributes and then {@link org.bukkit.inventory.ItemFlag#HIDE_ATTRIBUTES}.
 */
public final class HideAttributesManipulator implements MetaManipulator {

    @Override
    public void apply(final @NotNull ItemMeta meta) {
        meta.setAttributeModifiers(ArrayListMultimap.create());
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    }

}
