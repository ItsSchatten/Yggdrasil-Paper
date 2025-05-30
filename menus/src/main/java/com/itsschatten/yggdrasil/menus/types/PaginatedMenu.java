package com.itsschatten.yggdrasil.menus.types;

import com.itsschatten.yggdrasil.Utils;
import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.AnimatedButton;
import com.itsschatten.yggdrasil.menus.buttons.Button;
import com.itsschatten.yggdrasil.menus.buttons.premade.NavigationButton;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.menus.utils.MenuHolder;
import com.itsschatten.yggdrasil.menus.utils.MenuPaginator;
import com.itsschatten.yggdrasil.menus.utils.ReschedulableTask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Indicates a menu that is capable of being paginated, allowing different items to be shown on a different page.
 *
 * @param <T> The object that is being paged.
 */
public abstract class PaginatedMenu<T extends MenuHolder, V> extends StandardMenu<T> {

    // TODO: Redo.
    //  This should be redone to better facilitate updating objects on the fly.

    /**
     * Should the items be placed in the center of the menu? (Not in the borders.)
     */
    private final boolean center;

    /**
     * Stores all page buttons, this is modeled EXACTLY like normal menu buttons.
     *
     * @see Menu
     */
    private final List<Button<T>> registeredPageButtons = new ArrayList<>();

    /**
     * The pages for this menu.
     *
     * @see MenuPaginator
     */
    private final MenuPaginator<V> paginator;

    /**
     * Should we add the page counting item?
     */
    @Setter
    @Getter
    private boolean addCounter = true;

    /**
     * If true, it will remove any navigational buttons from view if the viewer cannot go to the page.
     */
    @Setter
    @Getter
    private boolean hideNav;

    /**
     * The page that the viewer is currently on.
     */
    @Getter
    private int page = 1;

    /**
     * Base implementation.
     *
     * @param parent The parent of this menu.
     * @param pages  The List of items we want to page for this menu.
     * @param center Should we center the items in the middle of the inventory?
     */
    public PaginatedMenu(final Menu<T> parent, String title, int size, List<V> pages, boolean center) {
        super(parent, title, size);
        this.center = center;
        this.paginator = new MenuPaginator<>(getUsableFromSize(size), pages);
    }

    /**
     * Base implementation.
     *
     * @param parent The parent of this menu.
     * @param pages  The List of items we want to page for this menu.
     */
    public PaginatedMenu(final Menu<T> parent, String title, int size, List<V> pages) {
        super(parent, title, size);
        this.center = false;
        this.paginator = new MenuPaginator<>(getUsableFromSize(size), pages);
    }

    /**
     * Base implementation.
     *
     * @param parent The parent of this menu.
     * @param pages  The List of items we want to page for this menu.
     * @param center Should we center the items in the middle of the inventory?
     */
    public PaginatedMenu(final Menu<T> parent, String title, int size, @NotNull Collection<V> pages, boolean center) {
        super(parent, title, size);
        this.center = center;
        this.paginator = new MenuPaginator<>(getUsableFromSize(size), pages);
    }

    /**
     * Base implementation.
     *
     * @param parent The parent of this menu.
     * @param pages  The List of items we want to page for this menu.
     */
    public PaginatedMenu(final Menu<T> parent, String title, int size, @NotNull Collection<V> pages) {
        super(parent, title, size);
        this.center = false;
        this.paginator = new MenuPaginator<>(getUsableFromSize(size), pages);
    }

