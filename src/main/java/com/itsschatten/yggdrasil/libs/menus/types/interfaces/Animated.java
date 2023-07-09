package com.itsschatten.yggdrasil.libs.menus.types.interfaces;

import com.itsschatten.yggdrasil.libs.menus.Menu;
import com.itsschatten.yggdrasil.libs.TimeUtils;

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
