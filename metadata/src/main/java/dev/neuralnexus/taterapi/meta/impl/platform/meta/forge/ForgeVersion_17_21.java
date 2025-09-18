/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta.forge;

import net.minecraftforge.fml.loading.FMLLoader;

final class ForgeVersion_17_21 {
    public static String forgeVersion() {
        return FMLLoader.versionInfo().forgeVersion();
    }
}
