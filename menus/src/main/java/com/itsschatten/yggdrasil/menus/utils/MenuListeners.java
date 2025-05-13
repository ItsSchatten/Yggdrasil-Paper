package com.itsschatten.yggdrasil.menus.utils;

import com.itsschatten.yggdrasil.Utils;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.MenuUtils;
import com.itsschatten.yggdrasil.menus.buttons.Button;
import com.itsschatten.yggdrasil.menus.buttons.DynamicButton;
import com.itsschatten.yggdrasil.menus.types.PageMenu;
import com.itsschatten.yggdrasil.menus.types.PaginatedMenu;
import com.itsschatten.yggdrasil.menus.types.interfaces.CheckDrag;
import com.itsschatten.yggdrasil.menus.types.interfaces.NoDrag;
import com.itsschatten.yggdrasil.menus.types.interfaces.Ticking;
import com.itsschatten.yggdrasil.menus.types.interfaces.ViewerMaintained;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This class has no functionality that could be used. This is simply a listener class used to handle Menus.
 */
public final class MenuListeners implements Listener {

    /**
     * Called when the player leaves the server, used to remove the metadata 'menu_holder'.
     * Removed here because MenuHolder stores a player.
     *
     * @param event The event.
     */
    @EventHandler
    public void onPlayerLeave(final @NotNull PlayerQuitEvent event) {
        event.getPlayer().removeMetadata("menu_holder", Utils.getInstance());
    }

