package com.itsschatten.yggdrasil.menus.buttons;

import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.utils.MenuHolder;
import org.bukkit.event.inventory.ClickType;

/**
 * A button that doesn't do anything, but can be animated.
 *
 * @see AnimatedButton
 */
public abstract class AnimatedSimpleButton<T extends MenuHolder> extends AnimatedButton<T> {

    /**
     * {@inheritDoc}
     *
     * @param user  The {@link T} that clicked this button.
     * @param menu  The {@link Menu} that this button was clicked in.
     * @param click The {@link ClickType} that was used to click this button.
     */
    @Override
    public final void onClicked(final T user, final Menu<T> menu, final ClickType click) {
    }
}
