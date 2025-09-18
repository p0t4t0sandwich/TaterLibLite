/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl;

/** Wraps the MC server instance */
public final class WMinecraftServer {
    public static boolean isDedicatedServer(Object server) {
        return MetaAPIImpl.store.invokeMethod("MinecraftServer", "isDedicatedServer", server);
    }
}
