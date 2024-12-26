package com.itsschatten.yggdrasil.menus.buttons;

import com.itsschatten.yggdrasil.items.ItemCreator;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.interfaces.AlternativeDisplayItem;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@link Button} that can be animated.
 */
@Setter
public abstract class AnimatedButton extends Button implements AlternativeDisplayItem {

    // Item stack used in-order to properly run click logic.
    // Button#getItemStack normally fails because it cannot be updated without a new button created.
    /**
     * Item stack used in-order to properly run click logic.
     * --- SETTER ---
     * Sets the {@link ItemStack} to be used in animations and validity checks.
     *
     * @param innerStack The {@link ItemStack} to set as the inner stack.
     */
    private ItemStack innerStack;

    /**
     * Gets the inner {@link ItemStack}.
     *
     * @return Returns the inner {@link ItemStack} used in animations and to ensure that the click checks are valid.
     */
    public ItemStack getInnerStack() {
        if (innerStack == null) {
            setInnerStack(getItem());
        }

        return innerStack;
    }

    /**
     * What the item should turn into when we animate this button.
     *
     * @return The {@link ItemCreator} instance that should be used to animate.
     * @see ItemCreator
     */
    public ItemCreator animate() {
        return createItem();
    }

    /**
     * Utility to make the new button.
     *
     * @return A new {@link ItemStack} generated from {@link ItemCreator}
     * @see ItemCreator
     */
    public final @NotNull ItemStack animation() {
        return animate().make();
    }

    /**
     * {@inheritDoc}
     *
     * @return Returns {@link #animation()}
     */
    public final @NotNull ItemStack displayItem() {
        return animation();
    }

    /**
     * What will be run for the animation task.
     *
     * @param menu The menu to run for.
     */
    public final void run(final @NotNull Menu menu) {
        if (menu.getInventory() == null || menu.getBukkitInventory() == null) return;
        setInnerStack(animation());

        if (getPositions() != null && !getPositions().isEmpty()) {
            for (final InventoryPosition position : getPositions()) {
                menu.forceSet(position, getInnerStack());
            }
        } else {
            menu.forceSet(getPosition(), getInnerStack());
        }
    }

    /**
     * How long should we wait before we run this animation?
     *
     * @return The time in Minecraft ticks.
     */
    public long getUpdateTime() {
        return 20L;
    }
}
