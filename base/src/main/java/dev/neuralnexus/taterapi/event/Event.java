/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.event;

/** Generic event interface. */
public interface Event {
    /** Gets the event name. */
    default String name() {
        return this.getClass().getSimpleName();
    }
}
