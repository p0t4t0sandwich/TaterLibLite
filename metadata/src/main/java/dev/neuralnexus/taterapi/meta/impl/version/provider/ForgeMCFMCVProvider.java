/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.version.provider;

import static dev.neuralnexus.taterapi.util.ReflectionUtil.checkForClass;

import dev.neuralnexus.taterapi.meta.MinecraftVersion;

import net.minecraftforge.fml.common.Loader;

import org.jspecify.annotations.NonNull;

public final class ForgeMCFMCVProvider implements MinecraftVersion.Provider {
    @Override
    public boolean shouldProvide() {
        return checkForClass("net.minecraftforge.fml.common.Loader");
    }

    @Override
    public @NonNull MinecraftVersion get() {
        String version = "Unknown";
        try {
            // Reflect to get net.minecraftforge.fml.common.Loader.MC_VERSION
            version = (String) Loader.class.getField("MC_VERSION").get(null);
        } catch (ReflectiveOperationException ignored) {
        }
        return MinecraftVersion.of(version);
    }
}
