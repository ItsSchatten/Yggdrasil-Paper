package com.itsschatten.yggdrasil.menus.buttons.interfaces;

import com.itsschatten.yggdrasil.menus.buttons.impl.PageNavigationButtonImpl;
import org.bukkit.inventory.ItemStack;

/**
 * Interface used to designate that a button has an alternative display item compared to an internal instance stack.
 */
public interface AlternativeDisplayItem {

    // This class is actually kinda garbage. It doesn't make any sense.
    // In fact, this class is never used anywhere other than to be implemented.
    // I think this was for something that was cooking in my head, but it's been so long (and has remained unimplemented for so long)
    // that it doesn't make sense to have anymore.
    // ItemStack getInner();

    /**
     * The {@link ItemStack} to display in the menu.
     *
     * @return Returns the provided {@link ItemStack}.
     * @see com.itsschatten.yggdrasil.menus.buttons.AnimatedButton
     * @see com.itsschatten.yggdrasil.menus.buttons.DynamicButton
     * @see PageNavigationButtonImpl
     */
    ItemStack displayItem();

}
