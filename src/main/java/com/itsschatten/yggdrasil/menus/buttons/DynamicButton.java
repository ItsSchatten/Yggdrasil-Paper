package com.itsschatten.yggdrasil.menus.buttons;

import com.itsschatten.yggdrasil.TimeUtils;
import com.itsschatten.yggdrasil.Utils;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.utils.IMenuHolder;
import com.itsschatten.yggdrasil.menus.utils.ItemCreator;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * A button that can be automatically updated after being clicked and retain all of its functionality.
 */
public abstract class DynamicButton extends Button {

    /**
     * The {@link ItemStack} that belongs to this button, this is used in click checks to ensure reliability.
     */
    @Getter
    @Setter
    private ItemStack innerStack = getItem();

    /**
     * What happens when a user clicks this button.
     *
     * @param user The {@link IMenuHolder} that clicked this button.
     * @param menu The {@link Menu} that the click occurred.
     * @param type The {@link ClickType} for this click.
     */
    public abstract void whenClicked(final IMenuHolder user, Menu menu, ClickType type);

    /**
     * The {@link ItemCreator} that should be used to update the item.
     *
     * @return An {@link ItemCreator} instance.
     * @see ItemCreator
     */
    public abstract ItemCreator updateStack();

    /**
     * The delay we should wait before attempting to update the button.
     * <p>
     * This uses Minecraft ticks.
     *
     * @return Default <code>5L</code>.
     * @see TimeUtils.MinecraftTimeUnits
     */
    public long getDelay() {
        return 5L;
    }

    /**
     * {@inheritDoc}
     *
     * @param user The {@link IMenuHolder} that clicked this button.
     * @param menu The {@link Menu} that this button was clicked in.
     * @param type The {@link ClickType} that was used to click this button.
     */
    @Override
    public final void onClicked(IMenuHolder user, Menu menu, ClickType type) {
        whenClicked(user, menu, type);

        if (user.getCurrentMenu() == menu) {
            if (updateStack() != null) {
                setInnerStack(updateStack().makeForMenu());
            }
            Bukkit.getScheduler().runTaskLater(Utils.getInstance(), () -> menu.forceSet(getPosition(), getInnerStack()), getDelay());
        }
    }
}
