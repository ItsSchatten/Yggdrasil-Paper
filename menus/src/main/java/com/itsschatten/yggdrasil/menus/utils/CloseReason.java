package com.itsschatten.yggdrasil.menus.utils;

/**
 * Reasons for a {@link com.itsschatten.yggdrasil.menus.Menu} closing.
 */
public enum CloseReason {

    /**
     * Default, will call the normal close logic.
     */
    DEFAULT,
    /**
     * Switched to another {@link com.itsschatten.yggdrasil.menus.Menu}. Calls the onSwitch logic.
     */
    SWITCH,
    /**
     * We opened a AnvilGUI.
     */
    ANVIL,
    /**
     * We don't know why the reason, this is heavily unused.
     */
    UNKNOWN

}
