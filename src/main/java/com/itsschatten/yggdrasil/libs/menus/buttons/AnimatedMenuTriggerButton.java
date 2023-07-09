package com.itsschatten.yggdrasil.libs.menus.buttons;

import com.itsschatten.yggdrasil.libs.menus.Menu;
import com.itsschatten.yggdrasil.libs.menus.utils.IMenuHolder;
import org.bukkit.event.inventory.ClickType;

/**
 * Animated version of {@link MenuTriggerButton}.
 *
 * @see MenuTriggerButton
 * @see AnimatedButton
 */
public abstract class AnimatedMenuTriggerButton extends AnimatedButton {

    /**
     * The menu that should be opened when clicked.
     *
     * @param user The {@link IMenuHolder} that clicked this button.
     * @return The {@link Menu} instance.
     */
    public abstract Menu getMenu(final IMenuHolder user, final ClickType clickType);

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
