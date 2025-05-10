package com.itsschatten.yggdrasil.menus.utils;

import com.itsschatten.yggdrasil.menus.Menu;
import org.bukkit.event.inventory.ClickType;

/**
 * A functional interfaces whose functional method is {@link #apply(MenuHolder, Menu, ClickType)}.
 * Used to run something and return a boolean value.
 *
 * @param <T> The holder of the menu for this function.
 */
@FunctionalInterface
public interface MenuFunction<T extends MenuHolder> {

    /**
     * Runs the operation.
     *
     * @param holder The holder of a menu.
     * @param menu   The menu clicked.
     * @param click  The click type.
     * @see MenuHolder
     * @see Menu
     * @see ClickType
     * @see com.itsschatten.yggdrasil.menus.buttons.Button#onClicked(MenuHolder, Menu, ClickType)
     */
    boolean apply(final T holder, final Menu<T> menu, final ClickType click);

}
