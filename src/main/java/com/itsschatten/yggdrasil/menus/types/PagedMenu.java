package com.itsschatten.yggdrasil.menus.types;

import com.itsschatten.yggdrasil.Utils;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.AnimatedButton;
import com.itsschatten.yggdrasil.menus.buttons.Button;
import com.itsschatten.yggdrasil.menus.buttons.SimpleAnimatedButton;
import com.itsschatten.yggdrasil.menus.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Indicates a menu that is capable of being paginated, allowing different items to be shown on a different page.
 *
 * @param <T> The object that is being paged.
 */
public abstract class PagedMenu<T> extends StandardMenu {

    /**
     * The list of things to be paginated.
     */
    private final List<T> fullList;

    /**
     * Should the items be placed in the center of the menu? (Not in the borders.)
     */
    private final boolean center;

    /**
     * The pages for this menu.
     *
     * @see PaginateMenu
     */
    private Map<Integer, List<T>> pages;

    /**
     * Stores all page buttons, this is modeled EXACTLY like normal menu buttons.
     *
     * @see Menu
     */
    private final List<Button> registeredPageButtons = new ArrayList<>();

    /**
     * Should the page number be appended to the title of the menu.
     */
    @Setter
    @Getter
    private boolean addPageNumbersToTitle;

    /**
     * Should we add the page counter item?
     */
    @Setter
    @Getter
    private boolean addPageCounterItem = true;

    /**
     * If true, it will remove any navigational buttons from view if the viewer cannot go to the page.
     */
    @Setter
    @Getter
    private boolean removeNavIfCantGo;

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
    public PagedMenu(Menu parent, List<T> pages, boolean center) {
        super(parent);
        this.fullList = new ArrayList<>(pages);
        this.center = center;
    }

    /**
     * Base implementation.
     *
     * @param parent The parent of this menu.
     * @param pages  The List of items we want to page for this menu.
     */
    public PagedMenu(Menu parent, List<T> pages) {
        super(parent);
        this.fullList = new ArrayList<>(pages);
        this.center = false;
    }

    /**
     * Base implementation.
     *
     * @param parent The parent of this menu.
     * @param pages  The List of items we want to page for this menu.
     * @param center Should we center the items in the middle of the inventory?
     */
    public PagedMenu(Menu parent, @NotNull Collection<T> pages, boolean center) {
        super(parent);
        this.fullList = new ArrayList<>(pages);
        this.center = center;
    }

    /**
     * Base implementation.
     *
     * @param parent The parent of this menu.
     * @param pages  The List of items we want to page for this menu.
     */
    public PagedMenu(Menu parent, @NotNull Collection<T> pages) {
        super(parent);
        this.fullList = new ArrayList<>(pages);
        this.center = false;
    }

