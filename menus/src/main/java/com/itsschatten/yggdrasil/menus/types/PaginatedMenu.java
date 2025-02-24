package com.itsschatten.yggdrasil.menus.types;

import com.itsschatten.yggdrasil.Utils;
import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.items.ItemOptions;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.AnimatedButton;
import com.itsschatten.yggdrasil.menus.buttons.Button;
import com.itsschatten.yggdrasil.menus.buttons.premade.NavigationButton;
import com.itsschatten.yggdrasil.menus.utils.*;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * Indicates a menu that is capable of being paginated, allowing different items to be shown on a different page.
 *
 * @param <T> The object that is being paged.
 */
public abstract class PaginatedMenu<T> extends StandardMenu {

    /**
     * The list of things to be paginated.
     */
    private final List<T> allItems;

    /**
     * Should the items be placed in the center of the menu? (Not in the borders.)
     */
    private final boolean center;
    /**
     * Stores all page buttons, this is modeled EXACTLY like normal menu buttons.
     *
     * @see Menu
     */
    private final List<Button> registeredPageButtons = new ArrayList<>();
    /**
     * The pages for this menu.
     *
     * @see PaginateMenu
     */
    private Map<Integer, List<T>> pages;
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
    public PaginatedMenu(final Menu parent, List<T> pages, boolean center) {
        super(parent);
        this.allItems = new ArrayList<>(pages);
        this.center = center;
    }

    /**
     * Base implementation.
     *
     * @param parent The parent of this menu.
     * @param pages  The List of items we want to page for this menu.
     */
    public PaginatedMenu(final Menu parent, List<T> pages) {
        super(parent);
        this.allItems = new ArrayList<>(pages);
        this.center = false;
    }

    /**
     * Base implementation.
     *
     * @param parent The parent of this menu.
     * @param pages  The List of items we want to page for this menu.
     * @param center Should we center the items in the middle of the inventory?
     */
    public PaginatedMenu(final Menu parent, @NotNull Collection<T> pages, boolean center) {
        super(parent);
        this.allItems = new ArrayList<>(pages);
        this.center = center;
    }

    /**
     * Base implementation.
     *
     * @param parent The parent of this menu.
     * @param pages  The List of items we want to page for this menu.
     */
    public PaginatedMenu(final Menu parent, @NotNull Collection<T> pages) {
        super(parent);
        this.allItems = new ArrayList<>(pages);
        this.center = false;
    }

    /**
     * Utility method to register multiple buttons, registers to an internal page button list.
     *
     * @param buttons The array of {@link Button buttons} to register.
     * @see Menu#registerButtons(Button...)
     */
    public final void registerPageButtons(final Button @NotNull ... buttons) {
        for (final Button button : buttons) {
            if (!registerPageButton(button)) {
                Utils.logWarning("Failed to register a page button: " + button);
            }
        }
    }

