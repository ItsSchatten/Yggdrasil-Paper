package com.itsschatten.yggdrasil.menus.buttons;

import com.itsschatten.yggdrasil.IPermission;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.utils.IMenuHolder;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.menus.utils.ItemCreator;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a button for a {@link Menu}
 */
public abstract class Button {

    /**
     * Instance stack to make ItemStack checks reliable.
     */
    private ItemStack instanceStack;

    /**
     * Used to create the item that should be shown in the menu.
     *
     * @return The {@link ItemCreator} instance.
     * @see ItemCreator
     */
    public abstract ItemCreator createItem();

    /**
     * Gets the created item.
     *
     * @return An {@link ItemStack} generated from {@link #createItem()}
     */
    public final @Nullable ItemStack getItem() {
        if (createItem() == null) return null;
        if (instanceStack == null) {
            instanceStack = createItem().makeForMenu();
        }
        return instanceStack;
    }

    /**
     * What should when a player clicks this button.
     *
     * @param user The {@link IMenuHolder} that clicked this button.
     * @param menu The {@link Menu} that this button was clicked in.
     * @param type The {@link ClickType} that was used to click this button.
     */
    public abstract void onClicked(IMenuHolder user, Menu menu, ClickType type);

    /**
     * The position of this button.
     *
     * @return An {@link InventoryPosition} instance.
     * @see InventoryPosition
     */
    public abstract InventoryPosition getPosition();

    /**
     * Positions for this button.
     *
     * @return An array of {@link InventoryPosition} instances.
     * @see InventoryPosition
     */
    public InventoryPosition[] getPositions() {
        return null;
    }

    /**
     * The permission required to click this button.
     *
     * @return An {@link IPermission}.
     * @see IPermission
     */
    public IPermission getPermission() {
        return null;
    }

    @Override
    public String toString() {
        return "Button{item=" + getItem() + ", positions=" + (getPositions() == null ? (getPosition() == null ? "all null" : getPosition().toString()) : Arrays.toString(getPositions())) + "}";
    }
}
