package com.itsschatten.yggdrasil.menus.utils;

import com.itsschatten.yggdrasil.Utils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Utility class that allows a BukkitTask to be reinitialized after it has been canceled.
 */
public abstract class ReschedulableTask implements Runnable {

    /**
     * Delay between running the task.
     */
    final long delay;

    /**
     * The {@link Type} of Task this is.
     */
    @NotNull
    final Type type;

    /**
     * The physical task, this will always be a timer task.
     */
    BukkitTask task;

    /**
     * If this event has been canceled or not.
     */
    @Getter
    @Setter(AccessLevel.PRIVATE)
    boolean canceled;

    /**
     * Standard implementation.
     *
     * @param delay The delay for this task.
     * @param type  What type of task this is?
     */
    public ReschedulableTask(long delay, @NotNull Type type) {
        this.delay = delay;
        this.type = type;
    }

    /**
     * Cancel the Bukkit task and this task.
     */
    public final void cancel() {
        setCanceled(true);
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
    public final @NotNull Type getType() {
        return type;
    }

    /**
     * Restart the task, registering a new Task instance.
     */
    public final void restart() {
        if (this.isCanceled() || task.isCancelled()) {
            setCanceled(false);
            register();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReschedulableTask that = (ReschedulableTask) o;
        return delay == that.delay && isCanceled() == that.isCanceled() && getType() == that.getType() && Objects.equals(task, that.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delay, getType(), task, isCanceled());
    }

    @Override
    public String toString() {
        return "ReschedulableTask{" +
                "delay=" + delay +
                ", type=" + type +
                ", task=" + task +
                ", cancelled=" + canceled +
                '}';
    }

    /**
     * The type of task.
     */
    public enum Type {
        /**
         * A menu task, usually runs against the entire menu.
         */
        MENU,

        /**
         * A button task, runs against a button.
         */
        BUTTON
    }

}
