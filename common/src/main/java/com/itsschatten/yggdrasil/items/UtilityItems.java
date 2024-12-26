package com.itsschatten.yggdrasil.items;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Utility class to build common utility items.
 */
@UtilityClass
public class UtilityItems {

    /**
     * Makes a filler {@link ItemCreator} item.
     *
     * @param stack The stack to use as the base.
     * @return Returns {@link ItemCreator} with the added {@link ItemOptions#FILLER}.
     */
    public ItemCreator makeFiller(final ItemStack stack) {
        return ItemCreator.of(stack).options(ItemOptions.FILLER).build();
    }

    /**
     * Makes a filler {@link ItemCreator} item.
     *
     * @param material The material to use as the base.
     * @return Returns {@link ItemCreator} with the added {@link ItemOptions#FILLER}.
     */
    public ItemCreator makeFiller(final Material material) {
        return ItemCreator.of(material).options(ItemOptions.FILLER).build();
    }

}
