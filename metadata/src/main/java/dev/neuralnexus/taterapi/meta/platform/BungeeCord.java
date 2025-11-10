/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.platform;

import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.impl.platform.PlatformImpl;

public interface BungeeCord {
    Platform BUNGEECORD = new PlatformImpl("BungeeCord", "net.md_5.bungee.api.ProxyServer");
    Platform WATERFALL =
            new PlatformImpl(
                    "Waterfall", "io.github.waterfallmc.waterfall.conf.WaterfallConfiguration");
    Platform TRAVERTINE =
            new PlatformImpl(
                    "Travertine",
                    "io.github.waterfallmc.travertine.protocol.MultiVersionPacketV17");
    Platform LIGHTFALL =
            new PlatformImpl("LightFall", "io.izzel.lightfall.forge.ModernForgeClientHandler");
}
