package com.itsschatten.yggdrasil.menus.utils;

import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Interface for the MenuHolder class.
 */
public interface IMenuHolderManager {

    /**
     * Obtain a menu holder from their UUID.
     *
     * @param uuid The uuid of the holder we wish to obtain.
     * @return An instanceof {@link IMenuHolder}
     * @see IMenuHolder
     */
    IMenuHolder getMenuHolder(final UUID uuid);

    /**
     * Obtain a menu holder from their {@link Player} object.
     * <p><b>Note:</b> Internally this is implemented to use {@link #getMenuHolder(UUID)}</p>
     *
     * @param player The player we wish to obtain the holder of.
     * @return An instanceof {@link IMenuHolder}
     * @see #getMenuHolder(UUID)
     * @see IMenuHolder
     */
    IMenuHolder getMenuHolder(final Player player);

    /**
     * Removes the provided player from the manager.
     *
     * @param player THe player we wish to remove.
     * @return The removed instanceof {@link IMenuHolder}
     * @see #getMenuHolder(UUID)
     * @see IMenuHolder
     */
    IMenuHolder remove(final Player player);

    /**
     * Removes the provided player from the manager.
     *
     * @param uuid The player we wish to remove.
     * @return The removed instanceof {@link IMenuHolder}
     * @see #getMenuHolder(UUID)
     * @see IMenuHolder
     */
    IMenuHolder remove(final UUID uuid);

}
