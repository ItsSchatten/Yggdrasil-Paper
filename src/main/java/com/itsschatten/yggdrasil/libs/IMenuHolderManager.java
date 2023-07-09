package com.itsschatten.yggdrasil.libs;

import com.itsschatten.yggdrasil.libs.menus.utils.IMenuHolder;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Interface for a Prisoner class manager.
 * <p>
 * <b>Classes should not implement this class, doing so may break functionality.</b>
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
}
