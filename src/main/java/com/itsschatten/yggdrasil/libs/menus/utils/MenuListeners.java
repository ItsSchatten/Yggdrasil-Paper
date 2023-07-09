package com.itsschatten.yggdrasil.libs.menus.utils;

import com.itsschatten.yggdrasil.libs.Utils;
import com.itsschatten.yggdrasil.libs.menus.Menu;
import com.itsschatten.yggdrasil.libs.menus.buttons.Button;
import com.itsschatten.yggdrasil.libs.menus.types.PagedMenu;
import com.itsschatten.yggdrasil.libs.menus.types.interfaces.CheckDrag;
import com.itsschatten.yggdrasil.libs.menus.types.interfaces.NoDrag;
import com.itsschatten.yggdrasil.libs.menus.types.interfaces.Ticking;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This class has no functionality that could be used. This is simply a listener class used to handle Menus.
 */
public final class MenuListeners implements Listener {

    /**
     * Called when a menu is closed, we ignore if the event is canceled and this method is called second to last.
     *
     * @param event The close event.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onMenuClose(final @NotNull InventoryCloseEvent event) {
        // If the HumanEntity that closes an Inventory is somehow not a player, ignore.
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        if (event.getInventory().getHolder() instanceof MenuInventory) {
            // MenuHolder "cast", used to update their active and previous menu.
            final IMenuHolder user = Utils.getManager().getMenuHolder(event.getPlayer().getUniqueId());
            // If the user is null or their base is null, ignore.
            if (user == null || user.getBase() == null)
                return;

            if (user.getViewedMenu() != null) {
                final Menu viewedMenu = user.getViewedMenu();
                viewedMenu.removeViewer(user);
                user.removeViewedMenu();
            }

            // Get the holder's current menu.
            final Menu menu = user.getCurrentMenu();
            // If their menu is not null.
            if (menu != null) {
                // Check if the menu is being redrawn.
                if (menu.isRedrawing()) {
                    menu.setRedrawing(false);
                    return;
                }

                if (menu instanceof Ticking) {
                    TickingManager.cancel(menu);
                }

                // Call onClose code.
                menu.onClose(user);

                // Close the animate title task.
                if (menu.getInventory().getAnimateTask() != 0) {
                    Bukkit.getScheduler().cancelTask(menu.getInventory().getAnimateTask());
                    menu.getInventory().clearAnimateTask();
                }

                // If we aren't opening a new menu, remove their current menu.
                if (!menu.isOpeningNew()) {
                    user.removeCurrentMenu();
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
    public void onClickInInventory(final @NotNull InventoryClickEvent event) {
        // If somehow the HumanEntity that clicked a menu is not a player, ignore.
        if (!(event.getWhoClicked() instanceof Player))
            return;

        // Get the clicked inventory, either a proper menu or the player's inventory.
        final Inventory clickedInventory = event.getRawSlot() < 0 ? null : event.getRawSlot() < event.getView().getTopInventory().getSize() ? event.getView().getTopInventory() : event.getView().getBottomInventory();
        // Get the action of the click.
        final InventoryAction action = event.getAction();
        // Where did this player click?
        final ClickLocation clickLocation = clickedInventory != null ? (clickedInventory.getType() == InventoryType.CHEST ? ClickLocation.MENU : ClickLocation.PLAYER) : ClickLocation.OUTSIDE;

        if (!(event.getInventory().getHolder() instanceof MenuInventory)) {
            // If the click location is a player (and wasn't handled earlier) allow the menu to handle the click.
            if (clickLocation == ClickLocation.PLAYER) {
                final Menu menu = Utils.getManager().getMenuHolder((Player) event.getWhoClicked()).getCurrentMenu();
                if (menu != null) {
                    menu.onPlayerClick(event);
                }
            }
            return;
        }

        if (event.getInventory().getHolder() instanceof MenuInventory) {
            // Get the holder instance.
            final IMenuHolder user = Utils.getManager().getMenuHolder((Player) event.getWhoClicked());

            if (user.getViewedMenu() != null) {
                final Menu viewedMenu = user.getViewedMenu();
                if (viewedMenu.getViewers().contains(user)) {
                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);
                    return;
                } else {
                    user.removeViewedMenu();
                }
            }

            // Get the holder's current menu.
            final Menu menu = user.getCurrentMenu();
            // If the menu is null, ignore it. Otherwise, attempt to run menu logic.
            if (menu != null) {
                if (user.getBase().getOpenInventory().getType() == InventoryType.CREATIVE || user.getBase().getOpenInventory().getType() == InventoryType.CRAFTING) {
                    // If the clicked inventory was creative or a crafting table, remove the holders current menu and ignore.
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
                final boolean allowed = menu.isAllowed(clickLocation, event.getSlot(), clicked, cursor);

                // If the action is not allowed, set the result to denied and cancel the event.
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
                            // Attempt to find a registered button of the clicked itemstack.
                            final Button button = menu.getButton(clicked);

                            // If the button is not null, allow the button to handle the click,
                            // if it is null, allow the menu to handle it.
                            if (button != null) {
                                button.onClicked(user, menu, event.getClick());
                            } else if (menu instanceof PagedMenu<?> pagedMenu) {
                                // Check if we are a paged men.

                                // We are a paged menu! Attempt page button lookup.
                                final Button pageButton = pagedMenu.getPageButton(clicked);
                                if (pageButton != null) {
                                    pageButton.onClicked(user, menu, event.getClick());
                                } else {
                                    menu.onClick(user, menu.getPositionFromSlot(event.getSlot()), event.getClick(), clicked);
                                }
                            } else {
                                menu.onClick(user, menu.getPositionFromSlot(event.getSlot()), event.getClick(), clicked);
                            }
                        } catch (final Throwable throwable) {
                            // Catches any errors thrown and sends a message to the viewer that something happened.

                            // If the user has the developer permission, send them information on the error that occurred.
                            Utils.sendDeveloperErrorMessage(user, throwable);

                            // Close the inventory for the viewer of the inventory (to prevent any further issues)
                            user.getBase().closeInventory();
                            // Send them a message saying something happened.
                            user.tell("<red>An error occurred while clicking in your menu! If this happens again, please report this to staff member!");
                            // Also log the error to console.
                            Utils.logError(throwable);
                            Utils.logError("Error clicking in " + menu + " for " + user);
                        }
                    }
                    // If the location is not a menu, handle the click for a player.
                    if (clickLocation == ClickLocation.PLAYER) {
                        menu.onPlayerClick(event);
                    }
                    // Return, we don't want to continue anymore all logic should have been run.
                    return;
                }

                // If the action was to move to another inventory or the click location wasn't a player and wasn't handled otherwise.
                if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                    if (clickLocation == ClickLocation.PLAYER) {
                        menu.onPlayerClick(event);
                        return;
                    }
                    // Attempt to handle the click.
                    try {
                        // The button from the clicked item.
                        final Button button = menu.getButton(clicked);
                        // If the button isn't null, let it handle the click, if it is null, let the menu handle it.
                        if (button != null) {
                            button.onClicked(user, menu, event.getClick());
                        } else if (menu instanceof PagedMenu<?> pagedMenu) {
                            // Check if we are a paged men.

                            // We are a paged menu! Attempt page button lookup.
                            final Button pageButton = pagedMenu.getPageButton(clicked);
                            if (pageButton != null) {
                                pageButton.onClicked(user, menu, event.getClick());
                            } else {
                                menu.onClick(user, menu.getPositionFromSlot(event.getSlot()), event.getClick(), clicked);
                            }
                        } else {
                            menu.onClick(user, menu.getPositionFromSlot(event.getSlot()), event.getClick(), clicked);
                        }
                    } catch (final Throwable throwable) {
                        // Catches any errors thrown and sends a message to the viewer that something happened.

                        // If the user has the developer permission, send them information on the error that occurred.
                        Utils.sendDeveloperErrorMessage(user, throwable);

                        // Close the inventory for the viewer of the inventory (to prevent any further issues)
                        user.getBase().closeInventory();
                        // Send them a message saying something happened.
                        user.tell("<red>An error occurred while clicking in your menu! If this happens again, please report this to staff member!");
                        // Also log the error to console.
                        Utils.logError(throwable);
                        Utils.logError("Error clicking in " + menu + " for " + user);
                    }
                }
                // If the click location is a player (and wasn't handled earlier) allow the menu to handle the click.
                if (clickLocation == ClickLocation.PLAYER) {
                    menu.onPlayerClick(event);
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
    public void onDrag(@NotNull InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof MenuInventory) {
            // Get the holder instance.
            final IMenuHolder user = Utils.getManager().getMenuHolder((Player) event.getWhoClicked());

            if (user.getViewedMenu() != null) {
                final Menu viewedMenu = user.getViewedMenu();
                // An instance of no drag, cancel dragging.
                if (viewedMenu.getViewers().contains(user)) {
                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);
                    return;
                } else {
                    user.removeViewedMenu();
                }
            }

            // Get the user's current menu.
            final Menu menu = user.getCurrentMenu();
            // If the menu is null, ignore it. Otherwise, deny drag events.
            if (menu != null) {
                if (menu instanceof CheckDrag checkDrag) {
                    if (event.getRawSlots().stream().allMatch((slot) -> slot >= event.getInventory().getSize())) {
                        return;
                    }

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
