package com.itsschatten.yggdrasil.menus.manager;

import com.itsschatten.yggdrasil.menus.utils.IMenuHolder;
import com.itsschatten.yggdrasil.menus.utils.IMenuHolderManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages User instances for Menus. The instances are created dynamically and discarded if the player leaves the server.
 */
public class DefaultPlayerManager implements IMenuHolderManager, Listener {

    // UUID = Wrapped Menu Holder
    private final Map<UUID, PlayerWrapperMenuHolder> players = new HashMap<>();

    /**
     * {@inheritDoc}
     *
     * @param uuid The uuid of the holder we wish to obtain.
     * @return Returns a possibly null {@link IMenuHolder}
     */
    @Override
    public final @Nullable IMenuHolder getMenuHolder(UUID uuid) {
        final Player target = Bukkit.getPlayer(uuid);
        if (target != null && target.isOnline()) {
            if (players.containsKey(uuid)) {
                return players.get(uuid);
            }
            return new PlayerWrapperMenuHolder(target);
        }

        return null;
    }

    /**
     * Deferred to {@link #getMenuHolder(UUID)}
     *
     * @param player The player we wish to get the holder of.
     * @return Returns a possibly null {@link IMenuHolder}
     * @see #getMenuHolder(UUID)
     */
    @Override
    public final @Nullable IMenuHolder getMenuHolder(@NotNull Player player) {
        return getMenuHolder(player.getUniqueId());
    }

    /**
     * Deferred to {@link #remove(UUID)}
     *
     * @param player The player we wish to remove.
     * @return Returns a possibly null {@link IMenuHolder}
     * @see #remove(UUID)
     */
    @Override
    public IMenuHolder remove(@NotNull Player player) {
        return remove(player.getUniqueId());
    }

    /**
     * {@inheritDoc}
     *
     * @param uuid The player we wish to remove.
     * @return Returns a possibly null {@link IMenuHolder}
     */
    @Override
    public IMenuHolder remove(UUID uuid) {
        return players.remove(uuid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void shutdown() {
        this.players.clear();
    }

    // Highest priority to ensure that it's executed last.
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(final @NotNull PlayerQuitEvent event) {
        // We attempt to remove the player regardless of if they are added to the internal map or not.
        // This is because we attempt to "dynamically" load the player whenever we require it.
        remove(event.getPlayer());
    }

}
