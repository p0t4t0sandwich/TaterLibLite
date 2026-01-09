/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.version.provider;

import dev.neuralnexus.taterapi.meta.MinecraftVersion;

import net.md_5.bungee.api.ProxyServer;

import org.jspecify.annotations.NonNull;

public final class BungeeCordMCVProvider implements MinecraftVersion.Provider {
    @SuppressWarnings("deprecation")
    @Override
    public @NonNull MinecraftVersion get() {
        return MinecraftVersion.of(ProxyServer.getInstance().getGameVersion());
    }
}
