/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.version.provider;

import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.util.MixinServiceUtil;

import org.jspecify.annotations.NonNull;

import java.io.IOException;

public final class VanillaMCVProvider implements MinecraftVersion.Provider {
    @Override
    public @NonNull MinecraftVersion get() { // TODO: Split into multiple?
        String version = "Unknown";
        try {
            version = MixinServiceUtil.mcVersion();
        } catch (ClassNotFoundException | IOException ignored) {
        }
        return MinecraftVersion.of(version);
    }
}
