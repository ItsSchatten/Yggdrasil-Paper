package com.itsschatten.yggdrasil.anvilgui;

import com.itsschatten.yggdrasil.Utils;
import com.itsschatten.yggdrasil.anvilgui.interfaces.Response;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @since 1.0.0
 */
public final class InventoryListener implements Listener {

    // If the handler is currently being run, used to ensure it isn't run multiple times.
    private boolean handlerRunning = false;

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        if (!(AnvilGUI.OPEN_ANVILS().containsKey(event.getInventory()))) {
            return;
        }

        final AnvilGUI gui = AnvilGUI.OPEN_ANVILS().get(event.getInventory());

        final int rawSlot = event.getRawSlot();
        // Not an invalid slot.
        if (rawSlot != -999) {
            final Player clicker = (Player) event.getWhoClicked();
            final Inventory clickedInventory = event.getClickedInventory();
            if (clickedInventory != null) {
                if (clickedInventory.equals(clicker.getInventory())) {
                    if (event.getClick().equals(ClickType.DOUBLE_CLICK)) {
                        event.setCancelled(true);
                        return;
                    }

                    if (event.isShiftClick()) {
                        event.setCancelled(true);
                        return;
                    }
                }

                if (event.getCursor().getType() != Material.AIR && !gui.interactableSlots().contains(rawSlot) && event.getClickedInventory().equals(gui.inventory())) {
                    event.setCancelled(true);
                    return;
                }
            }

            if (rawSlot < 3 && rawSlot >= 0 || event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                event.setCancelled(!gui.interactableSlots().contains(rawSlot));
                if (this.handlerRunning && !gui.concurrentClickHandlers()) {
                    return;
                }

                final CompletableFuture<List<Response>> actionsFuture = gui.clickHandler().apply(rawSlot, Snapshot.fromBuilt(gui));
                final Consumer<List<Response>> actionsConsumer = (actions) -> {
                    for (Response action : actions) {
                        action.accept(gui, clicker);
                    }
                };
                if (actionsFuture.isDone()) {
                    actionsFuture.thenAccept(actionsConsumer).join();
                } else {
                    this.handlerRunning = true;
                    actionsFuture.thenAcceptAsync(actionsConsumer, gui.executor()).handle((results, exception) -> {
                        if (exception != null) {
                            Utils.logError("An exception occurred in the AnvilGUI clickHandler");
                            Utils.logError(exception);
                        }

                        this.handlerRunning = false;
                        return null;
                    });
                }
            }

        }
    }

    @EventHandler
    public void onAnvilPrepare(final @NotNull PrepareAnvilEvent event) {
        if (!(AnvilGUI.OPEN_ANVILS().containsKey(event.getInventory()))) {
            return;
        }

        event.getView().setRepairCost(0);
    }

    @EventHandler
    public void onDrag(@NotNull InventoryDragEvent event) {
        if (!(AnvilGUI.OPEN_ANVILS().containsKey(event.getInventory()))) {
            return;
        }

        final AnvilGUI gui = AnvilGUI.OPEN_ANVILS().get(event.getInventory());

        // Limit dragging to only slots that can be interacted with.
        for (int slot : Slot.values()) {
            if (event.getRawSlots().contains(slot)) {
                event.setCancelled(gui.interactableSlots().contains(slot));
                break;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        if (AnvilGUI.OPEN_ANVILS().containsKey(event.getInventory())) {
            final AnvilGUI gui = AnvilGUI.OPEN_ANVILS().get(event.getInventory());
            gui.inventory().clear();
            gui.closed(false);
            if (gui.open()) {
                if (gui.preventClosing()) {
                    if (gui.response().equals(Response.close())) {
                        return;
                    }
                    gui.executor().execute(gui::openInventory);
                }
            }
        }
    }

}
