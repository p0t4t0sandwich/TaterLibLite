/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.platform;

import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.impl.platform.PlatformImpl;

public interface Forge {
    Platform FORGE =
            new PlatformImpl(
                    "Forge",
                    "net.minecraftforge.fml.loading.FMLLoader",
                    "net.minecraftforge.fml.common.Loader",
                    "cpw.mods.fml.common.Loader");
    Platform GOLDENFORGE =
            new PlatformImpl("GoldenForge", "org.goldenforgelauncher.GoldenForgeEntryPoint");
    Platform NEOFORGE = new PlatformImpl("NeoForge", "net.neoforged.neoforge.common.NeoForge");
}
