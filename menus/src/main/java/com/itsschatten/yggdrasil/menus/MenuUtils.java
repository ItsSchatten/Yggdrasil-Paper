package com.itsschatten.yggdrasil.menus;

import com.itsschatten.yggdrasil.Utils;
import com.itsschatten.yggdrasil.menus.manager.DefaultPlayerManager;
import com.itsschatten.yggdrasil.menus.utils.IMenuHolder;
import com.itsschatten.yggdrasil.menus.utils.IMenuHolderManager;
import com.itsschatten.yggdrasil.menus.utils.MenuListeners;
import com.itsschatten.yggdrasil.menus.utils.TickingManager;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class MenuUtils {

    /**
     * User manager, used for menus.
     * --- GETTER ---
     * Get the registered {@link IMenuHolderManager}.
     *
     * @return Returns the registered {@link IMenuHolderManager}.
     */
    @Getter
    private IMenuHolderManager manager;

    /**
     * Default player manager.
     */
    // There is no getter for this, you should instead be using #getManager()
    private DefaultPlayerManager defaultPlayerManager;

    /**
     * Initialize the default player manager, register the menu listener, and begin the ticking manager.
     *
     * @param plugin The plugin to register the events too.
     */
    public void initialize(final @NotNull Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new MenuListeners(), plugin);

        // Register the default player manager, so we can use menus "out of the box."
        // This also registers the class as a listener
        // to remove the player from the player map as they leave the server.
        final DefaultPlayerManager defaultPlayerManager = new DefaultPlayerManager();
        MenuUtils.defaultPlayerManager = defaultPlayerManager;
        plugin.getServer().getPluginManager().registerEvents(defaultPlayerManager, plugin);
        setManager(defaultPlayerManager);

        plugin.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPluginDisable(PluginDisableEvent event) {
                if (event.getPlugin().getName().equals(plugin.getName())) {
                    shutdown();
                }
            }
        }, plugin);


        TickingManager.beginTicking();
    }

    /**
     * Shutdowns the {@link TickingManager} and calls the shutdown logic of the registered {@link IMenuHolderManager}.
     */
    public void shutdown() {
        TickingManager.cancelAll();
        manager.shutdown();
    }

    /**
     * Sets the manager instance and unregisters the default player manager if it's registered.
     *
     * @param manager The manager instance to set as the new manager.
     */
    public static void setManager(IMenuHolderManager manager) {
        if (defaultPlayerManager != null) {
            HandlerList.unregisterAll(defaultPlayerManager);
            MenuUtils.defaultPlayerManager = null;
        }

        MenuUtils.manager = manager;
    }

    /**
     * Sends a message of an error if the {@link Player} has the developer permission.
     *
     * @param user      The player to send the message to.
     * @param throwable The error to send.
     * @see Utils#sendDeveloperErrorMessage(Player, Throwable)
     */
    public void sendDeveloperErrorMessage(final @NotNull IMenuHolder user, final Throwable throwable) {
        Utils.sendDeveloperErrorMessage(user.getBase(), throwable);
    }

}
