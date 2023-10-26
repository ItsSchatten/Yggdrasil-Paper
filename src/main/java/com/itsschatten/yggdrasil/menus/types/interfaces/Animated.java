package com.itsschatten.yggdrasil.menus.types.interfaces;

import com.itsschatten.yggdrasil.TimeUtils;
import com.itsschatten.yggdrasil.menus.Menu;

/**
 * Signifies that a {@link Menu} is animated.
 */
public interface Animated {

    /**
     * The delay before animating this menu.
     * <p></p>
     * Defaults to 20 ticks or one second.
     *
     * @return Return a long value in Minecraft ticks.
     * @see TimeUtils.MinecraftTimeUnits
     */
    default long getDelay() {
        return 20L;
    }

    /**
     * Called when the delay is finished and will be used to animate the menu.
     */
    void animate();

}