    /**
     * Register a Button.
     *
     * @param button The {@link Button} to register.
     * @return <code>true</code> if the button was successfully registered, <code>false</code> if it failed, or it was already registered.
     */
    private boolean registerPageButton(Button button) {
        // We return false if the EXACT button has already been registered.
        if (registeredPageButtons.contains(button)) {
            return false;
        }

        registeredPageButtons.add(button);

        // If a button is animated, we want to register a task.
        if (button instanceof final AnimatedButton animatedButton) {
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
     * @param stack The stack we should look for a button.
     * @return A {@link Button} should one exist.
     * @see Menu#getButton(ItemStack)
     */
    @Nullable
    public final Button getPageButton(final ItemStack stack) {
        if (stack == null) return null;

        return getButtonImpl(stack, registeredPageButtons);
    }

    /**
     * Updates the list and backend pages of for this menu.
     *
     * @param list The list that should be set as this Menu's.
     */
    public final void onlyUpdatePages(final Collection<T> list) {
        this.allItems.clear();
        this.allItems.addAll(list);

        // We force the player back to page one to avoid any issues determining the item we are clicking.
        // This also has the convenient side effect of never showing a null page if the data fails to
        // reach the previous page number.
        // This may not be required, but it does also show that the page has been updated to the viewer.
        this.page = 1;
        if (this.pages != null) {
            this.pages.clear();
            this.pages = PaginateMenu.page(getUsableFromSize(getSize()), this.allItems);
        }
    }

    /**
     * Refreshes the currently viewed page.
     */
    public final void refreshPage() {
        // We MUST clear the registered buttons first before registering new ones.
        // Failing to do so may cause double execution of a method which we don't want, especially if it references the
        // same data or the data is no longer present.
        this.registeredPageButtons.clear();

        clearPage();
        drawPage();
        updateTitleAndButtons();
        drawListOfButtons(registeredPageButtons);
    }

    /**
     * Updates the list of for this menu.
     *
     * @param list The list that should be set as this Menu's.
     */
    public final void updatePages(final Collection<T> list) {
        onlyUpdatePages(list);

        // We clear the registered normal buttons, so when we refresh, the buttons may be re-registered if required.
        clearButtons();
        refresh();
        drawListOfButtons(registeredPageButtons);
    }

    /**
     * Updates the list of for this menu, and attempts to update the viewable list.
     *
     * @param list The list that should be set as this Menu's.
     */
    public final void cleanUpdatePages(final Collection<T> list) {
        onlyUpdatePages(list);

        refreshPage();
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
                    forceSet(InventoryPosition.fromSlot(i), new ItemStack(Material.AIR));
                }
            }
        }
    }

    /**
     * Forcefully draws a page for page navigation.
     */
    private void forceDrawPage() {
        registeredPageButtons.clear();

        drawExtra();
        updateTitleAndButtons();

        drawPage();
        drawListOfButtons(registeredPageButtons);
    }

    /**
     * Draws a page.
     */
    private void drawPage() {
        // Define our usable so we can place them in the correct slots.
        final int usable = getUsableFromSize(getSize());

        // Use center positions.
        if (center) {
            for (int i = 0; i < usable; i++) {
                if (getValues().size() <= i) break;
                registerPageButtons(makeButton(i, InventoryPosition.MIDDLE_POSITIONS));
            }
        } else {
            // Use the set placeable positions.
            if (getPlaceablePositions() != null && !getPlaceablePositions().isEmpty()) {
                for (int i = 0; i < usable; i++) {
                    if (getValues().size() <= i) break;
                    registerPageButtons(makeButton(i, getPlaceablePositions()));
                }
            } else {
                // Use the usable positions.
                for (int i = 0; i < usable + 1; i++) {
                    if (getValues().size() <= i) break;
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
    private @NotNull Button makeButton(int iteration, final List<InventoryPosition> placeablePositions) {
        final T obj = getValues().get(iteration);
        return new Button() {
            @Override
            public ItemCreator createItem() {
                return convertToStack(obj);
            }

            @Override
            public void onClicked(final IMenuHolder user, final Menu menu, final ClickType click) {
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
    private @NotNull Button makeButtonCalculated(int iteration) {
        final T obj = getValues().get(iteration);
        return new Button() {
            @Override
            public ItemCreator createItem() {
                return convertToStack(obj);
            }

            @Override
            public void onClicked(final IMenuHolder user, final Menu menu, final ClickType click) {
                onClickPageItem(user, obj, click);
            }

            @Override
            public @NotNull InventoryPosition getPosition() {
                int row = iteration / 9;
                int column = iteration % 9;
                return InventoryPosition.of(row, column);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull MenuInventory formInventory() {
        // Do pages first, that way we can still access them in all methods.
        this.pages = PaginateMenu.page(getUsableFromSize(getSize()), allItems);

        final MenuInventory inv = super.formInventory();

        drawPage();
        updateTitleAndButtons();
        drawListOfButtons(registeredPageButtons);
        return inv;
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
                if (pages.size() > 1) {
                    registerPageButtons(getCounterButton());
                }
            } else {
                // We are not hiding navigation, register the counter.
                registerPageButtons(getCounterButton());
            }
        }

        // Are we hiding the navigation?
        if (hideNav) {
            // Check if we can go to the next or previous pages and ensure the button isn't null.
            // Then register the button.
            final boolean hasNext = page < pages.size();
            if (hasNext && getNextButton() != null) {
                registerPageButtons(getNextButton());
            }

            final boolean hasPrevious = page > 1;
            if (hasPrevious && getPreviousButton() != null) {
                registerPageButtons(getPreviousButton());
            }
        } else {
            // We aren't, register the buttons.
            registerPageButtons(getNextButton(), getPreviousButton());
        }
    }

    /**
     * Convert the object into an item.
     *
     * @param object The object.
     * @return An {@link ItemCreator} instance.
     */
    public abstract ItemCreator convertToStack(T object);

    /**
     * What should happen when we click on a page object.
     *
     * @param user   The holder of this menu.
     * @param object The object clicked.
     * @param click  The click type for this click.
     */
    public abstract void onClickPageItem(final IMenuHolder user, final T object, ClickType click);

    /**
     * {@inheritDoc}
     *
     * @param user     The holder of this menu.
     * @param position The position clicked.
     * @param click    The click type for this click.
     * @param clicked  What item was clicked?
     */
    @Override
    public void onClick(@NotNull IMenuHolder user, @NotNull InventoryPosition position, ClickType click, ItemStack clicked) {
        // Ignore outside clicks.
        if (user.getBase().getOpenInventory().getSlotType(position.getEffectiveSlot()) == InventoryType.SlotType.OUTSIDE)
            return;

        // We are using center positions.
        if (center) {
            // Ensure we clicked a valid location and that we have values.
            if (getValues().isEmpty() || !InventoryPosition.MIDDLE_POSITIONS.contains(position)) {
                return;
            }

            // Make sure we aren't over the value size.
            if (InventoryPosition.MIDDLE_POSITIONS.indexOf(position) >= getValues().size()) {
                return;
            }

            // Get the value and if it isn't null, go ahead and call the click.
            final T object = getValues().get(InventoryPosition.MIDDLE_POSITIONS.indexOf(position) - 1);
            if (object != null) {
                onClickPageItem(user, object, click);
            }
        } else {
            // Check if we have placeable positions.
            if (getPlaceablePositions() != null && !getPlaceablePositions().isEmpty()) {
                // Ensure we clicked a valid location and that we have values.
                if (getValues().isEmpty() || !getPlaceablePositions().contains(position)) {
                    return;
                }

                // Make sure we aren't over the value size.
                if (getPlaceablePositions().indexOf(position) >= getValues().size()) {
                    return;
                }

                // Get the value and if it isn't null, go ahead and call the click.
                final T object = getValues().get(getPlaceablePositions().indexOf(position));
                if (object != null) {
                    onClickPageItem(user, object, click);
                }
                return;
            }

            // Ensure we aren't over the value size.
            if (position.getEffectiveSlot() < getValues().size()) {
                // Get the value and if it isn't null, go ahead and call the click.
                final T object = getValues().get(position.getEffectiveSlot());
                if (object != null) {
                    onClickPageItem(user, object, click);
                }
            }
        }
    }

    /**
     * Check to see if there provided {@link InventoryPosition} is taken by a page button.
     * <br>
     * Modeled after {@link Menu#isSlotTakenByButton(InventoryPosition)}
     *
     * @param position The position to check.
     * @return <code>true</code> if the position is occupied by a registered button.
     * @see Menu#isSlotTakenByButton(InventoryPosition)
     */
    public boolean isSlotTakenByPageButton(InventoryPosition position) {
        // Loop registered page buttons and check if the position is taken.
        for (final Button button : this.registeredPageButtons) {
            // Check if we have permission for the button to appear.
            if (button.getPermission() != null && !viewer.getBase().hasPermission(button.getPermission()))
                continue;
            if (button.getPosition().equals(position)) return true;
        }
        return false;
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
        return pages.size();
    }

    /**
     * A {@link NavigationButton} used to show what page the viewer is on.
     *
     * @return Returns a new {@link NavigationButton}.
     * @see NavigationButton#builder()
     */
    @Nullable
    public NavigationButton getCounterButton() {
        return NavigationButton.builder()
                .material(Material.NAME_TAG)
                .name(getCounterString())
                .lore(pages.size() > 1 ? List.of("Click me to be sent back to the first page.", "Or right click to be sent to the last page!") : List.of())
                .runnable((user, menu, type) -> {
                    if (type == ClickType.RIGHT) {
                        this.page = Math.max(this.pages.size(), 1);
                        refreshPage();
                        return;
                    }

                    this.page = 1;
                    refreshPage();
                })
                .position(InventoryPosition.of(getInventory().getRows() - 1, 4))
                .build();
    }

    /**
     * A {@link NavigationButton} used to go to the next page.
     *
     * @return Returns a new {@link NavigationButton}.
     * @see NavigationButton#builder()
     */
    @Nullable
    public NavigationButton getNextButton() {
        return NavigationButton.builder()
                .material(Material.ARROW)
                .name("<yellow>Next >")
                .options(ItemOptions.HIDE_ALL_FLAGS.toBuilder().build())
                .runnable((user, menu, type) -> {
                    final boolean canGo = page < pages.size();
                    if (canGo) {
                        this.page = Utils.range(page + 1, 1, pages.size());
                        clearPage();
                        forceDrawPage();
                    } else {
                        menu.getViewer().tell("<red>You cannot go forward any further!");
                    }
                })
                .position(InventoryPosition.of(getInventory().getRows() - 1, 5))
                .build();
    }

    /**
     * A {@link NavigationButton} used to return to the previous page.
     *
     * @return Returns a new {@link NavigationButton}.
     * @see NavigationButton#builder()
     */
    @Nullable
    public NavigationButton getPreviousButton() {
        return NavigationButton.builder()
                .material(Material.ARROW)
                .name("<yellow>< Previous")
                .options(ItemOptions.HIDE_ALL_FLAGS.toBuilder().build())
                .runnable((user, menu, type) -> {
                    final boolean canGo = page > 1;
                    if (canGo) {
                        this.page = Utils.range(page - 1, 1, pages.size());
                        clearPage();
                        forceDrawPage();
                    } else {
                        menu.getViewer().tell("<red>You cannot go backwards any further!");
                    }
                })
                .position(InventoryPosition.of(getInventory().getRows() - 1, 3))
                .build();
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
    public final void reInit(final Collection<T> objects) {
        this.allItems.clear();
        this.allItems.addAll(objects);
        this.page = 1;
        refresh();
    }

    /**
     * Re-init a menu.
     *
     * @param objects The list of objects to re-populate the menu with.
     * @param delay   How long before re-init.
     */
    public final void reInit(Collection<T> objects, long delay) {
        Bukkit.getScheduler().runTaskLater(Utils.getInstance(), () -> reInit(objects), delay);
    }

    /**
     * Get the current pages values.
     *
     * @return The list of objects.
     */
    @NotNull
    @Unmodifiable
    public final List<T> getValues() {
        if (this.page == 0 || this.pages == null || this.pages.isEmpty()) return Collections.emptyList();
        org.apache.commons.lang3.Validate.isTrue(this.pages.containsKey(this.page - 1), "Menu " + this + " does not contain page #" + (this.page - 1));

        return Collections.unmodifiableList(this.pages.get(this.page - 1));
    }
}
