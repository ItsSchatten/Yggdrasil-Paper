package com.itsschatten.yggdrasil.menus.buttons;

import com.itsschatten.yggdrasil.TimeUtils;
import com.itsschatten.yggdrasil.Utils;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.buttons.interfaces.AlternativeDisplayItem;
import com.itsschatten.yggdrasil.menus.utils.IMenuHolder;
import com.itsschatten.yggdrasil.menus.utils.InventoryPosition;
import com.itsschatten.yggdrasil.items.ItemCreator;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A button that can be automatically updated after being clicked and retain all of its functionality.
 */
@Setter
public abstract class DynamicButton extends Button implements AlternativeDisplayItem {

    /**
     * The {@link ItemStack} that belongs to this button, this is used in click checks to ensure reliability.
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
     * The {@link ItemCreator} that should be used to update the item.
     *
     * @return An {@link ItemCreator} instance.
     * @see ItemCreator
     */
    public ItemCreator updateStack() {
        return createItem();
    }

    /**
     * {@inheritDoc}
     *
     * @return Returns {@link #getInnerStack()}
     */
    public final ItemStack displayItem() {
        return innerStack;
    }

    /**
     * The delay we should wait before attempting to update the button.
     * <p>
     * This uses Minecraft ticks.
     *
     * @return Default <code>5L</code>.
     * @see TimeUtils.MinecraftTimeUnits
     */
    public long getUpdateTime() {
        return 0L;
    }

    /**
     * Updates the inner {@link ItemStack}.
     *
     * @param holder The holder of the menu.
     * @param menu   The menu that we should update.
     */
    public final void updateInner(final @NotNull IMenuHolder holder, final Menu menu) {
        if (holder.getCurrentMenu() == menu) {
            if (updateStack() != null) {
                setInnerStack(updateStack().make());
            }

            Bukkit.getScheduler().runTaskLater(Utils.getInstance(), () -> {
                if (getPositions() != null && !getPositions().isEmpty()) {
                    for (final InventoryPosition position : getPositions()) {
                        menu.forceSet(position, getInnerStack());
                    }
                } else {
                    menu.forceSet(getPosition(), getInnerStack());
                }
            }, getUpdateTime());
        }
    }
}
