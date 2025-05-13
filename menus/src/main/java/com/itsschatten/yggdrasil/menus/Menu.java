package com.itsschatten.yggdrasil.menus;

import com.itsschatten.yggdrasil.Utils;
import com.itsschatten.yggdrasil.menus.buttons.AnimatedButton;
import com.itsschatten.yggdrasil.menus.buttons.Button;
import com.itsschatten.yggdrasil.menus.buttons.DynamicButton;
import com.itsschatten.yggdrasil.menus.types.PaginatedMenu;
import com.itsschatten.yggdrasil.menus.types.interfaces.Animated;
import com.itsschatten.yggdrasil.menus.types.interfaces.Ticking;
import com.itsschatten.yggdrasil.menus.utils.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

/**
 * The main menu class, you shouldn't extend this class,
 * instead look at some of the {@link com.itsschatten.yggdrasil.menus.types menu types}.
 *
 * @see InventorySize
 */
@ApiStatus.NonExtendable
public abstract class Menu<T extends MenuHolder> extends MenuInventory<T> {

    // TODO: Come up with a way to only call close logic when fully closing the menu.

    /**
     * All registered buttons for this menu.
     */
    private final List<Button<T>> buttons = new ArrayList<>();

    /**
     * All tasks for this menu.
     */
    @Getter
    private final Set<ReschedulableTask> tasks = new HashSet<>();

    /**
     * Utility to ensure a runnable has been run once.
     */
    @NotNull
    private final OneTimeRunnable register;
    /**
     * A list of all {@link T} viewers of this menu. A view cannot manipulate the menu they are viewing.
     */
    @Getter
    @Accessors(fluent = true)
    protected Set<T> viewers = new HashSet<>();

    /**
     * The reason this {@link Menu} was closed.
     * Will always default back to {@link CloseReason#DEFAULT} when this menu is {@link #displayTo(MenuHolder)}.
     */
    @ApiStatus.Internal
    @Setter(AccessLevel.PUBLIC)
    @Getter(AccessLevel.PUBLIC)
    @Accessors(fluent = true)
    private CloseReason closeReason = CloseReason.DEFAULT;

    /**
     * Default constructor.
     */
    @ApiStatus.Internal
    public Menu(int size, String title) {
        super(size, title);
        this.register = new OneTimeRunnable(() -> registerButtons(makeButtons()));
    }

    /**
     * Returns an unmodifiable list of all registered buttons for this menu.
     *
     * @return Returns an unmodifiable list of all {@link #buttons}.
     */
    public @UnmodifiableView List<Button<T>> buttons() {
        return Collections.unmodifiableList(buttons);
    }

    /**
     * Utility method to register multiple buttons.
     *
     * @param buttons The array of {@link Button buttons} to register.
     */
    @SafeVarargs
    public final void registerButtons(final Button<T> @NotNull ... buttons) {
        for (final Button<T> button : buttons) {
            if (registerButton(button)) {
                Utils.logWarning("Failed to register a button: " + button);
                if (Utils.isDebug()) {
                    Thread.dumpStack();
                }
            }
        }
    }

    /**
     * Register a Collection of {@link Button}
     *
     * @param buttons The buttons to register.
     */
    public final void registerButtons(final @NotNull Collection<Button<T>> buttons) {
        buttons.forEach(button -> {
            if (registerButton(button)) {
                Utils.logWarning("Failed to register a button: " + button);
                if (Utils.isDebug()) {
                    Thread.dumpStack();
                }
            }
        });
    }

    /**
     * Register a Button.
     *
     * @param button The {@link Button} to register.
     * @return <code>true</code> if the button was unsuccessfully registered, <code>false</code> if it succeeded.
     */
    private boolean registerButton(Button<T> button) {
        if (buttons.contains(button)) {
            return true;
        }

        buttons.add(button);
        if (button instanceof final AnimatedButton<T> animatedButton) {
            final ReschedulableTask task = new ReschedulableTask(animatedButton.getUpdateTime(), ReschedulableTask.Type.BUTTON) {
                @Override
                public void run() {
                    animatedButton.run(Menu.this);
                }
            };

            registerTask(task);
        }

        return false;
    }

    /**
     * Registers a {@link ReschedulableTask} to be run for this menu.
     *
     * @param task The task to register.
     */
    protected final void registerTask(final ReschedulableTask task) {
        tasks.add(task);
        task.register();
    }

