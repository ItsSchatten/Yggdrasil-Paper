package com.itsschatten.yggdrasil.menus.utils;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Interface for the MenuHolder class.
 */
public interface IMenuHolderManager {

    /**
     * Get a menu holder from their {@link UUID}.
     *
     * @param uuid The uuid of the holder we wish to obtain.
     * @return An instanceof {@link IMenuHolder}.
     * @see IMenuHolder
     */
    @Nullable
    IMenuHolder getMenuHolder(final UUID uuid);

    /**
     * Get a menu holder from their {@link Player} object.
     * <p><b>Note:</b> Internally this is implemented to use {@link #getMenuHolder(UUID)}</p>
     *
     * @param player The player we wish to get the holder of.
     * @return An instanceof {@link IMenuHolder}.
     * @see #getMenuHolder(UUID)
     * @see IMenuHolder
     */
    @Nullable
    IMenuHolder getMenuHolder(final Player player);

    /**
     * Removes the provided player from the manager.
     *
     * @param player The player we wish to remove.
     * @return The removed instanceof {@link IMenuHolder}.
     * @see #getMenuHolder(UUID)
     * @see IMenuHolder
     */
    @Nullable
    IMenuHolder remove(final Player player);

    /**
     * Removes the provided player from the manager.
     *
     * @param uuid The player we wish to remove.
     * @return The removed instanceof {@link IMenuHolder}.
     * @see #getMenuHolder(UUID)
     * @see IMenuHolder
     */
    @Nullable
    IMenuHolder remove(final UUID uuid);

    /**
     * Called when the owning plugin disables.
     */
    void shutdown();

}
