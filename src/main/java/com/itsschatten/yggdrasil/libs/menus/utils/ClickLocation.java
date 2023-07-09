package com.itsschatten.yggdrasil.libs.menus.utils;

import org.bukkit.entity.Player;

/**
 * Click locations within menus.
 */
public enum ClickLocation {

    /**
     * Represents a click location of the {@link MenuInventory}.
     */
    MENU,
    /**
     * Represents a click location of outside the {@link MenuInventory}.
     */
    OUTSIDE,
    /**
     * Represents a click inside the {@link Player player's} inventory
     */
    PLAYER

}
