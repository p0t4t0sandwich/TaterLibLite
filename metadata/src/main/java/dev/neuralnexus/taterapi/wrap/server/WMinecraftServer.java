/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.wrap.server;

import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.reflecto.Reflecto;

import org.jspecify.annotations.NonNull;

public final class WMinecraftServer {
    public static final String MINECRAFT_SERVER = "MinecraftServer";
    public static final String IS_DEDICATED_SERVER = "isDedicatedServer";

    @SuppressWarnings("unchecked")
    public static <T> @NonNull T getServer() {
        return (T) MetaAPI.instance().server();
    }

    public static boolean isDedicatedServer(Object server) {
        return Reflecto.invoke(MINECRAFT_SERVER, IS_DEDICATED_SERVER, server);
    }
}
