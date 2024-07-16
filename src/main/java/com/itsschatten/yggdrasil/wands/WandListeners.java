package com.itsschatten.yggdrasil.wands;

import com.itsschatten.yggdrasil.Utils;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Used to listen for {@link Wand} events.
 */
public class WandListeners implements Listener {

    /**
     * A map to store the first point locations.
     * -- GETTER --
     *  Gets the first location map that belongs to this listener.
     *
     * @return Returns the {@link #firstLocationMap}

     */
    @Getter
    public static Map<UUID, Location> firstLocationMap;

    /**
     * A map to store the second point locations.
     * -- GETTER --
     *  Gets the second location map that belongs to this listener.
     *
     * @return Returns the {@link #secondLocationMap}

     */
    @Getter
    public static Map<UUID, Location> secondLocationMap;

    /**
     * Initializes the maps.
     */
    public WandListeners() {
        firstLocationMap = new HashMap<>();
        secondLocationMap = new HashMap<>();
    }

    /**
     * Runs our wand logic.
     *
     * @param event A {@link PlayerInteractEvent}.
     */
    @EventHandler
    public void onWandInteract(final @NotNull PlayerInteractEvent event) {
        final Player player = event.getPlayer(); // Assign our player so we can access them later.
        if (event.getHand() == EquipmentSlot.OFF_HAND) return; // If the slot is not the main hand, ignore.
        if (event.getClickedBlock() == null) return; // If the block clicked doesn't exist.

        Utils.getWands().forEach((wand) -> {
            if (event.getItem() == null) return; // If the item is null, ignore.
            if (wand.getPermission().isBlank() || player.hasPermission(wand.getPermission())) {
                if (event.getItem().equals(wand.getItemStack())) { // If the item used is exactly the wand's item stack.
                    event.setCancelled(true); // Cancel the event (no breaking blocks / no tilling soil)

                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        firstLocationMap.put(player.getUniqueId(), event.getClickedBlock().getLocation()); // Set the first location.
                        wand.onSelection(event.getClickedBlock().getLocation(), player, false); // Call the onSelection method from the wand.
                    }

                    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        secondLocationMap.put(player.getUniqueId(), event.getClickedBlock().getLocation()); // Set the second location.
                        wand.onSelection(event.getClickedBlock().getLocation(), player, true); // Call the onSelection method from the wand.
                    }

                    if (firstLocationMap.containsKey(player.getUniqueId()) && secondLocationMap.containsKey(player.getUniqueId())) { // If both locations are set.
                        wand.onSelectionComplete(firstLocationMap.get(player.getUniqueId()), secondLocationMap.get(player.getUniqueId()), player); // Call our complete method.
                        if (wand.getType() == WandType.SINGLE_SELECTION) {
                            firstLocationMap.remove(player.getUniqueId()); // Remove the player from the first point map.
                            secondLocationMap.remove(player.getUniqueId()); // Remove the player from the second point map.
                        }
                    }
                }
            }
        });
    }

    /**
     * Removes the player from the location maps.
     *
     * @param event The player quit event.
     */
    @EventHandler
    public void onPlayerQuit(final @NotNull PlayerQuitEvent event) {
        firstLocationMap.remove(event.getPlayer().getUniqueId()); // Remove the player from the first point map.
        secondLocationMap.remove(event.getPlayer().getUniqueId()); // Remove the player from the second point map.
    }

}
