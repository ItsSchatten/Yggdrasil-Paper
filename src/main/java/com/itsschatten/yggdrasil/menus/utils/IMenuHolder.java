package com.itsschatten.yggdrasil.menus.utils;


import com.itsschatten.yggdrasil.IMenuHolderManager;
import com.itsschatten.yggdrasil.Utils;
import com.itsschatten.yggdrasil.menus.Menu;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * Represents a holder for a Menu.
 * <p>
 * This is implemented inside the PrisonCore project, other projects should not extend this class and instead should use an available instance via {@link IMenuHolderManager}.
 */
public interface IMenuHolder {

    /**
     * @return The base {@link Player}.
     */
    Player getBase();

    /**
     * This is only ever "active" when {@link #getBase()} has a {@link Menu} open.
     *
     * @return This holder's currently active menu.
     */
    Menu getCurrentMenu();

    /**
     * This is set whenever a menu is closed.
     *
     * @return This holder's previous menu.
     * @see MenuListeners
     */
    Menu getPreviousMenu();

    /**
     * This is set whenever a player begins viewing another holder's open {@link Menu}.
     * <P>This is reset whenever they close this menu and should never have a persistent value.</P>
     *
     * @return This holder's currently active viewing menu.
     */
    Menu getViewedMenu();

    /**
     * Sets the menu that this holder is viewing.
     *
     * @param menu The menu to set.
     */
    void setViewedMenu(Menu menu);

    /**
     * Remove's this holder's current menu.
     */
    void removeCurrentMenu();

    /**
     * Remove's this holder's previous menu.
     */
    void removePreviousMenu();

    /**
     * Removes this holder's currently viewed menu.
     */
    void removeViewedMenu();

    /**
     * Update his holder's current menu to the one provided.
     * <p>
     * If the holder currently has an active menu it is set as previous.
     *
     * @param menu The {@link Menu} that should be set as the active menu.
     */
    void updateMenu(final Menu menu);

    /**
     * Utility method to quickly send messages to the base of this class.
     *
     * @param message  A single message to send.
     * @param messages An additional array of messages to send to the base.
     */
    default void tell(final String message, final String... messages) {
        Utils.tell(getBase(), message, messages);
    }

    /**
     * Utility method to quickly send {@link Component}s to the base of this class.
     *
     * @param message  The single {@link Component} to send.
     * @param messages An additional array of {@link Component}s to send.
     */
    default void tell(final Component message, final Component... messages) {
        Utils.tell(getBase(), message, messages);
    }
}
