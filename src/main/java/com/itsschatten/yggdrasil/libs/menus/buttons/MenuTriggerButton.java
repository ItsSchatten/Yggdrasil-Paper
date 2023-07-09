package com.itsschatten.yggdrasil.libs.menus.buttons;

import com.itsschatten.yggdrasil.libs.menus.Menu;
import com.itsschatten.yggdrasil.libs.menus.utils.IMenuHolder;
import org.bukkit.event.inventory.ClickType;

/**
 * Button that, when clicked, will open a menu for the clicker.
 */
public abstract class MenuTriggerButton extends Button {

    /**
     * The menu that should be opened to the player.
     *
     * @param user The user that clicked on this button.
     * @return The menu class.
     * @see Menu#switchMenu(IMenuHolder, Menu)
     */
    public abstract Menu getMenu(final IMenuHolder user, ClickType type);

    /**
     * {@inheritDoc}
     *
     * @param user The {@link IMenuHolder} that clicked this button.
     * @param menu The {@link Menu} that this button was clicked in.
     * @param type The {@link ClickType} that was used to click this button.
     */
    @Override
    public final void onClicked(IMenuHolder user, Menu menu, ClickType type) {
        getMenu(user, type).switchMenu(user, menu);
    }
}
