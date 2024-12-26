package com.itsschatten.yggdrasil.menus.types.interfaces;

/**
 * Keeps the inventory open for other viewers when the main viewer closes the inventory.
 */
public interface ViewerMaintained {

    /**
     * Should we notify users when the menu is closed?
     *
     * @return Returns where we should notify other viewers when the inventory is closed by the main viewer.
     * @see #message()
     */
    default boolean notifyUsers() {
        return false;
    }

    /**
     * The message to send when the inventory is closed.
     *
     * @return Returns the message when the menu is closed.
     */
    default String message() {
        return "<green>The menu holder has closed the menu!";
    }

}
