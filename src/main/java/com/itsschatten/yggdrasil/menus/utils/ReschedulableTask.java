package com.itsschatten.yggdrasil.menus.utils;

import com.itsschatten.yggdrasil.Utils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

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
     *
     * @param delay The delay for this task.
     * @param type  What type of task this is?
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
                Utils.logError(ex);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReschedulableTask that = (ReschedulableTask) o;
        return delay == that.delay && isCancelled() == that.isCancelled() && getType() == that.getType() && Objects.equals(task, that.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delay, getType(), task, isCancelled());
    }

    @Override
    public String toString() {
        return "ReschedulableTask{" +
                "delay=" + delay +
                ", type=" + type +
                ", task=" + task +
                ", cancelled=" + cancelled +
                '}';
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
