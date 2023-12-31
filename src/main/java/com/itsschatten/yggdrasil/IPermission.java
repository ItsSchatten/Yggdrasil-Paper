package com.itsschatten.yggdrasil;

/**
 * Used to keep track of all permissions used and to keep them consistent.
 */
public interface IPermission {
    /**
     * The permission needed.
     *
     * @return The permission string.
     */
    String getPermission();
}
