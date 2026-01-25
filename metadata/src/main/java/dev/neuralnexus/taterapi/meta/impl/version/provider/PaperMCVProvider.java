/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.version.provider;

import static dev.neuralnexus.taterapi.util.ReflectionUtil.checkForMethod;

import dev.neuralnexus.taterapi.meta.MinecraftVersion;

import org.bukkit.Bukkit;
import org.jspecify.annotations.NonNull;

public final class PaperMCVProvider implements MinecraftVersion.Provider {
    @Override
    public boolean shouldProvide() {
        return checkForMethod("org.bukkit.Bukkit", "getMinecraftVersion");
    }

    @Override
    public @NonNull MinecraftVersion get() {
        return MinecraftVersion.of(Bukkit.getMinecraftVersion());
    }
}
