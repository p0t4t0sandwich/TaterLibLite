/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.version.provider;

import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import org.jspecify.annotations.NonNull;

import java.util.Optional;

public final class FabricMCVProvider implements MinecraftVersion.Provider {
    @Override
    public @NonNull MinecraftVersion get() {
        Optional<ModContainer> mcContainer =
                FabricLoader.getInstance().getModContainer("minecraft");
        if (mcContainer.isEmpty()) {
            return MinecraftVersions.UNKNOWN;
        }
        return MinecraftVersion.of(
                mcContainer.get().getMetadata().getVersion().getFriendlyString());
    }
}
