package com.itsschatten.yggdrasil.wands;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Wand utilities.
 */
@UtilityClass
public class WandUtils {

    /**
     * A set of all {@link Wand}s
     * -- Getter --
     * Get the set of {@link Wand}.
     *
     * @return Returns the {@link HashSet} of wands.
     */
    @Getter
    private static Set<Wand> wands;

    private final WandListeners listener = new WandListeners();

    /**
     * Initialize the wands, registering the listeners as well as assigning a new {@link HashSet}
     *
     * @param plugin The plugin instance to initialize the listener to.
     */
    public void initalize(final @NotNull Plugin plugin) {
        wands = new HashSet<>();
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPluginDisable(final PluginDisableEvent event) {
                if (event.getPlugin() == plugin) {
                    wands.clear();
                    shutdown();
                }
            }
        }, plugin);
    }

    /**
     * "Shutdown" the wands.
     */
    public void shutdown() {
        HandlerList.unregisterAll(listener);
        wands = null;
    }

    /**
     * Get both selection {@link Location locations} for a {@link Player player}.
     * <p>
     * <b>It should be noted that this will not work properly with a {@link WandType#SINGLE_SELECTION single selection wand},</b>
     * <b>as the locations maps are purged of the selectors UUID once the selection has been completed.</b>
     *
     * @param selectorUUID The {@link UUID uuid} of the selector.
     * @return An {@link ArrayList} of the selector's locations.
     */
    public static @NotNull List<Location> getSelectionLocations(@NotNull UUID selectorUUID) {
        if (wands == null) {
            throw new RuntimeException("Cannot get selection locations when wands have not been registered.");
        }

        final List<Location> locationList = new ArrayList<>();
        if (WandListeners.getFirstLocationMap().containsKey(selectorUUID)) {
            locationList.add(WandListeners.getFirstLocationMap().get(selectorUUID));
        }

        if (WandListeners.getSecondLocationMap().containsKey(selectorUUID)) {
            locationList.add(WandListeners.getSecondLocationMap().get(selectorUUID));
        }

        return locationList;
    }

    /**
     * Returns the locations if found, and then removes the provided UUID from the location maps.
     *
     * @param selectorUUID The selecting player.
     * @return Returns a List of {@link Location}, may contain no entries.
     * @implNote If the List is ever not empty, it will ALWAYS contain two entries.
     */
    public static @NotNull List<Location> clearSelection(UUID selectorUUID) {
        if (wands == null) {
            throw new RuntimeException("Cannot get selection locations when wands have not been registered.");
        }

        final List<Location> locationList = new ArrayList<>();
        if (WandListeners.getFirstLocationMap().containsKey(selectorUUID) && WandListeners.getSecondLocationMap().containsKey(selectorUUID)) {
            locationList.add(WandListeners.getFirstLocationMap().get(selectorUUID));
            locationList.add(WandListeners.getSecondLocationMap().get(selectorUUID));
        }

        WandListeners.getFirstLocationMap().remove(selectorUUID);
        WandListeners.getSecondLocationMap().remove(selectorUUID);

        return locationList;
    }

}
