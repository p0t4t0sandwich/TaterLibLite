/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi;

/** Indicates if a platform object is wrapped */
public interface Wrapped<T> {
    /**
     * Unwrap the object.
     *
     * @return The unwrapped object
     */
    T unwrap();
}
