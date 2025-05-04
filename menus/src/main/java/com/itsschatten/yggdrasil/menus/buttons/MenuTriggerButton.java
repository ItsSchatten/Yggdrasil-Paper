package com.itsschatten.yggdrasil.menus.buttons;

import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.utils.MenuHolder;
import org.bukkit.event.inventory.ClickType;

/**
 * Button that, when clicked, will open a menu for the clicker.
 */
public abstract class MenuTriggerButton<T extends MenuHolder> extends Button<T> {

    /**
     * The menu that should be opened to the player.
     *
     * @param user The user that clicked on this button.
     * @return The menu class.
     * @see Menu#switchMenu(MenuHolder, Menu)
     */
    public abstract Menu<T> getMenu(final T user, ClickType click);

    /**
     * Called before switching to the menu for this button.
     *
     * @param user  The user that clicked.
     * @param menu  The menu that was clicked.
     * @param click The type of click.
     */
    public void preSwitch(T user, final Menu<T> menu, final ClickType click) {
    }

    /**
     * {@inheritDoc}
     *
     * @param user  The {@link MenuHolder} that clicked this button.
     * @param menu  The {@link Menu} that this button was clicked in.
     * @param click The {@link ClickType} that was used to click this button.
     */
    @Override
    public final void onClicked(final T user, final Menu<T> menu, final ClickType click) {
        preSwitch(user, menu, click);
        getMenu(user, click).switchMenu(user, menu);
    }
}
