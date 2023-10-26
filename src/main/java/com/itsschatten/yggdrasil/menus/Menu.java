package com.itsschatten.yggdrasil.menus;

import com.itsschatten.yggdrasil.Utils;
import com.itsschatten.yggdrasil.menus.utils.*;
import com.itsschatten.yggdrasil.menus.buttons.AnimatedButton;
import com.itsschatten.yggdrasil.menus.buttons.Button;
import com.itsschatten.yggdrasil.menus.buttons.DynamicButton;
import com.itsschatten.yggdrasil.menus.buttons.SimpleAnimatedButton;
import com.itsschatten.yggdrasil.menus.types.PagedMenu;
import com.itsschatten.yggdrasil.menus.types.interfaces.Animated;
import com.itsschatten.yggdrasil.menus.types.interfaces.Ticking;
import com.itsschatten.yggdrasil.menus.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The main menu class, you shouldn't extend this class,
 * instead look at some of the {@link com.itsschatten.yggdrasil.menus.types Types}
 */
public abstract class Menu extends AbstractMenuInventory {

    /**
     * Constant to get a player's current menu.
     */
    public static final String CURRENT_TAG = "current_menu";
    /**
     * Constant to get a player's previous menu.
     */
    public static final String PREVIOUS_TAG = "previous_menu";

    /**
     * Constant to get a player's viewed menu.
     */
    public static final String VIEWED_TAG = "viewed_menu";


    /**
     * All registered buttons for this menu.
     */
    private final List<Button> registeredButtons = new ArrayList<>();

    /**
     * All tasks for this menu.
     */
    private final Set<ReschedulableTask> reschedulableTasks = new HashSet<>();

    /**
     * Utility to ensure a runnable has been run once.
     */
    private final OneTimeRunnable register;

    /**
     * The {@link IMenuHolder} of this inventory.
     */
    @Getter
    @Setter
    protected IMenuHolder viewer;

    /**
     * The secondary viewers to this menu.
     * <br>
     * <b>This DOES NOT contain the main viewer of this inventory.</b>
     */
    @Getter
    protected Set<IMenuHolder> viewers = new HashSet<>();

    /**
     * Are we opening a new menu?
     */
    @Getter
    private boolean isOpeningNew = false;

    /**
     * Has this menu been registered.
     */
    @Getter
    private boolean hasRegistered;

    /**
     * Are we redrawing this menu?
     */
    @Getter
    @Setter
    private boolean redrawing;

    /**
     * Default constructor.
     */
    public Menu() {
        this.register = new OneTimeRunnable(this::makeButtons);
    }

    /**
     * Utility method to register multiple buttons.
     *
     * @param buttons The array of {@link Button buttons} to register.
     */
    public final void registerButtons(final Button @NotNull ... buttons) {
        for (final Button button : buttons) {
            if (!registerButton(button)) {
                Utils.debugLog("Failed to register a button: " + button);
            }
        }
    }

    /**
     * Register a Button.
     *
     * @param button The {@link Button} to register.
     * @return <code>true</code> if the button was successfully registered, <code>false</code> if it failed, or it was already registered.
     */
    private boolean registerButton(Button button) {
        if (registeredButtons.contains(button)) {
            return false;
        }

        registeredButtons.add(button);
        if (button instanceof final AnimatedButton animatedButton) {
            final ReschedulableTask task = new ReschedulableTask(animatedButton.getUpdateTime(), ReschedulableTask.Type.BUTTON) {
                @Override
                public void run() {
                    animatedButton.run(Menu.this);
                }
            };

            registerTask(task);
        }

        if (button instanceof final SimpleAnimatedButton animatedButton) {
            final ReschedulableTask task = new ReschedulableTask(animatedButton.getUpdateTime(), ReschedulableTask.Type.BUTTON) {
                @Override
                public void run() {
                    animatedButton.run(Menu.this);
                }
            };

            registerTask(task);
        }
        return true;
    }

    /**
     * Registers a {@link ReschedulableTask} to be run for this menu.
     *
     * @param task The task to register.
     */
    protected final void registerTask(final ReschedulableTask task) {
        reschedulableTasks.add(task);
        task.register();
    }

    /**
     * Is the potion provided taken by a button?
     *
     * @param position The position that we wish to check.
     * @return <code>true</code> if a button is found in the provided position, <code>false</code> otherwise.
     */
    @Override
    public final boolean isSlotTakenByButton(final InventoryPosition position) {
        for (final Button button : registeredButtons) {
            if (button.getPosition() == null || (button.getPermission() != null && !viewer.getBase().hasPermission(button.getPermission().getPermission())))
                return false;
            if (button.getPosition().equals(position)) return true;
        }
        return false;
    }

