/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.platform;

import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.impl.platform.PlatformImpl;

public interface Fabric {
    Platform FABRIC = new PlatformImpl("Fabric", "net.fabricmc.loader.api.FabricLoader");
    Platform QUILT = new PlatformImpl("Quilt", "net.quiltservertools.quilt.api.QuiltServer");

    // Fabric+Bukkit Hybrids
    Platform CARDBOARD = new PlatformImpl("Cardboard", "org.cardboardpowered.CardboardConfig");
    Platform BANNER = new PlatformImpl("Banner", "com.mohistmc.banner.BannerMCStart");
}