    /**
     * Utility method to register multiple buttons.
     *
     * @param buttons The array of {@link Button buttons} to register.
     * @see Menu#registerButtons(Button...)
     */
    public final void registerPageButtons(final Button @NotNull ... buttons) {
        for (final Button button : buttons) {
            if (!registerPageButton(button)) {
                Utils.debugLog("Failed to register a page button: " + button);
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
        if (registeredPageButtons.contains(button)) {
            return false;
        }

        registeredPageButtons.add(button);
        if (button instanceof final AnimatedButton animatedButton) {
            final ReschedulableTask task = new ReschedulableTask(animatedButton.getUpdateTime(), ReschedulableTask.Type.BUTTON) {
                @Override
                public void run() {
                    animatedButton.run(PagedMenu.this);
                }
            };

            registerTask(task);
        }

        if (button instanceof final SimpleAnimatedButton animatedButton) {
            final ReschedulableTask task = new ReschedulableTask(animatedButton.getUpdateTime(), ReschedulableTask.Type.BUTTON) {
                @Override
                public void run() {
                    animatedButton.run(PagedMenu.this);
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
    public final Button getPageButton(final ItemStack stack) {
        if (stack == null) return null;

        return getButtonImpl(stack, registeredPageButtons);
    }

    /**
     * Updates the list of for this menu.
     *
     * @param list The list that should be set as this Menu's.
     */
    public final void onlyUpdatePages(final Collection<T> list) {
        this.page = 1;
        if (pages != null) {
            this.pages.clear();
        }
        this.fullList.clear();
        fullList.addAll(list);
    }

    /**
     * Refreshes the page currently viewed page.
     */
    public final void refreshPage() {
        this.registeredPageButtons.clear();

        clearPage();
        drawPage();
        updateTitleAndButtons();
        drawListOfButtons(registeredPageButtons);
    }

    /**
     * Updates the list of for this menu, and attempts to update the viewable list.
     *
     * @param list The list that should be set as this Menu's.
     */
    public final void cleanUpdatePages(final Collection<T> list) {
        onlyUpdatePages(list);
        int usable = getUsableFromSize(getSize());
        this.pages = PaginateMenu.page(usable, fullList);

        refreshPage();
    }

    /**
     * Attempts to clear the page spots.
     */
    private void clearPage() {
        final int usable = getUsableFromSize(getSize());
        if (center) {
            for (int i = 0; i < usable; i++) {
                forceSet(InventoryPosition.middlePositions.get(i), new ItemStack(Material.AIR));
            }
        } else {
            if (getPlaceablePositions() != null && !getPlaceablePositions().isEmpty()) {
                for (int i = 0; i < usable; i++) {
                    forceSet(getPlaceablePositions().get(i), new ItemStack(Material.AIR));
                }
            } else {
                for (int i = 0; i < usable + 1; i++) {
                    int row = i / 9;
                    int column = i % 9;
                    forceSet(InventoryPosition.of(row, column), new ItemStack(Material.AIR));
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
        final int usable = getUsableFromSize(getSize());
        if (center) {
            for (int i = 0; i < usable; i++) {
                int finalI = i;
                if (getValues().size() <= i) break;
                final T obj = getValues().get(finalI);
                final Button button = new Button() {
                    @Override
                    public ItemCreator createItem() {
                        return convertToStack(obj);
                    }

                    @Override
                    public void onClicked(IMenuHolder user, Menu menu, ClickType type) {
                        onClickPageItem(user, obj, type);
                    }

                    @Override
                    public InventoryPosition getPosition() {
                        return InventoryPosition.middlePositions.get(finalI);
                    }
                };
                registerPageButtons(button);
            }
        } else {
            if (getPlaceablePositions() != null && !getPlaceablePositions().isEmpty()) {
                for (int i = 0; i < usable; i++) {
                    int finalI = i;
                    if (getValues().size() <= i) break;
                    final T obj = getValues().get(finalI);
                    final Button button = new Button() {
                        @Override
                        public ItemCreator createItem() {
                            return convertToStack(obj);
                        }

                        @Override
                        public void onClicked(IMenuHolder user, Menu menu, ClickType type) {
                            onClickPageItem(user, obj, type);
                        }

                        @Override
                        public @NotNull InventoryPosition getPosition() {
                            return getPlaceablePositions().get(finalI);
                        }
                    };
                    registerPageButtons(button);
                }
            } else {
                for (int i = 0; i < usable + 1; i++) {
                    int finalI = i;
                    if (getValues().size() <= i) break;
                    final T obj = getValues().get(finalI);
                    final Button button = new Button() {
                        @Override
                        public ItemCreator createItem() {
                            return convertToStack(obj);
                        }

                        @Override
                        public void onClicked(IMenuHolder user, Menu menu, ClickType type) {
                            onClickPageItem(user, obj, type);
                        }

                        @Override
                        public @NotNull InventoryPosition getPosition() {
                            int row = finalI / 9;
                            int column = finalI % 9;
                            return InventoryPosition.of(row, column);
                        }
                    };
                    registerPageButtons(button);
                }
            }
        }
    }

    /**
     * Updates the list of for this menu.
     *
     * @param list The list that should be set as this Menu's.
     */
    public final void updatePages(final Collection<T> list) {
        onlyUpdatePages(list);
        clearButtons();
        refresh();
        drawListOfButtons(registeredPageButtons);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MenuInventory formInventory() {
        Objects.requireNonNull(getTitle(), "Title is not set in " + this + "!");

        final MenuInventory inv;
        if (rows != null && rows > 0) {
            setSize(rows * 9);
            inv = MenuInventory.ofRows(rows, this, getTitle());
        } else {
            Validate.isTrue((getSize() % 9) == 0, "Size must be a multiple of 9.");
            inv = MenuInventory.of(getSize(), this, getTitle());
        }
        inv.setViewer(getViewer());
        inv.setMenu(this);
        setInventory(inv);

        this.pages = PaginateMenu.page(getUsableFromSize(getSize()), fullList);
        drawPage();

        registerPreMadeButtons();
        drawExtra();
        updateTitleAndButtons();
        drawButtons();
        drawListOfButtons(registeredPageButtons);
        return inv;
    }

    /**
     * Sets the titles and buttons for this menu.
     */
    private void updateTitleAndButtons() {
        if (addPageNumbersToTitle) {
            final boolean hasPages = pages.size() > 1;
            final String title = this.getInventory().getTitle() + (hasPages ? " " + getCurrentPageString() + getPageSeparator() + getTotalPages() : "");
            updateTitle(title);
        }

        if (addPageCounterItem) {
            final Button counterButton = new Button() {
                @Override
                public ItemCreator createItem() {
                    return createPageCounter();
                }

                @Override
                public void onClicked(IMenuHolder user, Menu menu, ClickType type) {
                    onPageCounterClick(menu, type);
                }

                @Override
                public InventoryPosition getPosition() {
                    return getCounterPosition();
                }
            };

            registerPageButtons(counterButton);
        }

        final Button nextButton = new Button() {
            @Override
            public ItemCreator createItem() {
                return createNextButton();
            }

            @Override
            public void onClicked(IMenuHolder user, Menu menu, ClickType type) {
                onNextClick(menu, type);
            }

            @Override
            public InventoryPosition getPosition() {
                return getNextPosition();
            }
        };

        final Button backButton = new Button() {
            @Override
            public ItemCreator createItem() {
                return createBackButton();
            }

            @Override
            public void onClicked(IMenuHolder user, Menu menu, ClickType type) {
                onBackClick(menu, type);
            }

            @Override
            public InventoryPosition getPosition() {
                return getBackPosition();
            }
        };

        if (removeNavIfCantGo) {
            final boolean canGoForward = page < pages.size();
            if (canGoForward) {
                registerPageButtons(nextButton);
            }

            final boolean canGoBack = page > 1;
            if (canGoBack) {
                registerPageButtons(backButton);
            }
        } else {
            registerPageButtons(nextButton, backButton);
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
        if (user.getBase().getOpenInventory().getSlotType(position.getEffectiveSlot()) == InventoryType.SlotType.OUTSIDE)
            return;

        if (center) {
            if (getValues().isEmpty() || !InventoryPosition.middlePositions.contains(position)) {
                return;
            }

            if (InventoryPosition.middlePositions.indexOf(position) >= getValues().size()) {
                return;
            }

            final T object = getValues().get(InventoryPosition.middlePositions.indexOf(position) - 1);
            if (object != null) {
                onClickPageItem(user, object, click);
            }
        } else {
            if (getPlaceablePositions() != null && getPlaceablePositions().size() > 0) {
                if (getValues().isEmpty() || !getPlaceablePositions().contains(position)) {
                    return;
                }

                if (getPlaceablePositions().indexOf(position) >= getValues().size()) {
                    return;
                }

                final T object = getValues().get(getPlaceablePositions().indexOf(position));
                if (object != null) {
                    onClickPageItem(user, object, click);
                    return;
                }
                return;
            }

            if (position.getEffectiveSlot() < getValues().size()) {
                final T object = getValues().get(position.getEffectiveSlot());
                if (object != null) {
                    onClickPageItem(user, object, click);
                }
            }
        }
    }

    /**
     * Check to see if there provided {@link InventoryPosition} is taken by a page button.
     * <p>
     * Modeled after {@link Menu#isSlotTakenByButton(InventoryPosition)}
     * </p>
     *
     * @param position The position to check.
     * @return <code>true</code> if the position is occupied by a registered button.
     * @see Menu#isSlotTakenByButton(InventoryPosition)
     */
    public boolean isSlotTakenByPageButton(InventoryPosition position) {
        for (final Button button : registeredPageButtons) {
            if (button.getPosition() == null || (button.getPermission() != null && !viewer.getBase().hasPermission(button.getPermission().getPermission())))
                continue;
            if (button.getPosition().equals(position)) return true;
        }
        return false;
    }

    /**
     * @return The current page string.
     */
    public String getCurrentPageString() {
        return "<dark_aqua>" + page + "</dark_aqua>";
    }

    /**
     * @return The current page separator.
     */
    public String getPageSeparator() {
        return "<dark_gray>/</dark_gray>";
    }

    /**
     * @return The total page string.
     */
    public String getTotalPagesString() {
        return "<gray>" + pages.size() + "</gray>";
    }

    /**
     * @return The current page.
     */
    public final int getCurrentPage() {
        return getPage();
    }

    /**
     * @return The number of pages.
     */
    public final int getTotalPages() {
        return pages.size();
    }

    /**
     * What happens when you click on the page counter.
     *
     * @param menu The menu that the click occurred.
     * @param type The click type.
     */
    public void onPageCounterClick(final Menu menu, final ClickType type) {
        if (type == ClickType.RIGHT) {
            this.page = Math.max(this.pages.size(), 1);
            clearPage();
            forceDrawPage();
            return;
        }

        this.page = 1;
        clearPage();
        forceDrawPage();
    }

    /**
     * What happens when you click on the next page button.
     *
     * @param menu The menu that the click occurred.
     * @param type The click type.
     */
    public void onNextClick(final Menu menu, final ClickType type) {
        final boolean canGo = page < pages.size();
        if (canGo) {
            this.page = Utils.range(page + 1, 1, pages.size());
            clearPage();
            forceDrawPage();
        } else {
            menu.getViewer().tell("<red>You cannot go forward any further!");
            animateTitle("<red>You can't go any further!", 40L);
        }
    }

    /**
     * What happens when you click on the back page button.
     *
     * @param menu The menu that the click occurred.
     * @param type The click type.
     */
    public void onBackClick(final Menu menu, final ClickType type) {
        final boolean canGo = page > 1;
        if (canGo) {
            this.page = Utils.range(page - 1, 1, pages.size());
            clearPage();
            forceDrawPage();
        } else {
            menu.getViewer().tell("<red>You cannot go backwards any further!");
            animateTitle("<red>You can't go back!", 40L);
        }
    }

    /**
     * @return The page counter position.
     */
    public InventoryPosition getCounterPosition() {
        return InventoryPosition.of(getInventory().getRows() - 1, 4);
    }

    /**
     * @return The next page button position.
     */
    public InventoryPosition getNextPosition() {
        return InventoryPosition.of(getInventory().getRows() - 1, 5);
    }

    /**
     * @return The previous page button position.
     */
    public InventoryPosition getBackPosition() {
        return InventoryPosition.of(getInventory().getRows() - 1, 3);
    }

    /**
     * The page counter item.
     *
     * @return An {@link ItemCreator} instance.
     */
    public ItemCreator createPageCounter() {
        final boolean hasPages = pages.size() > 1;
        return ItemCreator.of(Material.NAME_TAG).name((getCurrentPageString() + getPageSeparator() + getTotalPagesString())).lore(!hasPages ? List.of() : List.of("", "Click me to be sent back to the first page.", "Or right click to be sent to the last page!")).build();
    }

    /**
     * The next page item.
     *
     * @return An {@link ItemCreator} instance.
     */
    public ItemCreator createNextButton() {
        final ItemCreator.ItemCreatorBuilder creator = ItemCreator.of(Material.ARROW).name(getNextButtonName());
        if (getNextButtonLore() != null) {
            creator.lore(getNextButtonLore());
        }
        return creator.build();
    }

    /**
     * The back page item.
     *
     * @return An {@link ItemCreator} instance.
     */
    public ItemCreator createBackButton() {
        final ItemCreator.ItemCreatorBuilder creator = ItemCreator.of(Material.ARROW).name(getBackButtonName());
        if (getBackButtonLore() != null) {
            creator.lore(getBackButtonLore());
        }
        return creator.build();
    }

    /**
     * Name of the next button.
     *
     * @return A string for the next button's name.
     */
    public String getNextButtonName() {
        return "<yellow>Next Page >";
    }

    /**
     * Lore of the next button.
     *
     * @return A List of string for the lore, <code>null</code> default.
     */
    public List<String> getNextButtonLore() {
        return null;
    }


    /**
     * Name of the back button.
     *
     * @return A string for the back button's name.
     */
    public String getBackButtonName() {
        return "<yellow>< Go Back";
    }

    /**
     * Lore of the back button.
     *
     * @return A List of string for the lore, <code>null</code> default.
     */
    public List<String> getBackButtonLore() {
        return null;
    }

    /**
     * Gets the usable number of slots from the size.
     *
     * @param size The size of the inventory.
     * @return The proper size.
     */
    private int getUsableFromSize(final int size) {
        if (center) {
            if (size < 27)
                throw new UnsupportedOperationException("Size must be 27 or higher to center items in the menu.");
            return size == 54 ? 28 : (size == 45 ? 21 : (size == 36 ? 14 : 7));
        }

        if (getPlaceablePositions() != null && getPlaceablePositions().size() > 0) {
            return getPlaceablePositions().size();
        }

        return size == 54 ? 45 : (size == 45 ? 36 : (size == 36 ? 27 : (size == 27 ? 18 : 9)));
    }


    /**
     * The list of placeable positions.
     *
     * @return A list of {@link InventoryPosition}s.
     * @see InventoryPosition
     */
    public List<InventoryPosition> getPlaceablePositions() {
        return null;
    }

    /**
     * Re-init a menu.
     *
     * @param objects The list of objects to re-populate the menu with.
     */
    public final void reInit(Collection<T> objects) {
        this.fullList.clear();
        this.fullList.addAll(objects);
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
     * @return The list of objects
     */
    public final List<T> getValues() {
        if (page == 0 || pages.isEmpty()) return Collections.emptyList();
        org.apache.commons.lang.Validate.isTrue(pages.containsKey(page - 1), "Menu " + this + " does not contain page #" + (page - 1));

        return pages.get(page - 1);
    }
}
