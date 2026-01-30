/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.platform;

import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.impl.platform.PlatformImpl;

public interface Hybrid {
    // Multi-Platform Bukkit Hybrids
    Platform ARCLIGHT =
            new PlatformImpl(
                    "Arclight",
                    "io.izzel.arclight.api.Arclight",
                    "io.izzel.arclight.common.ArclightMain",
                    "io.izzel.arclight.forge.ArclightMod",
                    "io.izzel.arclight.neoforge.ArclightMod",
                    "io.izzel.arclight.fabric.ArclightModEntrypoint");
    // Forge-only Arclight Fork
    Platform LUMINARA =
            new PlatformImpl("Luminara", "io.izzel.arclight.common.mod.command.LuminaraCommand");

    // Fabric+Bukkit Hybrids
    Platform CARDBOARD = new PlatformImpl("Cardboard", "org.cardboardpowered.CardboardConfig");
    Platform BANNER = new PlatformImpl("Banner", "com.mohistmc.banner.BannerMCStart");
    Platform TAIYITIST =
            new PlatformImpl(
                    "Taiyitist",
                    "com.taiyitistmc.TaiyitistMain",
                    "org.teneted.taiyitist.TaiyitistMain");

    // Forge+Bukkit Hybrids
    Platform MCPCPLUSPLUS = new PlatformImpl("MCPC++", "not.defined"); // TODO: Find a MCPC++ class
    Platform CAULDRON = new PlatformImpl("Cauldron", "net.minecraftforge.cauldron.CauldronConfig");
    Platform KCAULDRON =
            new PlatformImpl("KCauldron", "net.minecraftforge.kcauldron.KCauldronConfig");
    Platform THERMOS = new PlatformImpl("Thermos", "thermos.ThermosConfig");
    Platform CRUCIBLE = new PlatformImpl("Crucible", "io.github.crucible.CrucibleConfig");
    Platform MOHIST =
            new PlatformImpl("Mohist", "com.mohistmc.MohistMC", "com.mohistmc.MohistMCStart");
    Platform CATSERVER =
            new PlatformImpl(
                    "CatServer", "catserver.server.CatServerLaunch", "org.foxserver.FoxServer");
    Platform MAGMA =
            new PlatformImpl(
                    "Magma",
                    "org.magmafoundation.magma.Magma",
                    "org.magmafoundation.magma.MagmaStart");
    Platform KETTING = new PlatformImpl("Ketting", "org.kettingpowered.ketting.core.Ketting");

    // NeoForge+Bukkit Hybrids
    Platform YOUER =
            new PlatformImpl(
                    "Youer", "com.mohistmc.launcher.youer.Main", "com.mohistmc.youer.Youer");
    Platform NEOTENET = new PlatformImpl("NeoTenet", "org.teneted.neotenet.NeoTenet");
}