    /**
     * Refresh the menu, re-registering declared buttons and redraws the menu.
     */
    public void refresh() {
        // We cancel all running button tasks because after this point they will point to nothing and will cause
        // unexpected behavior with buttons.
        tasks.forEach((task) -> {
            if (task.getType() == ReschedulableTask.Type.BUTTON) {
                task.cancel();
            }
        });

        // We remove all button tasks here to prevent memory leaks.
        tasks.removeIf((task) -> task.getType() == ReschedulableTask.Type.BUTTON);
        buttons.clear();

        registerButtons(makeButtons());
        redraw();
    }

    /**
     * Redraw the menu.
     */
    public final void redraw() {
        formInventory();
    }

    /**
     * Resets all registered buttons and re-registers declared buttons.
     */
    protected final void resetButtons() {
        if (!buttons.isEmpty()) {
            // Cancel all running button tasks.
            tasks.forEach((task) -> {
                if (task.getType() == ReschedulableTask.Type.BUTTON) {
                    task.cancel();
                }
            });
            // Remove all button tasks to help relieve memory.
            tasks.removeIf((task) -> task.getType() == ReschedulableTask.Type.BUTTON);

            this.buttons.clear();
            makeButtons();
        }
    }

    /**
     * Clears all registered buttons, mainly used in {@link PaginatedMenu}
     */
    protected final void clearButtons() {
        this.buttons.clear();
    }

    /**
     * Doesn't do anything in a normal instance, can be used to generate new buttons via {@link #resetButtons()}
     * <p>
     * It should be noted that {@link #registerButtons(Button...)} does need to be called to register the buttons.
     */
    public abstract List<Button<T>> makeButtons();

    /**
     * Used in implementations of {@link Menu} to register 'dynamic' buttons and draw other items.
     */
    public abstract void formInventory();

    /**
     * Draws the buttons to the menu.
     */
    protected final void drawButtons() {
        drawListOfButtons(buttons);
    }

    /**
     * Used to draw extra things to the menu that aren't necessarily menu buttons.
     */
    public void drawExtra() {
    }

    /**
     * Draws the buttons to the menu.
     *
     * @param toDraw A list of buttons to draw.
     */
    protected final void drawListOfButtons(final @NotNull List<Button<T>> toDraw) {
        for (final Button<T> button : toDraw) {
            if (button.getPermission() != null) {
                if (holder().player().hasPermission(button.getPermission())) {
                    if (button.getPositions() != null && !button.getPositions().isEmpty()) {
                        for (final InventoryPosition position : button.getPositions()) {
                            forceSet(position, button);
                        }
                    } else {
                        forceSet(button.getPosition(), button);
                    }
                }
                continue;
            }

            if (button.getPositions() != null && !button.getPositions().isEmpty()) {
                for (final InventoryPosition position : button.getPositions()) {
                    forceSet(position, button);
                }
            } else {
                forceSet(button.getPosition(), button);
            }
        }
    }

    /**
     * Used to switch between menus, this method avoids calling the onClose for this method and removing the player's current menu.
     *
     * @param holder The user to switch this menu for.
     * @param from   The menu this switch is called from.
     */
    public void switchMenu(final T holder, final Menu<T> from) {
        from.closeReason = CloseReason.SWITCH;
        displayTo(holder);
    }

