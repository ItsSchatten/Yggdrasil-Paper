package com.itsschatten.yggdrasil.menus.utils;

import com.itsschatten.yggdrasil.Utils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 * Utility class that allows a BukkitTask to be reinitialized after it has been canceled.
 */
public abstract class ReschedulableTask implements Runnable {

    final long delay;
    final Type type;

    BukkitTask task;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    boolean cancelled;

    /**
     * Standard implementation.
     * @param delay The delay for this task.
     * @param type What type of task this is?
     */
    public ReschedulableTask(long delay, Type type) {
        this.delay = delay;
        this.type = type;
    }

    /**
     * Cancel the Bukkit task and this task.
     */
    public final void cancel() {
        setCancelled(true);
        task.cancel();
    }

    /**
     * Register this task and set it running.
     * <p>
     * This is all ran sync.
     */
    public final void register() {
        this.task = Bukkit.getScheduler().runTaskTimer(Utils.getInstance(), () -> {
            try {
                this.run();
            } catch (Exception ex) {
                this.cancel();
            }
        }, 1, delay);
    }

    /**
     * What {@link Type type} of a task this is.
     *
     * @return The Task type.
     */
    public final Type getType() {
        return type;
    }

    /**
     * Restart the task, registering a new Task instance.
     */
    public final void restart() {
        if (this.isCancelled() || task.isCancelled()) {
            setCancelled(false);
            register();
        }
    }

    /**
     * The type of task.
     */
    public enum Type {
        /**
         * A menu task.
         */
        MENU,
        /**
         * A button task.
         */
        BUTTON
    }

}
