package com.itsschatten.yggdrasil.menus.buttons;

import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.utils.IMenuHolder;
import org.bukkit.event.inventory.ClickType;

/**
 * A button that doesn't do anything, but can be animated.
 *
 * @see AnimatedButton
 */
public abstract class AnimatedSimpleButton extends AnimatedButton {

    /**
     * {@inheritDoc}
     *
     * @param user The {@link IMenuHolder} that clicked this button.
     * @param menu The {@link Menu} that this button was clicked in.
     * @param click The {@link ClickType} that was used to click this button.
     */
    @Override
    public final void onClicked(final IMenuHolder user, final Menu menu, final ClickType click) {
    }
}