    /**
     * Display's a menu to a user.
     *
     * @param user The user to show the menu too.
     */
    public final void displayTo(final T user) {
        // Revert the close reason to DEFAULT.
        // If the close reason was changed, it's likely that is a reopening of this menu.
        if (closeReason != CloseReason.DEFAULT) this.closeReason = CloseReason.DEFAULT;
        holder(user);

        if (!register.hasBeenRun()) {
            if (this instanceof Ticking) {
                TickingManager.add(this);
            }

            if (this instanceof final Animated animated) {
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
            if (!tasks.isEmpty()) {
                Bukkit.getScheduler().runTask(Utils.getInstance(), () -> tasks.forEach(ReschedulableTask::restart));
            }
        }
        register.attemptRun();

        Utils.debugLog("Display was called for " + getClass().getSimpleName() + ".");
        formInventory();
        onOpen(user);
        display(user);
        user.updateMenu(this);
        postDisplay(user);
    }

    /**
     * Simply shows the completed menu to the player.
     * <p><b>Showing a menu in this way does not allow the user to click or interact with this menu.
     * It is simply a way to show a completed menu to a player.</b></p>
     *
     * @param user The user to show this menu to.
     */
    public final void showTo(final T user) {
        show(user);
        user.setViewedMenu(this);
        viewers.add(user);
    }

    /**
     * Removes a viewer from the Menu, you cannot remove the main viewer of this Menu.
     *
     * @param user A {@link T}.
     */
    public final void removeViewer(final T user) {
        viewers.remove(user);
    }

    /**
     * Is this action allowed?
     *
     * @param holder        The holder of this menu.
     * @param clickLocation The location of the click.
     * @param slot          The clicked slot number.
     * @param slotItem      The item clicked.
     * @param cursorItem    The item currently on the clicker's cursor.
     * @return Returning <code>false</code> will cancel the action, while <code>true</code> will allow the action.
     */
    public boolean isAllowed(T holder, ClickLocation clickLocation, int slot, final ItemStack slotItem, final ItemStack cursorItem) {
        return false;
    }

    /**
     * Cancel all button and menu animation tasks.
     */
    public final void cancelTasks() {
        tasks.forEach(ReschedulableTask::cancel);
    }

    /**
     * What should happen when the menu is closed.
     *
     * @param user The user that closed this menu.
     */
    public void onClose(final T user) {
    }

    /**
     * What happens when the {@link Menu} is switched to.
     *
     * @param user The user that switched the menu.
     */
    public void onSwitch(final T user) {
    }

    /**
     * What should happen when the menu is opened.
     *
     * @param user The user that opened this menu.
     */
    public void onOpen(final T user) {
    }

    /**
     * What should happen after the menu has been displayed, all variables should be set for this menu at this point.
     *
     * @param user The user that closed this menu.
     */
    public void postDisplay(T user) {
    }

    /**
     * What should happen before we return to the previous menu.
     *
     * @param user The user that closed this menu.
     */
    public void beforeReturn(T user) {
    }

    /**
     * Logic for when the user of this inventory clicks in their inventory.
     *
     * @param event  The {@link InventoryClickEvent} that called this method.
     * @param holder The {@link T}.
     */
    public void onPlayerClick(T holder, final InventoryClickEvent event) {
    }

    /**
     * Gets a button from all registered buttons.
     *
     * @param stack    The stack we should look for a button.
     * @param position The position clicked.
     * @return A {@link Button} should one exist.
     */
    public abstract Button<T> getButton(final ItemStack stack, InventoryPosition position);

    /**
     * Method to get the button from an {@link ItemStack}
     *
     * @param stack   The {@link ItemStack} to search for a button with.
     * @param buttons The list of {@link Button} to search.
     * @return Returns the found {@link Button} or {@code null} if not found.
     */
    @ApiStatus.Internal
    protected final @Nullable Button<T> getButtonImpl(final ItemStack stack, final InventoryPosition position, final @NotNull List<Button<T>> buttons) {
        for (final Button<T> registeredButton : buttons) {
            // Because Animated and Dynamic are switched in runtime, it's easier if we simply defer to known item type
            // so we don't have to constantly check the item over and over again.
            final ItemStack item = switch (registeredButton) {
                case AnimatedButton<T> animated -> animated.getInnerStack().ensureServerConversions();
                case DynamicButton<T> dynamicButton -> dynamicButton.getInnerStack().ensureServerConversions();
                default -> {
                    final ItemStack instance = registeredButton.getItem();
                    yield instance == null ? null : instance.ensureServerConversions();
                }
            };

            // If a registered button has a null item return.
            if (item == null) continue;

            // Ensuring all conversions have taken place on the item,
            // we check if it is similar to the provided stack or exactly equal to it.
            if ((item.isSimilar(stack) || item.equals(stack)) && position.equals(registeredButton.getPosition())) {
                if (registeredButton.getPermission() != null) {
                    // We do have permissions, check if the main holder of the menu has permission to click the button.
                    if (!holder().hasPermission(registeredButton.getPermission()))
                        return null;
                }

                return registeredButton;
            }
        }

        return null;
    }

    /**
     * Called when clicking on the inventory.
     *
     * @param holder  The holder of this menu.
     * @param slot    The slot clicked.
     * @param click   The click type for this click.
     * @param clicked What item was clicked.
     */
    public void onClick(final T holder, final InventoryPosition slot, final ClickType click, final ItemStack clicked) {
    }

}
