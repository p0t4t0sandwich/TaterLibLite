/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network;

import dev.neuralnexus.taterapi.adapter.AdapterCodec;
import dev.neuralnexus.taterapi.adapter.AdapterRegistry;

import org.jspecify.annotations.NonNull;

public final class NetworkAdapters {
    private static final AdapterRegistry REGISTRY = new AdapterRegistry();

    public static AdapterRegistry registry() {
        return REGISTRY;
    }

    public static void register(final @NonNull AdapterCodec<?, ?>... codecs) {
        REGISTRY.register(codecs);
    }
}
