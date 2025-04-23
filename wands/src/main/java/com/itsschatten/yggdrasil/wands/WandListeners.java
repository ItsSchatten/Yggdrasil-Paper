package com.itsschatten.yggdrasil.wands;

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
     */
    public final static Map<UUID, Location> FIRST_LOCATION = new HashMap<>();

    /**
     * A map to store the second point locations.
     */
    public final static Map<UUID, Location> SECOND_LOCATION = new HashMap<>();

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

        WandUtils.getWands().forEach((wand) -> {
            if (event.getItem() == null) return; // If the item is null, ignore.
            if (wand.permission().isBlank() || player.hasPermission(wand.permission())) {
                if (event.getItem().equals(wand.getItemStack())) { // If the item used is exactly the wand's item stack.
                    event.setCancelled(true); // Cancel the event (no breaking blocks / no tilling soil)

                    if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                        FIRST_LOCATION.put(player.getUniqueId(), event.getClickedBlock().getLocation()); // Set the first location.
                        wand.onSelect(event.getClickedBlock().getLocation(), player, false); // Call the onSelection method from the wand.
                    }

                    if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        SECOND_LOCATION.put(player.getUniqueId(), event.getClickedBlock().getLocation()); // Set the second location.
                        wand.onSelect(event.getClickedBlock().getLocation(), player, true); // Call the onSelection method from the wand.
                    }

                    if (FIRST_LOCATION.containsKey(player.getUniqueId()) && SECOND_LOCATION.containsKey(player.getUniqueId())) { // If both locations are set.
                        wand.onComplete(FIRST_LOCATION.get(player.getUniqueId()), SECOND_LOCATION.get(player.getUniqueId()), player); // Call our complete method.
                        if (wand.getType() == WandType.SINGLE_SELECTION) {
                            WandUtils.clearSelection(player.getUniqueId());
                        }
                    }
                }
            }
        });
    }

    /**
     * Removes the player from the location maps.
     *
     * @param event The PlayerQuitEvent.
     */
    @EventHandler
    public void onPlayerQuit(final @NotNull PlayerQuitEvent event) {
        FIRST_LOCATION.remove(event.getPlayer().getUniqueId()); // Remove the player from the first point map.
        SECOND_LOCATION.remove(event.getPlayer().getUniqueId()); // Remove the player from the second point map.
    }

}
