/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.version.provider;

import com.velocitypowered.api.network.ProtocolVersion;

import dev.neuralnexus.taterapi.meta.MinecraftVersion;

import org.jspecify.annotations.NonNull;

public final class VelocityMCVProvider implements MinecraftVersion.Provider {
    @Override
    public @NonNull MinecraftVersion get() {
        return MinecraftVersion.of(ProtocolVersion.MAXIMUM_VERSION.toString());
    }
}