    /**
     * Called when a menu is closed, we ignore if the event is canceled and this method is called second to last.
     *
     * @param event The close event.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public <T extends MenuHolder> void onMenuClose(final @NotNull InventoryCloseEvent event) {
        // If the HumanEntity that closes an Inventory is somehow not a player, ignore.
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        if (event.getInventory().getHolder() instanceof MenuInventory<? extends MenuHolder> inventory) {
            // MenuHolder "cast", used to update their active and previous menu.
            final T user = (T) inventory.holder();

            if (user.getViewedMenu() != null) {
                final Menu<T> viewedMenu = (Menu<T>) user.getViewedMenu();
                viewedMenu.removeViewer(user);
                user.removeViewedMenu();
            }

            // Get the holder's current menu.
            final Menu<T> menu = (Menu<T>) user.getCurrentMenu();
            // If their menu is not null.
            if (menu != null) {
                if (menu instanceof Ticking) {
                    TickingManager.cancel(menu);
                }

                // Call onClose code.
                if (menu.closeReason() == CloseReason.DEFAULT) {
                    menu.onClose(user);
                } else if (menu.closeReason() == CloseReason.SWITCH) {
                    menu.onSwitch(user);
                }

                // Handle closing the menu for the viewer.
                if (!menu.viewers().isEmpty()) {
                    if (!(menu instanceof ViewerMaintained maintain)) {
                        menu.viewers().forEach((viewer) -> {
                            viewer.player().closeInventory(InventoryCloseEvent.Reason.CANT_USE);
                            viewer.tell("<red>The main viewer of the inventory you where viewing has closed the inventory!");
                        });
                    } else if (maintain.notifyViewers()) {
                        menu.viewers().forEach((viewer) -> viewer.tell(maintain.message()));
                    }
                }

                // Cancel all running tasks for this menu.
                menu.cancelTasks();
            } else // If the menu is null, remove their current menu anyway.
                user.removeCurrentMenu();
        }

    }

    /**
     * Click listener.
     *
     * @param event The event.
     */
    @EventHandler
    public <T extends MenuHolder> void onClickInInventory(final @NotNull InventoryClickEvent event) {
        // If somehow the HumanEntity that clicked a menu is not a player, ignore.
        if (!(event.getWhoClicked() instanceof Player))
            return;

        // Get the clicked inventory, either a proper menu or the player's inventory.
        final Inventory clickedInventory = event.getRawSlot() < 0 ? null : event.getRawSlot() < event.getView().getTopInventory().getSize() ? event.getView().getTopInventory() : event.getView().getBottomInventory();
        // Get the action of the click.
        final InventoryAction action = event.getAction();
        // Where did this player click?
        final ClickLocation clickLocation = clickedInventory != null ? (clickedInventory.getType() == InventoryType.CHEST ? ClickLocation.MENU : ClickLocation.PLAYER) : ClickLocation.OUTSIDE;

        final InventoryPosition position = InventoryPosition.fromSlot(event.getSlot());

        if (event.getInventory().getHolder() instanceof final MenuInventory<? extends MenuHolder> inv) {
            // Get the holder instance.
            final T user = (T) inv.holder();

            if (user.getViewedMenu() != null) {
                final Menu<T> viewedMenu = (Menu<T>) user.getViewedMenu();
                if (viewedMenu.viewers().contains(user)) {
                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);
                    return;
                } else {
                    user.removeViewedMenu();
                }
            }

            // Get the holder's current menu.
            final Menu<T> menu = (Menu<T>) user.getCurrentMenu();
            // If the menu is null, ignore it. Otherwise, attempt to run menu logic.
            if (menu != null) {
                if (user.player().getOpenInventory().getType() == InventoryType.CREATIVE || user.player().getOpenInventory().getType() == InventoryType.CRAFTING) {
                    // If the clicked inventory was creative or a crafting table,
                    // remove the holders currently viewed menu and ignore.
                    if (user.getCurrentMenu() != null) {
                        user.removeCurrentMenu();
                        return;
                    }
                }
                // Clicked item.
                final ItemStack clicked = event.getCurrentItem();
                // Item in the player's cursor.
                final ItemStack cursor = event.getCursor();

                // Is this action allowed?
                final boolean allowed = menu.isAllowed(user, clickLocation, event.getSlot(), clicked, cursor);

                // If the action is not allowed, set the result to deny and cancel the event.
                if (!allowed) {
                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);
                } else {
                    if (event.getClick() == ClickType.DOUBLE_CLICK) {
                        event.setResult(Event.Result.DENY);
                        event.setCancelled(true);
                        return;
                    }
                }

                // If the action was a PICKUP, PLACE, or CLONE action.
                if (action.toString().contains("PICKUP") || action.toString().contains("PLACE") || action == InventoryAction.CLONE_STACK) {
                    // Make sure the click location was a menu.
                    if (clickLocation == ClickLocation.MENU) {
                        // Attempt to handle buttons.
                        try {
                            // Attempt to find a registered button of the clicked ItemStack.
                            final Button<T> button = menu.getButton(clicked, position);

                            // If the button is not null, allow the button to handle the click,
                            // if it is null, allow the menu to handle it.
                            if (button != null) {
                                button.onClicked(user, menu, event.getClick());

                                if (button instanceof final DynamicButton<T> dynamicButton) {
                                    dynamicButton.updateInner(user, menu);
                                }
                            } else if (menu instanceof final PaginatedMenu<T, ?> pagedMenu) {
                                // Check if we are a paged men.
                                final Button<T> pageButton = pagedMenu.getPageButton(clicked, position);
                                if (pageButton != null) {
                                    pageButton.onClicked(user, menu, event.getClick());

                                    if (pageButton instanceof final DynamicButton<T> dynamicButton) {
                                        dynamicButton.updateInner(user, menu);
                                    }
                                } else {
                                    menu.onClick(user, position, event.getClick(), clicked);
                                }
                            } else if (menu instanceof final PageMenu<T> pageMenu) {
                                final Button<T> pageButton = pageMenu.getPageButton(clicked, position);
                                if (pageButton != null) {
                                    pageButton.onClicked(user, menu, event.getClick());

                                    if (pageButton instanceof final DynamicButton<T> dynamicButton) {
                                        dynamicButton.updateInner(user, menu);
                                    }
                                } else {
                                    menu.onClick(user, position, event.getClick(), clicked);
                                }
                            } else {
                                menu.onClick(user, position, event.getClick(), clicked);
                            }
                        } catch (final Throwable throwable) {
                            // Catches any errors thrown and sends a message to the viewer that something happened.

                            // If the user has the developer permission, send them information on the error that occurred.
                            MenuUtils.sendDeveloperErrorMessage(user, throwable);

                            // Close the inventory for the viewer of the inventory (to prevent any further issues)
                            user.player().closeInventory();
                            // Send them a message saying something happened.
                            user.tell("<red>An error occurred while clicking in your menu! If this happens again, please report this to staff member!");
                            // Also log the error to console.
                            Utils.logError(throwable);
                            Utils.logError("Error clicking in " + menu + " for " + user);
                        }
                    }
                    // If the location is not a menu, handle the click for a player.
                    if (clickLocation == ClickLocation.PLAYER) {
                        menu.onPlayerClick(user, event);
                    }
                    // Return, we don't want to continue anymore all logic should have been run.
                    return;
                }

                // If the action was to move to another inventory or the click location wasn't a player and wasn't handled otherwise.
                if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                    if (clickLocation == ClickLocation.PLAYER) {
                        menu.onPlayerClick(user, event);
                        return;
                    }
                    // Attempt to handle the click.
                    try {
                        // The button from the clicked item.
                        final Button<T> button = menu.getButton(clicked, position);
                        // If the button isn't null, let it handle the click, if it is null, let the menu handle it.
                        if (button != null) {
                            button.onClicked(user, menu, event.getClick());

                            if (button instanceof DynamicButton<T> dynamicButton) {
                                dynamicButton.updateInner(user, menu);
                            }
                        } else if (menu instanceof PaginatedMenu<T, ?> paginatedMenu) {
                            // Check if we are a paged menu.

                            // We are a paged menu! Attempt page button lookup.
                            final Button<T> pageButton = paginatedMenu.getPageButton(clicked, position);
                            if (pageButton != null) {
                                pageButton.onClicked(user, menu, event.getClick());

                                if (pageButton instanceof DynamicButton<T> dynamicButton) {
                                    dynamicButton.updateInner(user, menu);
                                }
                            } else {
                                menu.onClick(user, position, event.getClick(), clicked);
                            }
                        } else if (menu instanceof final PageMenu<T> pageMenu) {
                            final Button<T> pageButton = pageMenu.getPageButton(clicked, position);
                            if (pageButton != null) {
                                pageButton.onClicked(user, menu, event.getClick());

                                if (pageButton instanceof final DynamicButton<T> dynamicButton) {
                                    dynamicButton.updateInner(user, menu);
                                }
                            } else {
                                menu.onClick(user, position, event.getClick(), clicked);
                            }
                        } else {
                            menu.onClick(user, position, event.getClick(), clicked);
                        }
                    } catch (final Throwable throwable) {
                        // Catches any errors thrown and sends a message to the viewer that something happened.

                        // If the user has the developer permission, send them information on the error that occurred.
                        MenuUtils.sendDeveloperErrorMessage(user, throwable);

                        // Close the inventory for the viewer of the inventory (to prevent any further issues)
                        user.player().closeInventory();
                        // Send them a message saying something happened.
                        user.tell("<red>An error occurred while clicking in your menu! If this happens again, please report this to staff member!");
                        // Also log the error to console.
                        Utils.logError(throwable);
                        Utils.logError("Error clicking in " + menu + " for " + user);
                    }
                }
                // If the click location is a player (and wasn't handled earlier) allow the menu to handle the click.
                if (clickLocation == ClickLocation.PLAYER) {
                    menu.onPlayerClick(user, event);
                }
            }
        }
    }

    /**
     * Cancel the drag event.
     *
     * @param event the event.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public <T extends MenuHolder> void onDrag(@NotNull InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player && event.getInventory().getHolder() instanceof final MenuInventory<? extends MenuHolder> inv) {
            // Get the holder instance.
            final T user = (T) inv.holder();
            if (user == null) return;

            if (user.getViewedMenu() != null) {
                final Menu<T> viewedMenu = (Menu<T>) user.getViewedMenu();
                // We cancel dragging if the user is viewing the menu.
                if (viewedMenu.viewers().contains(user)) {
                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);
                    return;
                } else {
                    user.removeViewedMenu();
                }
            }

            // Get the user's current menu.
            final Menu<T> menu = (Menu<T>) user.getCurrentMenu();
            // If the menu is null, ignore it. Otherwise, deny drag events.
            if (menu != null) {
                if (menu instanceof final CheckDrag checkDrag) {
                    // Ignore dragging if the slot isn't in the inventory.
                    if (event.getRawSlots().stream().allMatch((slot) -> slot >= event.getInventory().getSize())) {
                        return;
                    }

                    // Actually check if we can drag.
                    if (!checkDrag.canDrag(event.getNewItems())) {
                        event.setResult(Event.Result.DENY);
                        event.setCancelled(true);
                    }
                    return;
                }

                if (!(menu instanceof NoDrag)) return;
                event.setResult(Event.Result.DENY);
                event.setCancelled(true);
            }
        }
    }

}
