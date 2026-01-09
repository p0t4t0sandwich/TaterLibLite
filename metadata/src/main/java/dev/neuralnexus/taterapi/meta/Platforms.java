/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta;

import dev.neuralnexus.taterapi.meta.impl.platform.PlatformImpl;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.FabricMeta;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.NeoForgeMeta;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.forge.ForgeData;
import dev.neuralnexus.taterapi.meta.platform.Bukkit;
import dev.neuralnexus.taterapi.meta.platform.BungeeCord;
import dev.neuralnexus.taterapi.meta.platform.Fabric;
import dev.neuralnexus.taterapi.meta.platform.Forge;
import dev.neuralnexus.taterapi.meta.platform.Hybrid;
import dev.neuralnexus.taterapi.meta.platform.Misc;
import dev.neuralnexus.taterapi.meta.platform.Sponge;
import dev.neuralnexus.taterapi.meta.platform.Vanilla;
import dev.neuralnexus.taterapi.meta.platform.Velocity;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** A class that provides information about the platforms that are available */
public final class Platforms
        implements Bukkit, BungeeCord, Fabric, Forge, Hybrid, Misc, Sponge, Vanilla, Velocity {
    public static final Platform UNKNOWN = new PlatformImpl("Unknown");
    private static final Set<Platform> platforms = new HashSet<>();
    private static final Set<Platform> layered = new LinkedHashSet<>();

    /**
     * Returns all platforms that are available.
     *
     * @return An array of all available platforms
     */
    public static Set<Platform> get() {
        if (platforms.isEmpty()) {
            detectPlatforms(true);
        }
        if (layered.isEmpty()) {
            layerPlatforms();
        }
        return layered;
    }

    /**
     * Detects all platforms that are available. Doesn't need to be called manually, unless somehow
     * it was called before all platforms were loaded. In such a case, call this method with the
     * force parameter set to true.
     *
     * @param force If true, the platforms will be detected again, even if they were already
     *     detected.
     */
    public static void detectPlatforms(boolean force) {
        if (!force && !platforms.isEmpty()) {
            return;
        }

        // Forge
        if (GOLDENFORGE.detect(force)) {
            platforms.addAll(List.of(GOLDENFORGE, FORGE));
            // Forge Hybrids
        } else if (MCPCPLUSPLUS.detect(force)) {
            platforms.addAll(List.of(MCPCPLUSPLUS, FORGE));
        } else if (CAULDRON.detect(force)) {
            platforms.addAll(List.of(CAULDRON, FORGE));
        } else if (KCAULDRON.detect(force)) {
            platforms.addAll(List.of(KCAULDRON, FORGE));
        } else if (THERMOS.detect(force)) {
            platforms.addAll(List.of(THERMOS, FORGE));
        } else if (CRUCIBLE.detect(force)) {
            platforms.addAll(List.of(CRUCIBLE, FORGE));
        } else if (MOHIST.detect(force)) {
            platforms.addAll(List.of(MOHIST, FORGE));
        } else if (CATSERVER.detect(force)) {
            platforms.addAll(List.of(CATSERVER, FORGE));
        } else if (KETTING.detect(force)) {
            platforms.addAll(List.of(KETTING, FORGE));
        } else if (FORGE.detect(force)) {
            platforms.add(FORGE);
        }

        // NeoForge
        if (YOUER.detect(force)) {
            platforms.addAll(List.of(YOUER, NEOFORGE));
        } else if (NEOFORGE.detect(force)) {
            platforms.add(NEOFORGE);
        }

        // Fabric
        if (QUILT.detect(force)) {
            platforms.addAll(List.of(QUILT, FABRIC));
            // Fabric Hybrids
        } else if (CARDBOARD.detect(force)) {
            platforms.addAll(List.of(CARDBOARD, FABRIC));
        } else if (BANNER.detect(force)) {
            platforms.addAll(List.of(BANNER, FABRIC));
        } else if (FABRIC.detect(force)) {
            platforms.add(FABRIC);
        }

        // Bukkit
        if (PURPUR.detect(force)) {
            platforms.addAll(List.of(PURPUR, PUFFERFISH, PAPER, SPIGOT, BUKKIT));
        } else if (PUFFERFISH.detect(force)) {
            platforms.addAll(List.of(PUFFERFISH, PAPER, SPIGOT, BUKKIT));
        } else if (PAPER.detect(force)) {
            platforms.addAll(List.of(PAPER, SPIGOT, BUKKIT));
        } else if (SPIGOT.detect(force)) {
            platforms.addAll(List.of(SPIGOT, BUKKIT));
        } else if (POSEIDON.detect(force)) {
            platforms.addAll(List.of(POSEIDON, BUKKIT));
        } else if (BUKKIT.detect(force)) {
            platforms.add(BUKKIT);
        }
        if (FOLIA.detect(force)) {
            platforms.add(FOLIA);
        }

        // BungeeCord
        if (TRAVERTINE.detect(force)) {
            platforms.addAll(List.of(TRAVERTINE, WATERFALL, BUNGEECORD));
        } else if (LIGHTFALL.detect(force)) {
            platforms.addAll(List.of(LIGHTFALL, WATERFALL, BUNGEECORD));
        } else if (WATERFALL.detect(force)) {
            platforms.addAll(List.of(WATERFALL, BUNGEECORD));
        } else if (BUNGEECORD.detect(force)) {
            platforms.add(BUNGEECORD);
        }

        // Hybrid
        if (ARCLIGHT.detect(force)) {
            platforms.add(ARCLIGHT);
        } else if (MAGMA.detect(force)) {
            platforms.add(MAGMA);
        }

        // Sponge
        if (SPONGE.detect(force)) {
            platforms.add(SPONGE);
        }

        // Velocity
        if (VELOCITY.detect(force)) {
            platforms.add(VELOCITY);
        }

        // Ignite
        if (IGNITE.detect(force)) {
            platforms.add(IGNITE);
        }
    }

    /**
     * Detects the primary platform based on the available platforms.
     *
     * @return The primary platform
     */
    public static dev.neuralnexus.taterapi.meta.enums.Platform detectPrimary() {
        final dev.neuralnexus.taterapi.meta.enums.Platform primary;
        if (platforms.contains(BUNGEECORD)) { // Check proxies
            primary = dev.neuralnexus.taterapi.meta.enums.Platform.BUNGEECORD;
        } else if (platforms.contains(VELOCITY)) {
            primary = dev.neuralnexus.taterapi.meta.enums.Platform.VELOCITY;
        } else if (platforms.contains(FORGE)
                && platforms.contains(FABRIC)) { // Check for Connector and Kilt
            if (new FabricMeta().isModLoaded("kilt")) {
                primary = dev.neuralnexus.taterapi.meta.enums.Platform.FABRIC;
            } else if (ForgeData.create().isModLoaded("connector")) {
                primary = dev.neuralnexus.taterapi.meta.enums.Platform.FORGE;
            } else {
                throw new IllegalArgumentException(
                        "Both Forge and Fabric platforms detected, but neither Connector nor Kilt found. Cannot determine primary platform.");
            }
        } else if (platforms.contains(NEOFORGE)
                && platforms.contains(FABRIC)
                && new NeoForgeMeta().isModLoaded("connector")) { // Check for Connector
            primary = dev.neuralnexus.taterapi.meta.enums.Platform.NEOFORGE;
        } else if (platforms.contains(FORGE)) {
            primary = dev.neuralnexus.taterapi.meta.enums.Platform.FORGE;
        } else if (platforms.contains(NEOFORGE)) {
            primary = dev.neuralnexus.taterapi.meta.enums.Platform.NEOFORGE;
        } else if (platforms.contains(FABRIC)) {
            primary = dev.neuralnexus.taterapi.meta.enums.Platform.FABRIC;
        } else if (platforms.contains(SPONGE)) {
            primary = dev.neuralnexus.taterapi.meta.enums.Platform.SPONGE;
        } else if (platforms.contains(PAPER)) {
            primary = dev.neuralnexus.taterapi.meta.enums.Platform.PAPER;
        } else if (platforms.contains(BUKKIT)) {
            primary = dev.neuralnexus.taterapi.meta.enums.Platform.BUKKIT;
        } else {
            primary = dev.neuralnexus.taterapi.meta.enums.Platform.UNKNOWN;
        }
        return primary;
    }

    /** Layers the detected platforms based on their relationships */
    private static void layerPlatforms() {
        layered.clear();

        final dev.neuralnexus.taterapi.meta.enums.Platform primary = detectPrimary();
        switch (primary) {
            case FABRIC -> {
                layered.add(FABRIC);
                if (platforms.contains(SPONGE)) { // Loofah
                    layered.add(SPONGE);
                }
                if (platforms.contains(FORGE)) { // Kilt
                    layered.add(FORGE);
                }
                if (platforms.contains(PAPER)) { // Hybrid Paper
                    layered.add(PAPER);
                }
                layered.add(VANILLA); // Vanilla
                if (platforms.contains(BUKKIT)) { // Hybrid Bukkit
                    layered.add(BUKKIT);
                }
                layered.addAll(platforms);
            }
            case FORGE -> {
                layered.add(FORGE);
                if (platforms.contains(SPONGE)) { // SpongeForge
                    layered.add(SPONGE);
                }
                if (platforms.contains(FABRIC)) { // Connector
                    layered.add(FABRIC);
                }
                if (platforms.contains(PAPER)) { // Hybrid Paper
                    layered.add(PAPER);
                }
                layered.add(VANILLA); // Vanilla
                if (platforms.contains(BUKKIT)) { // Hybrid Bukkit
                    layered.add(BUKKIT);
                }
                layered.addAll(platforms);
            }
            case NEOFORGE -> {
                layered.add(NEOFORGE);
                if (platforms.contains(SPONGE)) { // SpongeNeo
                    layered.add(SPONGE);
                }
                if (platforms.contains(FABRIC)) { // Connector
                    layered.add(FABRIC);
                }
                if (platforms.contains(PAPER)) { // Hybrid Paper
                    layered.add(PAPER);
                }
                layered.add(VANILLA); // Vanilla
                if (platforms.contains(BUKKIT)) { // Hybrid Bukkit
                    layered.add(BUKKIT);
                }
                layered.addAll(platforms);
            }
            case SPONGE -> {
                layered.add(SPONGE);
                if (platforms.contains(PAPER)) { // Sponge on Paper
                    layered.add(PAPER);
                }
                layered.add(VANILLA); // Vanilla
                if (platforms.contains(PAPER)) { // Soak Paper
                    layered.add(PAPER);
                }
                if (platforms.contains(BUKKIT)) { // Soak Bukkit
                    layered.add(BUKKIT);
                }
                layered.addAll(platforms);
            }
            case PAPER -> {
                layered.add(primary.ref());
                if (platforms.contains(IGNITE)) { // Ignite
                    layered.add(IGNITE);
                }
                layered.add(VANILLA); // Vanilla
                if (platforms.contains(BUKKIT)) { // Bukkit
                    layered.add(BUKKIT);
                }
                layered.addAll(platforms);
            }
            case BUKKIT -> {
                layered.add(primary.ref());
                if (platforms.contains(IGNITE)) { // Ignite
                    layered.add(IGNITE);
                }
                layered.add(VANILLA); // Vanilla
                layered.addAll(platforms);
            }
            case BUNGEECORD, VELOCITY -> { // Proxies
                layered.add(primary.ref());
                if (platforms.contains(IGNITE)) { // Ignite
                    layered.add(IGNITE);
                }
                layered.addAll(platforms);
            }
            default -> layered.addAll(platforms); // Fallback
        }
    }
}
