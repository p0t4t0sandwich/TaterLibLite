/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network;

import org.jspecify.annotations.NonNull;

public final class CryptException extends Exception {
    public CryptException(final @NonNull Throwable cause) {
        super(cause);
    }
}
