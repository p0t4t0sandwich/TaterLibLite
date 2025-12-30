/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.event;

/** Abstract class for cancellable events. */
public interface Cancellable {
    Dummy DUMMY = new Dummy();

    /**
     * Gets whether the event is cancelled.
     *
     * @return Whether the event is cancelled.
     */
    boolean cancelled();

    /**
     * Sets whether the event is cancelled.
     *
     * @param cancelled Whether the event is cancelled.
     */
    void setCancelled(boolean cancelled);

    /** Cancels the event. */
    default void cancel() {
        this.setCancelled(true);
    }

    final class Dummy implements Cancellable {
        @Override
        public boolean cancelled() {
            return false;
        }

        @Override
        public void setCancelled(boolean cancelled) {}
    }
}
