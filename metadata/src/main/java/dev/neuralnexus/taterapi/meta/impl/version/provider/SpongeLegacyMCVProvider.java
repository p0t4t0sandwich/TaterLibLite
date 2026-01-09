/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.version.provider;

import static dev.neuralnexus.taterapi.util.ReflectionUtil.checkForMethod;

import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import org.jspecify.annotations.NonNull;
import org.spongepowered.api.Sponge;

public final class SpongeLegacyMCVProvider implements MinecraftVersion.Provider {
    @Override
    public boolean shouldProvide() {
        return checkForMethod("org.spongepowered.api.Sponge", "getPlatform");
    }

    @Override
    public @NonNull MinecraftVersion get() {
        try {
            return Sponge.getPluginManager()
                    .getPlugin("minecraft")
                    .map(p -> p.getVersion().toString())
                    .map(MinecraftVersion::of)
                    .orElse(MinecraftVersions.UNKNOWN);
        } catch (IllegalStateException ignored) {
        }
        return MinecraftVersions.UNKNOWN;
    }
}