    /**
     * Gets a new instance of this class.
     *
     * @return The new instance of the menu.
     */
    public Menu newInstance() {
        return instantiate(getClass());
    }

    /**
     * Refresh the menu, re-registering declared buttons and redraws the menu.
     */
    public final void refresh() {
        // Cancel all running button tasks.
        reschedulableTasks.forEach((task) -> {
            if (task.getType() == ReschedulableTask.Type.BUTTON) {
                task.cancel();
            }
        });
        // Remove all button tasks to help relieve memory.
        reschedulableTasks.removeIf((task) -> task.getType() == ReschedulableTask.Type.BUTTON);
        registeredButtons.clear();

        makeButtons();
        // viewer.getBase().getOpenInventory().getTopInventory().clear();
        redraw();
    }

    /**
     * Redraw the menu.
     */
    public final void redraw() {
        setRedrawing(true);
        setInventory(formInventory());

        //viewer.getBase().getOpenInventory().getTopInventory().setContents(getInventory().getBukkitInventory().getContents());
        getInventory().display(viewer);
    }

    /**
     * Resets all registered buttons and re-registers declared buttons.
     */
    protected final void resetButtons() {
        if (!registeredButtons.isEmpty()) {
            // Cancel all running button tasks.
            reschedulableTasks.forEach((task) -> {
                if (task.getType() == ReschedulableTask.Type.BUTTON) {
                    task.cancel();
                }
            });
            // Remove all button tasks to help relieve memory.
            reschedulableTasks.removeIf((task) -> task.getType() == ReschedulableTask.Type.BUTTON);

            this.registeredButtons.clear();
            makeButtons();
        }
    }

    /**
     * Clears all registered buttons, mainly used in {@link PagedMenu}
     */
    protected final void clearButtons() {
        this.registeredButtons.clear();
    }

    /**
     * Doesn't do anything in a normal instance, can be used to generate new buttons via {@link #resetButtons()}
     * <p>
     * It should be noted that {@link #registerButtons(Button...)} does need to be called in order to register the buttons.
     */
    public void makeButtons() {
    }