    /**
     * Utility method to register multiple buttons, registers to an internal page button list.
     *
     * @param buttons The array of {@link Button buttons} to register.
     * @see Menu#registerButtons(Button...)
     */
    @SafeVarargs
    public final void registerPageButtons(final Button<T> @NotNull ... buttons) {
        for (final Button<T> button : buttons) {
            if (!registerPageButton(button)) {
                Utils.logWarning("Failed to register a page button: " + button);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param position The position to check.
     * @return Returns {@code true} if, and only if, a {@link Button} was found to be in the same position as the one provided.
     */
    @Override
    public boolean isSlotTakenByButton(InventoryPosition position) {
        return registeredPageButtons.stream()
                .filter(button -> button.getPermission() == null || holder().hasPermission(button.getPermission()))
                .anyMatch((button) -> button.getPosition().equals(position))
                || super.isSlotTakenByButton(position);
    }

    /**
     * Register a Button.
     *
     * @param button The {@link Button} to register.
     * @return <code>true</code> if the button was successfully registered, <code>false</code> if it failed, or it was already registered.
     */
    private boolean registerPageButton(Button<T> button) {
        // We return false if the EXACT button has already been registered.
        if (registeredPageButtons.contains(button)) {
            return false;
        }

        registeredPageButtons.add(button);

        // If a button is animated, we want to register a task.
        if (button instanceof final AnimatedButton<T> animatedButton) {
            final ReschedulableTask task = new ReschedulableTask(animatedButton.getUpdateTime(), ReschedulableTask.Type.BUTTON) {
                @Override
                public void run() {
                    animatedButton.run(PaginatedMenu.this);
                }
            };

            registerTask(task);
        }

        return true;
    }

    /**
     * Gets a button from all registered buttons.
     *
     * @param stack    The stack we should look for a button.
     * @param position The clicked position.
     * @return A {@link Button} should one exist.
     * @see Menu#getButton(ItemStack, InventoryPosition)
     */
    @Nullable
    public final Button<T> getPageButton(final ItemStack stack, InventoryPosition position) {
        if (stack == null) return null;

        return getButtonImpl(stack, position, registeredPageButtons);
    }

    /**
     * Updates the list and backend pages of for this menu.
     *
     * @param list The list that should be set as this Menu's.
     */
    public final void updateValues(final Collection<V> list) {
        this.paginator.getValues().clear();
        this.paginator.getValues().addAll(list);
        this.paginator.recalculate();
    }

    /**
     * Refreshes the currently viewed page.
     */
    @Override
    public final void refresh() {
        super.refresh();
        clearPage();
        forceDrawPage();
    }

    /**
     * Updates the list of for this menu and clears the menu page.
     *
     * @param list The list that should be set as this Menu's.
     */
    public final void updatePages(final Collection<V> list) {
        updateValues(list);

        // We clear the registered normal buttons, so when we refresh, the buttons may be re-registered if required.
        clearButtons();
        clearPage();
        refresh();
        drawListOfButtons(registeredPageButtons);
    }

    /**
     * Updates the list of for this menu, and attempts to update the viewable list.
     *
     * @param list The list that should be set as this Menu's.
     */
    public final void cleanUpdatePages(final Collection<V> list) {
        updateValues(list);
        refresh();
    }

    /**
     * Attempts to clear the page spots.
     */
    private void clearPage() {
        final int usable = getUsableFromSize(getSize());
        if (center) {
            // Clear the middle slots.
            for (int i = 0; i < usable; i++) {
                forceSet(InventoryPosition.MIDDLE_POSITIONS.get(i), new ItemStack(Material.AIR));
            }
        } else {
            // Replace all placeable positions with air.
            if (getPlaceablePositions() != null && !getPlaceablePositions().isEmpty()) {
                for (int i = 0; i < usable; i++) {
                    forceSet(getPlaceablePositions().get(i), new ItemStack(Material.AIR));
                }
            } else {
                for (int i = 0; i < usable; i++) {
                    forceSet(getPlaceablePositions().get(i), new ItemStack(Material.AIR));
                }
            }
        }

        // Clear the next and counter buttons.
        if (getNextButton() != null) {
            forceSet(getNextButton().build().getPosition(), new ItemStack(Material.AIR));
        }

        if (getPreviousButton() != null) {
            forceSet(getPreviousButton().build().getPosition(), new ItemStack(Material.AIR));
        }

        if (getCounterButton() != null) {
            forceSet(getCounterButton().build().getPosition(), new ItemStack(Material.AIR));
        }
    }

    /**
     * Forcefully draws a page for page navigation.
     */
    private void forceDrawPage() {
        // We MUST clear the registered buttons first before registering new ones.
        // Failing to do so may cause double execution of a method which we don't want, especially if it references the
        // same data or the data is no longer present.
        this.registeredPageButtons.clear();

        drawExtra();
        updateTitleAndButtons();

        initializePage();
        drawListOfButtons(registeredPageButtons);
    }

    /**
     * Draws a page.
     */
    private void initializePage() {
        // Define our usable so we can place them in the correct slots.
        final int usable = getUsableFromSize(getSize());

        // Use center positions.
        if (center) {
            for (int i = 0; i < usable; i++) {
                if (getPageValues().size() <= i) break;
                registerPageButtons(makeButton(i, InventoryPosition.MIDDLE_POSITIONS));
            }
        } else {
            // Use the set placeable positions.
            if (getPlaceablePositions() != null && !getPlaceablePositions().isEmpty()) {
                for (int i = 0; i < usable; i++) {
                    if (getPageValues().size() <= i) break;
                    registerPageButtons(makeButton(i, getPlaceablePositions()));
                }
            } else {
                // Use the usable positions.
                for (int i = 0; i < usable + 1; i++) {
                    if (getPageValues().size() <= i) break;
                    registerPageButtons(makeButtonCalculated(i));
                }
            }
        }
    }

    /**
     * Make a button with a set position.
     *
     * @param iteration          The iteration we are on.
     * @param placeablePositions The positions that could be placed.
     * @return Returns a new {@link Button}.
     */
    private @NotNull Button<T> makeButton(int iteration, final List<InventoryPosition> placeablePositions) {
        final V obj = getPageValues().get(iteration);
        return new Button<>() {
            @Override
            public ItemCreator createItem() {
                return convertToStack(obj);
            }

            @Override
            public void onClicked(T user, Menu<T> menu, ClickType click) {
                onClickPageItem(user, obj, click);
            }

            @Override
            public @NotNull InventoryPosition getPosition() {
                return placeablePositions.get(iteration);
            }
        };
    }

    /**
     * Make a button with a calculated position in the inventory.
     *
     * @param iteration The iteration we are on.
     * @return Returns a new {@link Button}.
     */
    private @NotNull Button<T> makeButtonCalculated(int iteration) {
        final V obj = getPageValues().get(iteration);
        return new Button<>() {
            @Override
            public ItemCreator createItem() {
                return convertToStack(obj);
            }

            @Override
            public void onClicked(final T user, final Menu<T> menu, final ClickType click) {
                onClickPageItem(user, obj, click);
            }

            @Override
            public @NotNull InventoryPosition getPosition() {
                final int row = iteration / 9;
                final int column = iteration % 9;
                return InventoryPosition.of(row, column);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public void formInventory() {
        // Do pages first, that way we can still access them in all methods.
        super.formInventory();

        initializePage();
        updateTitleAndButtons();
        drawListOfButtons(registeredPageButtons);
    }

    /**
     * Sets the titles and buttons for this menu.
     */
    private void updateTitleAndButtons() {
        // Are we adding a counter, and do we have a button to use?
        if (addCounter && getCounterButton() != null) {
            // Are we hiding navigation?
            // If we are, check if we have more than 1 page.
            if (hideNav) {
                if (this.paginator.getPages().size() > 1) {
                    registerPageButtons(getCounterButton().build());
                }
            } else {
                // We are not hiding navigation, register the counter.
                registerPageButtons(getCounterButton().build());
            }
        }

        // Are we hiding the navigation?
        if (hideNav) {
            // Check if we can go to the next or previous pages and ensure the button isn't null.
            // Then register the button.
            final boolean hasNext = page < this.paginator.getPages().size();
            if (hasNext && getNextButton() != null) {
                registerPageButtons(getNextButton().build());
            }

            final boolean hasPrevious = page > 1;
            if (hasPrevious && getPreviousButton() != null) {
                registerPageButtons(getPreviousButton().build());
            }
        } else {
            // We aren't, register the buttons.
            if (getNextButton() != null) {
                registerPageButtons(getNextButton().build());
            }
            if (getPreviousButton() != null) {
                registerPageButtons(getPreviousButton().build());
            }
        }
    }

    /**
     * Convert the object into an item.
     *
     * @param object The object.
     * @return An {@link ItemCreator} instance.
     */
    public abstract ItemCreator convertToStack(V object);

    /**
     * What should happen when we click on a page object.
     *
     * @param user   The holder of this menu.
     * @param object The object clicked.
     * @param click  The click type for this click.
     */
    public abstract void onClickPageItem(final T user, final V object, ClickType click);

    /**
     * {@inheritDoc}
     *
     * @param user     The holder of this menu.
     * @param position The position clicked.
     * @param click    The click type for this click.
     * @param clicked  What item was clicked?
     */
    @Override
    public void onClick(@NotNull T user, @NotNull InventoryPosition position, ClickType click, ItemStack clicked) {
        // Ignore outside clicks.
        if (user.player().getOpenInventory().getSlotType(position.getEffectiveSlot()) == InventoryType.SlotType.OUTSIDE)
            return;

        // We are using center positions.
        if (center) {
            // Ensure we clicked a valid location and that we have values.
            if (getPageValues().isEmpty() || !InventoryPosition.MIDDLE_POSITIONS.contains(position)) {
                return;
            }

            // Make sure we aren't over the value size.
            if (InventoryPosition.MIDDLE_POSITIONS.indexOf(position) >= getPageValues().size()) {
                return;
            }

            // Get the value and if it isn't null, go ahead and call the click.
            final V object = getPageValues().get(InventoryPosition.MIDDLE_POSITIONS.indexOf(position) - 1);
            if (object != null) {
                onClickPageItem(user, object, click);
            }
        } else {
            // Check if we have placeable positions.
            if (getPlaceablePositions() != null && !getPlaceablePositions().isEmpty()) {
                // Ensure we clicked a valid location and that we have values.
                if (getPageValues().isEmpty() || !getPlaceablePositions().contains(position)) {
                    return;
                }

                // Make sure we aren't over the value size.
                if (getPlaceablePositions().indexOf(position) >= getPageValues().size()) {
                    return;
                }

                // Get the value and if it isn't null, go ahead and call the click.
                final V object = getPageValues().get(getPlaceablePositions().indexOf(position));
                if (object != null) {
                    onClickPageItem(user, object, click);
                }
                return;
            }

            // Ensure we aren't over the value size.
            if (position.getEffectiveSlot() < getPageValues().size()) {
                // Get the value and if it isn't null, go ahead and call the click.
                final V object = getPageValues().get(position.getEffectiveSlot());
                if (object != null) {
                    onClickPageItem(user, object, click);
                }
            }
        }
    }

    /**
     * Get the counter {@link String} used in {@link #getCounterButton()} and the title if that is enabled.
     *
     * @return The current page string.
     */
    @NotNull
    public String getCounterString() {
        return getTotalPages() > 1 ? "<dark_aqua>" + getPage() + "</dark_aqua><dark_gray>/</dark_gray><gray>" + getTotalPages() : "<yellow>No More Pages!";
    }

    /**
     * Get the total number of pages.
     *
     * @return The number of pages.
     */
    public final int getTotalPages() {
        return this.paginator.getPages().size();
    }

    /**
     * A {@link NavigationButton} used to show what page the viewer is on.
     *
     * @return Returns a {@link NavigationButton} builder.
     * @see NavigationButton#builder()
     */
    @Nullable
    public NavigationButton.NavigationButtonBuilder<T> getCounterButton() {
        return NavigationButton.<T>builder()
                .material(Material.NAME_TAG)
                .name(getCounterString())
                .lore(this.paginator.getPages().size() > 1 ? List.of("Click me to be sent back to the first page.", "Or right click to be sent to the last page!") : List.of())
                .runnable((user, menu, type) -> {
                    if (type == ClickType.RIGHT) {
                        this.page = Math.max(this.paginator.getPages().size(), 1);
                        refresh();
                        return;
                    }

                    this.page = 1;
                    refresh();
                })
                .position(InventoryPosition.of(rows() - 1, 4));
    }

    /**
     * A {@link NavigationButton} used to go to the next page.
     *
     * @return Returns a {@link NavigationButton} builder.
     * @see NavigationButton#builder()
     */
    @Nullable
    public NavigationButton.NavigationButtonBuilder<T> getNextButton() {
        return NavigationButton.<T>builder()
                .material(Material.ARROW)
                .name("<yellow>Next >")
                .runnable((user, menu, type) -> {
                    final boolean canGo = page < this.paginator.getPages().size();
                    if (canGo) {
                        this.page = Utils.range(page + 1, 1, this.paginator.getPages().size());
                        refresh();
                    } else {
                        menu.holder().tell("<red>You cannot go forward any further!");
                    }
                })
                .position(InventoryPosition.of(rows() - 1, 5));
    }

    /**
     * A {@link NavigationButton} used to return to the previous page.
     *
     * @return Returns a {@link NavigationButton} builder.
     * @see NavigationButton#builder()
     */
    @Nullable
    public NavigationButton.NavigationButtonBuilder<T> getPreviousButton() {
        return NavigationButton.<T>builder()
                .material(Material.ARROW)
                .name("<yellow>< Previous")
                .runnable((user, menu, type) -> {
                    final boolean canGo = page > 1;
                    if (canGo) {
                        this.page = Utils.range(page - 1, 1, this.paginator.getPages().size());
                        refresh();
                    } else {
                        menu.holder().tell("<red>You cannot go backwards any further!");
                    }
                })
                .position(InventoryPosition.of(rows() - 1, 3));
    }

    /**
     * Gets the usable number of slots from the size.
     *
     * @param size The size of the inventory.
     * @return The proper size.
     */
    private int getUsableFromSize(final int size) {
        // Are we centered?
        if (center) {
            if (size < 27)
                throw new UnsupportedOperationException("Size must be 27 or higher to center items in the menu.");

            // Return all centered items (at minimum one space away from the edges).
            return size == 54 ? 28 : (size == 45 ? 21 : (size == 36 ? 14 : 7));
        }

        // Ensure we have placeable positions if we do return the size.
        if (getPlaceablePositions() != null && !getPlaceablePositions().isEmpty()) {
            return getPlaceablePositions().size();
        }

        // Using the size of the inventory, always leave one empty row.
        return size == 54 ? 45 : (size == 45 ? 36 : (size == 36 ? 27 : (size == 27 ? 18 : 9)));
    }

    /**
     * The list of placeable positions.
     *
     * @return A list of {@link InventoryPosition}s.
     * @see InventoryPosition
     */
    @Nullable
    @Unmodifiable
    public List<InventoryPosition> getPlaceablePositions() {
        return null;
    }

    /**
     * Re-init a menu.
     *
     * @param objects The list of objects to re-populate the menu with.
     */
    public final void reInit(final Collection<V> objects) {
        this.paginator.getValues().clear();
        this.paginator.getValues().addAll(objects);
        this.page = 1;
        refresh();
    }

    /**
     * Re-init a menu.
     *
     * @param objects The list of objects to re-populate the menu with.
     * @param delay   How long before re-init.
     */
    public final void reInit(Collection<V> objects, long delay) {
        Bukkit.getScheduler().runTaskLater(Utils.getInstance(), () -> reInit(objects), delay);
    }

    /**
     * Adds a value to the {@link MenuPaginator} values and refreshes the menu.
     *
     * @param val The value to add.
     */
    public final void addValue(final V val) {
        this.paginator.getValues().add(val);
        this.paginator.recalculate();
        refresh();
    }

    /**
     * Removes a value from the {@link MenuPaginator} values and refreshed the menu.
     *
     * @param val The value to remove.
     */
    public final void removeValue(final V val) {
        this.paginator.getValues().remove(val);
        this.paginator.recalculate();

        // If we remove the last entry in the viewed page, revert to page 1.
        while (this.paginator.getPages().size() < this.page) {
            this.page--;
        }

        refresh();
    }

    /**
     * Returns an unmodifiable list of all values that are being paginated by this menu.
     *
     * @return Returns an unmodifiable copy of {@link MenuPaginator#getValues()}.
     */
    public final @NotNull @Unmodifiable List<V> getValues() {
        return List.copyOf(this.paginator.getValues());
    }

    /**
     * Get the current pages values.
     *
     * @return The list of objects.
     */
    @NotNull
    @Unmodifiable
    public final List<V> getPageValues() {
        if (this.page == 0 || this.paginator.getPages().isEmpty()) return Collections.emptyList();
        org.apache.commons.lang3.Validate.isTrue(this.paginator.getPages().containsKey(this.page - 1), "Menu " + this.getClass().getSimpleName() + " does not contain page #" + (this.page - 1));

        return Collections.unmodifiableList(this.paginator.getPages().get(this.page - 1));
    }
}
