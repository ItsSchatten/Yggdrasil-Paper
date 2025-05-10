package com.itsschatten.yggdrasil.menus;

import com.itsschatten.yggdrasil.Utils;
import com.itsschatten.yggdrasil.menus.utils.MenuHolder;
import com.itsschatten.yggdrasil.menus.utils.MenuListeners;
import com.itsschatten.yggdrasil.menus.utils.TickingManager;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class MenuUtils {

    /**
     * Initialize the default player manager, register the menu listener, and begin the ticking manager.
     *
     * @param plugin The plugin to register the events too.
     */
    public void initialize(final @NotNull Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new MenuListeners(), plugin);

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
     * Shutdowns the {@link TickingManager}.
     */
    public void shutdown() {
        TickingManager.cancelAll();
    }

    /**
     * Sends a message of an error if the {@link Player} has the developer permission.
     *
     * @param holder      The player to send the message to.
     * @param throwable   The error to send.
     * @see Utils#sendDeveloperErrorMessage(Audience, Throwable)
     */
    public void sendDeveloperErrorMessage(final @NotNull MenuHolder holder, final Throwable throwable) {
        Utils.sendDeveloperErrorMessage(holder.player(), throwable);
    }

}
