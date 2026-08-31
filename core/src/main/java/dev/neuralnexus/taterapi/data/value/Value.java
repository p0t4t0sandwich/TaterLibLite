/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.data.value;

import dev.neuralnexus.taterapi.data.Key;

public interface Value<E> {
    /**
     * The value
     *
     * @return The value
     */
    E get();

    /**
     * The key for this value
     *
     * @return The key
     */
    Key<? extends Value<E>> key();
}
