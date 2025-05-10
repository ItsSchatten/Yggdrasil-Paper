package com.itsschatten.yggdrasil.menus.buttons;

import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.menus.utils.MenuHolder;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * A button that doesn't do anything.
 */
public abstract class SimpleButton<T extends MenuHolder> extends Button<T> {

    /**
     * Makes a new SimpleButton from an already made {@link Button}.
     *
     * @param button The button to use.
     * @return Returns a new SimpleButton instance using the provided button to make the item stack and position.
     */
    @Contract(value = "_ -> new", pure = true)
    public static <T extends MenuHolder> @NotNull SimpleButton<T> makeFrom(final Button<T> button) {
        return new SimpleButton<>() {
            @Override
            public ItemCreator createItem() {
                return button.createItem();
            }

            @Override
            public @NotNull InventoryPosition getPosition() {
                return button.getPosition();
            }

            @Override
            public Collection<InventoryPosition> getPositions() {
                return button.getPositions();
            }
        };
    }

    /**
     * Makes a new SimpleButton from an already made {@link Button} and replaces the item.
     *
     * @param button  The button to use.
     * @param creator The {@link ItemCreator} to use it for the button.
     * @return Returns a new SimpleButton instance
     * using the provided button to provide the position and the creator to update the ItemStack.
     */
    @Contract(value = "_,_ -> new", pure = true)
    public static <T extends MenuHolder> @NotNull SimpleButton<T> makeFrom(final Button<T> button, final ItemCreator creator) {
        return new SimpleButton<>() {
            @Override
            public ItemCreator createItem() {
                return creator;
            }

            @Override
            public @NotNull InventoryPosition getPosition() {
                return button.getPosition();
            }

            @Override
            public Collection<InventoryPosition> getPositions() {
                return button.getPositions();
            }
        };
    }

    /**
     * {@inheritDoc}
     *
     * @param user  The {@link T} that clicked this button.
     * @param menu  The {@link Menu} that this button was clicked in.
     * @param click The {@link ClickType} that was used to click this button.
     */
    @Override
    public final void onClicked(final T user, final Menu<T> menu, final ClickType click) {
    }
}
