package com.itsschatten.yggdrasil.menus.types;

import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.Button;
import com.itsschatten.yggdrasil.menus.utils.IMenuHolder;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.menus.utils.ItemCreator;
import com.itsschatten.yggdrasil.menus.utils.MenuInventory;
import org.apache.commons.lang3.Validate;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * A List menu, used to display a list of objects in a Menu.
 *
 * @param <T> The object.
 */
public abstract class ListMenu<T> extends StandardMenu {

    /**
     * The list of things to be paginated.
     */
    private final List<T> list;

    /**
     * Standard implementation.
     *
     * @param parent The parent (or previous) menu.
     * @param list   The list that we should display.
     */
    protected ListMenu(final Menu parent, List<T> list) {
        super(parent);
        this.list = new ArrayList<>();
        this.list.addAll(list);
    }

    /**
     * Standard implementation.
     *
     * @param parent     The parent (or previous) menu.
     * @param collection The collection that we should display.
     */
    protected ListMenu(final Menu parent, Collection<T> collection) {
        super(parent);
        this.list = new ArrayList<>();
        list.addAll(collection);
    }

    /**
     * Standard implementation.
     *
     * @param parent   The parent (or previous) menu.
     * @param iterable The iterable that we should display.
     */
    protected ListMenu(final Menu parent, @NotNull Iterable<T> iterable) {
        super(parent);
        this.list = new ArrayList<>();
        iterable.forEach(list::add);
    }

    /**
     * Creates a "used" item, or an item that does have a value in the list.
     *
     * @param object The object that should use to create this {@link ItemCreator}
     * @return An instance of {@link ItemCreator}
     * @see ItemCreator
     */
    protected abstract ItemCreator createFilledItem(final T object);

    /**
     * Creates an "unused" item, or an item that does NOT have a value in the list.
     *
     * @return An instance of {@link ItemCreator}
     * @see ItemCreator
     */
    protected abstract ItemCreator createEmptyItem();

    /**
     * What happens when a user clicks on an item that represents a list item.
     *
     * @param user   The {@link IMenuHolder} that clicked.
     * @param object The list item that was clicked.
     * @param click  The {@link ClickType} that was used.
     */
    protected abstract void onUsedClick(final IMenuHolder user, final T object, final ClickType click);

    /**
     * What happens when a user clicks on an item that doesn't represent a list item.
     *
     * @param user  The {@link IMenuHolder} that clicked.
     * @param click The {@link ClickType} that was used.
     */
    protected abstract void onUnusedClick(final IMenuHolder user, final ClickType click);

    /**
     * An array of {@link InventoryPosition positions} that can be used to represent list items.
     *
     * @return An array of {@link InventoryPosition}
     * @see InventoryPosition
     */
    protected List<InventoryPosition> getPlaceablePositions() {
        return new ArrayList<>();
    }

    /**
     * Update this menu's list object to contain the provided list's values.
     *
     * @param list The list we wish to update this menu's list to.
     */
    public final void updateList(final Collection<T> list) {
        this.list.clear();
        this.list.addAll(list);
        updateViewableList();
        drawButtons();
    }

    /**
     * Force updates the menu to show the new list of objects.
     */
    public final void updateViewableList() {
        resetButtons();
        registerPreMadeButtons(); // Must be called here as the Menu doesn't have this method.

        if (!getPlaceablePositions().isEmpty()) {
            for (int i = 0; i < getPlaceablePositions().size(); i++) {
                // Used because of inner classes.
                final int finalI = i;

                // checks if the value size is less than the current place in the placeable cells,
                // if it is attempts to place an unused slot.
                if (getValues().size() - 1 < i) {
                    final Button button = new Button() {
                        @Override
                        public ItemCreator createItem() {
                            return createEmptyItem();
                        }

                        @Override
                        public void onClicked(IMenuHolder user, Menu menu, ClickType type) {
                            onUnusedClick(user, type);
                        }

                        @Override
                        public @NotNull InventoryPosition getPosition() {
                            return getPlaceablePositions().get(finalI);
                        }
                    };
                    registerButtons(button);
                    set(button.getPosition(), button);
                    continue; // Continue here, so we can get a proper unused slot.
                }

                final T obj = getValues().get(finalI);
                final Button button = new Button() {
                    @Override
                    public ItemCreator createItem() {
                        return createFilledItem(obj);
                    }

                    @Override
                    public void onClicked(IMenuHolder user, Menu menu, ClickType type) {
                        onUsedClick(user, obj, type);
                    }

                    @Override
                    public @NotNull InventoryPosition getPosition() {
                        return getPlaceablePositions().get(finalI);
                    }
                };

                registerButtons(button);
                set(button.getPosition(), button);
            }
        }
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

        if (getPlaceablePositions().size() > 0) {
            for (int i = 0; i < getPlaceablePositions().size(); i++) {
                // Used because of inner classes.
                int finalI = i;

                // checks if the value size is less than the current place in the placeable cells,
                // if it is attempts to place an unused slot.
                if (getValues().size() - 1 < i) {
                    final Button button = new Button() {
                        @Override
                        public ItemCreator createItem() {
                            return createEmptyItem();
                        }

                        @Override
                        public void onClicked(IMenuHolder user, Menu menu, ClickType type) {
                            onUnusedClick(user, type);
                        }

                        @Override
                        public @NotNull InventoryPosition getPosition() {
                            return getPlaceablePositions().get(finalI);
                        }
                    };
                    registerButtons(button);
                    continue; // Continue here, so we can get a proper unused slot.
                }

                final T obj = getValues().get(finalI);
                final Button button = new Button() {
                    @Override
                    public ItemCreator createItem() {
                        return createFilledItem(obj);
                    }

                    @Override
                    public void onClicked(IMenuHolder user, Menu menu, ClickType type) {
                        onUsedClick(user, obj, type);
                    }

                    @Override
                    public @NotNull InventoryPosition getPosition() {
                        return getPlaceablePositions().get(finalI);
                    }
                };

                registerButtons(button);
            }
        }

        registerPreMadeButtons();
        drawExtra();
        drawButtons();
        return inv;
    }

    /**
     * {@inheritDoc}
     *
     * @param user     The holder of this menu.
     * @param position The slot clicked.
     * @param click    The click type for this click.
     * @param clicked  What item was clicked?
     */
    @Override
    public void onClick(@NotNull IMenuHolder user, @NotNull InventoryPosition position, ClickType click, ItemStack clicked) {
        if (user.getBase().getOpenInventory().getSlotType(position.getEffectiveSlot()) == InventoryType.SlotType.OUTSIDE)
            return;

        for (int i = 0; i < getPlaceablePositions().size(); i++) {
            if (getValues().size() - 1 < i) {
                if (position.equals(getPlaceablePositions().get(i))) {
                    onUnusedClick(user, click);
                }
                continue;
            }
            final T object = getValues().get(0);
            if (position.equals(getPlaceablePositions().get(i))) {
                if (object != null) {
                    onUsedClick(user, object, click);
                }
            }
        }
    }

    /**
     * Utility method to quickly get the values of the list.
     *
     * @return Return's the list of objects for this menu.
     */
    public final List<T> getValues() {
        return list;
    }
}
