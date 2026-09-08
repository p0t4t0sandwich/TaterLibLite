/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.data;

import org.jspecify.annotations.NonNull;

public class MissingKeyException extends RuntimeException {
    public MissingKeyException(final @NonNull Key<?> key, final @NonNull DataHolder holder) {
        super("Key " + key.asString() + " was not found on DataHolder " + holder.getClass());
    }
}