    /**
     * Instantiate a {@link Menu}, creating a new instance.
     *
     * @param clazz The class to make a new instance of.
     * @param <T>   What class type?
     * @return The new instance, or null if errors.
     */
    private <T> @Nullable T instantiate(@NotNull Class<T> clazz) {
        try {
            final Constructor<T> c = clazz.getDeclaredConstructor();
            c.setAccessible(true);

            return c.newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Draws the buttons to the menu.
     */
    protected final void drawButtons() {
        drawListOfButtons(registeredButtons);
    }

    /**
     * Draws the buttons to the menu.
     *
     * @param toDraw A list of buttons to draw.
     */
    protected final void drawListOfButtons(final @NotNull List<Button> toDraw) {
        for (final Button button : toDraw) {
            if (button.getPermission() != null) {
                if (getViewer().getBase().hasPermission(button.getPermission().getPermission())) {
                    getInventory().forceSet(button.getPosition(), button);
                }
                continue;
            }
            getInventory().forceSet(button.getPosition(), button);
        }
    }

    /**
     * Quick method to open a menu.
     *
     * @param user The user to open the menu for.
     * @see #displayTo(IMenuHolder, boolean)
     */
    public void displayTo(final IMenuHolder user) {
        displayTo(user, false);
    }

    /**
     * Used to switch between menus, this method avoids calling the onClose for this method and removing the player's current menu.
     *
     * @param user The user to switch this menu for.
     * @param from The menu this switch is called from.
     */
    public void switchMenu(final IMenuHolder user, final Menu from) {
        if (from != null)
            from.isOpeningNew = true;
        displayTo(user);
    }

    /**
     * Display's a menu to a user.
     *
     * @param user  The user to show the menu too.
     * @param force Should we force this menu to be opened if a player is in a conversation?
     */
    public final void displayTo(final IMenuHolder user, final boolean force) {
        setViewer(user);
        register.attemptRun();

        if (redrawing) {
            makeButtons();
        }

        if (!hasRegistered) {
            if (this instanceof Ticking) {
                TickingManager.add(this);
            }

            if (this instanceof Animated animated) {
                if (animated.getDelay() > -1) {
                    final ReschedulableTask task = new ReschedulableTask(animated.getDelay(), ReschedulableTask.Type.MENU) {
                        @Override
                        public void run() {
                            animated.animate();
                        }
                    };

                    registerTask(task);
                }
            }
        } else {
            if (reschedulableTasks.size() > 0) {
                Bukkit.getScheduler().runTask(Utils.getInstance(), () -> reschedulableTasks.forEach(ReschedulableTask::restart));
            }
        }

        hasRegistered = true;

        if (!force && user.getBase().isConversing()) {
            user.tell("<red>Please exit your conversation and try opening this menu again!");
            return;
        }
        setInventory(formInventory());

        onOpen(user);
        Utils.debugLog("Display is called for " + getClass().getSimpleName() + ".");
        getInventory().display(user);
        user.updateMenu(this);
    }

    /**
     * Simply shows the completed menu to the player.
     * <p><b>Showing a menu in this way does not allow the user to click or interact with this menu.
     * It is simply a way to show a completed menu to a player.</b></p>
     *
     * @param user The user to show this menu to.
     */
    public final void showTo(final IMenuHolder user) {
        if (getInventory() == null) {
            throw new UnsupportedOperationException("Cannot show a menu that has not already been displayed to a primary user.");
        }

        getInventory().show(user);
        user.setViewedMenu(this);
        viewers.add(user);
    }

    /**
     * Removes a viewer from the Menu, you cannot remove the main viewer of this Menu.
     *
     * @param user A {@link IMenuHolder}
     */
    public final void removeViewer(final IMenuHolder user) {
        viewers.remove(user);
    }

    /**
     * Is this action allowed?
     *
     * @param clickLocation The location of the click.
     * @param slot          The clicked slot number.
     * @param slotItem      The item clicked.
     * @param cursorItem    The item currently on the clicker's cursor.
     * @return Returning <code>false</code> will cancel the action, while <code>true</code> will allow the action.
     */
    public boolean isAllowed(ClickLocation clickLocation, int slot, final ItemStack slotItem, final ItemStack cursorItem) {
        return false;
    }

    /**
     * Cancel all button and menu animation tasks.
     */
    public final void cancelTasks() {
        if (!isOpeningNew) {
            reschedulableTasks.forEach(ReschedulableTask::cancel);
        } else {
            if (viewer.getPreviousMenu() == this) {
                viewer.getPreviousMenu().isOpeningNew = false;
                return;
            }
            if (viewer.getPreviousMenu() != null && viewer.getPreviousMenu() != this) {
                viewer.getPreviousMenu().cancelTasks();
            }

            reschedulableTasks.forEach(ReschedulableTask::cancel);
            isOpeningNew = false;
        }
    }

    /**
     * What should happen when the menu is closed.
     *
     * @param user The user that belongs to this menu.
     */
    public void onClose(final IMenuHolder user) {
    }

    /**
     * What should happen when the menu is opened.
     *
     * @param user The user that belongs to this menu.
     */
    public void onOpen(final IMenuHolder user) {
    }

    /**
     * What should happen before we return to the previous menu.
     */
    public void beforeReturn() {
    }

    /**
     * Logic for when the user of this inventory clicks in their inventory.
     *
     * @param event The {@link InventoryClickEvent} that called this method.
     */
    public void onPlayerClick(final InventoryClickEvent event) {
    }

    /**
     * Gets a button from all registered buttons.
     *
     * @param stack The stack we should look for a button.
     * @return A {@link Button} should one exist.
     */
    public Button getButton(final ItemStack stack) {
        if (stack == null) return null;
        for (final Button registeredButton : registeredButtons) {
            if (registeredButton instanceof final AnimatedButton animatedButton) {
                if (animatedButton.getInner().isSimilar(stack)) {
                    return animatedButton;
                }
            }

            if (registeredButton instanceof final DynamicButton dynamic) {
                if (dynamic.getInnerStack().isSimilar(stack)) {
                    return dynamic;
                }
            }

            if (registeredButton.getItem() == null) continue;

            if (registeredButton.getItem().isSimilar(stack) || registeredButton.getItem().equals(stack)) {
                if (registeredButton.getPermission() != null)
                    if (!getViewer().getBase().hasPermission(registeredButton.getPermission().getPermission()))
                        return null;
                return registeredButton;
            }
        }
        return null;
    }

    /**
     * Called when clicking on the inventory.
     *
     * @param user    The holder of this menu.
     * @param slot    The slot clicked.
     * @param click   The click type for this click.
     * @param clicked What item was clicked.
     */
    public void onClick(final IMenuHolder user, final InventoryPosition slot, final ClickType click, final ItemStack clicked) {
    }
}
