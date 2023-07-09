package com.itsschatten.yggdrasil.libs.menus.buttons;

import com.itsschatten.yggdrasil.libs.menus.Menu;
import com.itsschatten.yggdrasil.libs.menus.utils.ItemCreator;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@link Button} that can be animated.
 */
public abstract class AnimatedButton extends Button {

    @Setter
    @Getter
    private ItemStack inner = getItem();

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
    public ItemStack animation() {
        return animate().makeForMenu();
    }

    /**
     * What will be run for the animation task.
     *
     * @param menu The menu to run for.
     */
    public final void run(final @NotNull Menu menu) {
        if (menu.getInventory() == null || menu.getBukkitInventory() == null) return;
        // Utils.debugLog("Running animation for button: " + this);
        setInner(animation());
        menu.forceSet(getPosition(), getInner());
    }

    /**
     * How long should we wait before we run this animation?
     *
     * @return The time in Minecraft ticks.
     */
    public abstract long getUpdateTime();
}
