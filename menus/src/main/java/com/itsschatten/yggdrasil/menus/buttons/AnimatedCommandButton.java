package com.itsschatten.yggdrasil.menus.buttons;

import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.types.StandardMenu;
import com.itsschatten.yggdrasil.menus.utils.IMenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

/**
 * Animated version of {@link CommandButton}
 *
 * @see AnimatedButton
 * @see CommandButton
 */
public abstract class AnimatedCommandButton extends AnimatedButton {

    /**
     * The command to be executed.
     *
     * @return The command to be executed.
     */
    @NotNull
    public abstract String getCommand();

    /**
     * Determines if the menu will be closed after clicking this button.
     *
     * @return <code>true</code> to close the menu, <code>false</code> to leave it open. This defaults to false.
     */
    public boolean closeOnExecute() {
        return false;
    }

    /**
     * Determines if console should execute the command instead of by the player.
     *
     * @return <code>true</code> to have console execute the command, <code>false</code> otherwise. This defaults to false.
     */
    public boolean executeByConsole() {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @param user The {@link IMenuHolder} that clicked this button.
     * @param menu The {@link Menu} that this button was clicked in.
     * @param click The {@link ClickType} that was used to click this button.
     */
    @Override
    public final void onClicked(@NotNull IMenuHolder user, Menu menu, ClickType click) {
        Bukkit.dispatchCommand(executeByConsole() ? Bukkit.getConsoleSender() : user.getBase(),
                getCommand().startsWith("/") ? getCommand().substring(1) : getCommand());

        if (closeOnExecute())
            user.getBase().closeInventory();
    }
}
