package com.itsschatten.yggdrasil.menus.utils;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * Single-use runnable will only ever be run once.
 */
@RequiredArgsConstructor
public final class OneTimeRunnable {

    /**
     * The runnable field to access throughout the class.
     */
    @NotNull
    private final Runnable runnable;

    /**
     * The boolean to check if the runnable has been run before.
     */
    private boolean hasBeenRun = false;

    /**
     * If the runnable hasn't been run, run it.
     */
    public void attemptRun() {
        if (hasBeenRun)
            return;

        try {
            runnable.run();
        } finally {
            hasBeenRun = true;
        }
    }

    /**
     * Gets the hasBeenRun boolean.
     *
     * @return The hasBeenRun boolean.
     */
    public boolean hasBeenRun() {
        return hasBeenRun;
    }

}
