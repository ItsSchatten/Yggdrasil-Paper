package com.itsschatten.yggdrasil.menus.utils;

import com.itsschatten.yggdrasil.Utils;
import com.itsschatten.yggdrasil.menus.Menu;
import com.itsschatten.yggdrasil.menus.types.interfaces.Ticking;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class to keep check of all Ticking menus.
 */
public final class TickingManager {

    /**
     * Our storage of all ticking menus.
     */
    @Getter
    private static final Set<Menu> TICKING_STANDARD_MENUS = new HashSet<>();

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
    public static boolean add(final Menu menu) {
        Validate.isTrue(menu instanceof Ticking, "To tick a menu it must implement the Ticking interface.");
        return TICKING_STANDARD_MENUS.add(menu);
    }

    /**
     * Utility method to remove a {@link Menu} into the set.
     *
     * @param menu The menu to add.
     * @return <code>true</code> if successful, <code>false</code> if otherwise.
     */
    public static boolean remove(final Menu menu) {
        return TICKING_STANDARD_MENUS.remove(menu);
    }

    /**
     * Begins the task running, called in {@link Utils#setInstance(JavaPlugin, boolean, boolean)}, only if register menus is true.
     */
    public static void beginTicking() {
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(Utils.getInstance(), () -> {
            if (TICKING_STANDARD_MENUS.size() == 0) return;
            TICKING_STANDARD_MENUS.forEach((menu) -> {
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
    public static void cancel(final Menu menu) {
        TICKING_STANDARD_MENUS.remove(menu);
    }

    /**
     * Remove all currently active ticking menus.
     */
    public static void cancelAll() {
        TICKING_STANDARD_MENUS.clear();
        Bukkit.getScheduler().cancelTask(taskId);
    }

}
