package com.itsschatten.yggdrasil.menus.buttons;

import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.utils.MenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

/**
 * A button that will execute a command for the player that clicked it.
 *
 * @see Button
 * @see AnimatedCommandButton
 */
public abstract class CommandButton<T extends MenuHolder> extends Button<T> {

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
     * @param user  The {@link T} that clicked this button.
     * @param menu  The {@link Menu} that this button was clicked in.
     * @param click The {@link ClickType} that was used to click this button.
     */
    @Override
    public final void onClicked(@NotNull T user, Menu<T> menu, ClickType click) {
        Bukkit.dispatchCommand(executeByConsole() ? Bukkit.getConsoleSender() : user.player(),
                getCommand().startsWith("/") ? getCommand().substring(1) : getCommand());

        if (closeOnExecute())
            user.player().closeInventory();
    }
}
