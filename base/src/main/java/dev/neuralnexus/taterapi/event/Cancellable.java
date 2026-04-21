/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.event;

import org.jspecify.annotations.NonNull;

/** Abstract class for cancellable events. */
public interface Cancellable {
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

    Dummy DUMMY = new Dummy();

    static Cancellable dummy() {
        return DUMMY;
    }

    static Cancellable simple() {
        return new Simple();
    }

    static Cancellable callback(final @NonNull Runnable action) {
        return new Callback(action);
    }

    final class Dummy implements Cancellable {
        @Override
        public boolean cancelled() {
            return false;
        }

        @Override
        public void setCancelled(boolean cancelled) {}
    }

    final class Simple implements Cancellable {
        private boolean cancelled = false;

        @Override
        public boolean cancelled() {
            return this.cancelled;
        }

        @Override
        public void setCancelled(final boolean cancelled) {
            this.cancelled = cancelled;
        }
    }

    /** A cancellable impl that runs a callback when cancelled. */
    final class Callback implements Cancellable {
        private boolean cancelled = false;
        private final Runnable action;

        public Callback(final @NonNull Runnable action) {
            this.action = action;
        }

        @Override
        public boolean cancelled() {
            return this.cancelled;
        }

        @Override
        public void setCancelled(final boolean cancelled) {
            if (this.cancelled) return;
            this.cancelled = cancelled;
            if (cancelled) {
                this.action.run();
            }
        }
    }
}
