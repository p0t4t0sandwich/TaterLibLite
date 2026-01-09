/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.version.provider;

import static dev.neuralnexus.taterapi.util.ReflectionUtil.checkForClass;

import dev.neuralnexus.taterapi.meta.MinecraftVersion;

import net.minecraftforge.fml.loading.FMLLoader;

import org.jspecify.annotations.NonNull;

import java.lang.reflect.Field;

public final class ForgeFMLMCVProvider implements MinecraftVersion.Provider {
    @Override
    public boolean shouldProvide() {
        return checkForClass("net.minecraftforge.fml.loading.FMLLoader");
    }

    @Override
    public @NonNull MinecraftVersion get() {
        String version = "Unknown";
        try {
            try {
                // Reflect to get FMLLoader.versionInfo().mcVersion()
                Object versionInfoObject = FMLLoader.class.getMethod("versionInfo").invoke(null);
                version =
                        (String)
                                versionInfoObject
                                        .getClass()
                                        .getMethod("mcVersion")
                                        .invoke(versionInfoObject);
            } catch (ReflectiveOperationException e) {
                // Reflect to get private FMLLoader.mcVersion
                Field mcVersionField = FMLLoader.class.getDeclaredField("mcVersion");
                mcVersionField.setAccessible(true);
                version = (String) mcVersionField.get(null);
            }
        } catch (ReflectiveOperationException ignored) {
        }
        return MinecraftVersion.of(version);
    }
}
