package com.itsschatten.yggdrasil.menus.utils;

import com.itsschatten.yggdrasil.menus.Menu;
import org.bukkit.event.inventory.ClickType;

/**
 * A functional interfaces whose functional method is {@link #run(MenuHolder, com.itsschatten.yggdrasil.menus.Menu, ClickType)}.
 * Used to run something.
 */
@FunctionalInterface
public interface MenuRunnable<T extends MenuHolder> {

    /**
     * Runs the operation.
     *
     * @param holder The holder of a menu.
     * @param menu   The menu clicked.
     * @param click  The click type.
     * @see MenuHolder
     * @see Menu
     * @see ClickType
     * @see com.itsschatten.yggdrasil.menus.buttons.Button#onClicked(MenuHolder, com.itsschatten.yggdrasil.menus.Menu, ClickType)
     */
    void run(final T holder, final Menu<T> menu, final ClickType click);

}
