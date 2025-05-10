package com.itsschatten.yggdrasil.menus.utils;

import com.itsschatten.yggdrasil.Utils;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.types.interfaces.Ticking;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class to keep check of all Ticking menus.
 */
public final class TickingManager {

    /**
     * Our storage of all ticking menus.
     * --- GETTER ---
     * Returns the ticking menu.
     *
     * @return a {@link HashSet} of all {@link Menu} that are ticking.
     */
    @Getter
    @Accessors(fluent = true)
    @NotNull
    private static final Set<Menu<? extends MenuHolder>> TICKING_MENUS = new HashSet<>();

    /**
     * The id of the task responsible for ticking all menus.
     */
    private static int taskId;

    /**
     * Utility method to add a {@link Menu} into the set.
     *
     * @param menu The menu to add.
     * @return <code>true</code> if successful, <code>false</code> if otherwise.
     */
    public static boolean add(final Menu<? extends MenuHolder> menu) {
        Validate.isTrue(menu instanceof Ticking, "To tick a menu it must implement the Ticking interface.");
        return TICKING_MENUS.add(menu);
    }

    /**
     * Utility method to remove a {@link Menu} into the set.
     *
     * @param menu The menu to add.
     * @return <code>true</code> if successful, <code>false</code> if otherwise.
     */
    public static boolean remove(final Menu menu) {
        return TICKING_MENUS.remove(menu);
    }

    /**
     * Begins the task running, called in {@link com.itsschatten.yggdrasil.menus.MenuUtils#initialize(Plugin)}.
     */
    public static void beginTicking() {
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(Utils.getInstance(), () -> {
            if (TICKING_MENUS.isEmpty()) return;
            TICKING_MENUS.forEach((menu) -> {
                if (menu instanceof Ticking ticking) {
                    ticking.tick();
                }
            });
        }, 0, 1).getTaskId();
    }

    /**
     * Cancel a Ticking Menu.
     *
     * @param menu The Menu to cancel.
     */
    public static void cancel(final Menu<? extends MenuHolder> menu) {
        TICKING_MENUS.remove(menu);
    }

    /**
     * Remove all currently active ticking menus.
     */
    public static void cancelAll() {
        TICKING_MENUS.clear();
        Bukkit.getScheduler().cancelTask(taskId);
    }

}
