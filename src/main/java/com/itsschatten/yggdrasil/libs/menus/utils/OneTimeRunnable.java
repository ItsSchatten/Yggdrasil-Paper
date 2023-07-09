package com.itsschatten.yggdrasil.libs.menus.utils;

import lombok.RequiredArgsConstructor;

/**
 * Ensure this runnable was run only once.
 */
@RequiredArgsConstructor
public class OneTimeRunnable {

    /**
     * The runnable field to access throughout the class.
     */
    private final Runnable runnable;

    /**
     * The boolean to check if the runnable has been run before.
     */
    private boolean hasBeenRun = false;

    /**
     * If the runnable hasn't been run, run it.
     */
    public final void attemptRun() {
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
    public final boolean hasBeenRun() {
        return hasBeenRun;
    }
}
