package com.itsschatten.yggdrasil.menus.buttons;

import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.utils.IMenuHolder;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.menus.utils.MenuRunnable;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Represents a button for a {@link Menu}.
 */
public abstract class Button {

    /**
     * Instance stack to make {@link ItemStack} checks reliable.
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
        if (this.instanceStack == null) {
            this.instanceStack = createItem().make();
        }

        return instanceStack;
    }

    /**
     * What should happen when a player clicks this button.
     *
     * @param user  The {@link IMenuHolder} that clicked this button.
     * @param menu  The {@link Menu} that this button was clicked in.
     * @param click The {@link ClickType} that was used to click this button.
     */
    public abstract void onClicked(final IMenuHolder user, final Menu menu, final ClickType click);

    /**
     * The position of this button.
     *
     * @return An {@link InventoryPosition} instance.
     * @see InventoryPosition
     */
    @NotNull
    public abstract InventoryPosition getPosition();

    /**
     * Positions for this button.
     *
     * @return A collection of {@link InventoryPosition} instances.
     * @see InventoryPosition
     */
    @Nullable
    public Collection<InventoryPosition> getPositions() {
        return null;
    }

    /**
     * The permission required to click this button.
     *
     * @return A {@link String} to use as the permission.
     */
    @Nullable
    public String getPermission() {
        return null;
    }

    @Override
    public String toString() {
        return "Button{item=" + getItem() + ", positions=" + (getPositions() == null ? getPosition().toString() : getPositions()) + "}";
    }
}
