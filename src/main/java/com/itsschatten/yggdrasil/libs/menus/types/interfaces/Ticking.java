package com.itsschatten.yggdrasil.libs.menus.types.interfaces;

import com.itsschatten.yggdrasil.libs.menus.Menu;

/**
 * Signifies a menu is tickable. A ticking {@link Menu} will be ticked every in-game tick.
 */
public interface Ticking {

    /**
     * What should happen when we tick a menu.
     */
    void tick();

}
