/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network;

import dev.neuralnexus.taterapi.registries.AdapterRegistry;

public final class NetworkRegistry {
    private static final AdapterRegistry ADAPTER_REGISTRY = new AdapterRegistry();

    public static AdapterRegistry adapters() {
        return ADAPTER_REGISTRY;
    }
}
