/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta.neoforge;

import static dev.neuralnexus.taterapi.util.ReflectionUtil.checkForMethod;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.VersionInfo;

/** Stores data about the NeoForge platform */
public class NeoForgeData {
    public static final boolean NeoFML_21_9 =
            checkForMethod("net.neoforged.fml.loading.FMLLoader", "getCurrentOrNull");

    public static Dist dist() {
        if (NeoFML_21_9) {
            return FMLLoader.getCurrentOrNull().getDist();
        }
        return FMLLoader.getDist();
    }

    public static VersionInfo versionInfo() {
        if (NeoFML_21_9) {
            return FMLLoader.getCurrentOrNull().getVersionInfo();
        }
        return FMLLoader.versionInfo();
    }
}
