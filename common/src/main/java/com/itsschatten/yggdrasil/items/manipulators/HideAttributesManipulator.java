package com.itsschatten.yggdrasil.items.manipulators;

import com.itsschatten.yggdrasil.items.ItemManipulator;
import com.itsschatten.yggdrasil.items.MetaManipulator;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A prebuilt {@link MetaManipulator} that will add an empty map to the item's attributes and then {@link org.bukkit.inventory.ItemFlag#HIDE_ATTRIBUTES}.
 */
public final class HideAttributesManipulator implements ItemManipulator {

    @Override
    public void apply(@NotNull ItemStack itemStack) {
        itemStack.unsetData(DataComponentTypes.ATTRIBUTE_MODIFIERS);
    }

}
