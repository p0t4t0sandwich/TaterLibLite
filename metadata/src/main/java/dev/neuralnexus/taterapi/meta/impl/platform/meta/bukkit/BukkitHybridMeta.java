/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta.bukkit;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.FabricMeta;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.NeoForgeMeta;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.forge.ForgeData;

import org.jetbrains.annotations.NotNull;

/** Stores data about the Bukkit platform */
// TODO: See if there's a better way around this for hybrids
public final class BukkitHybridMeta extends BukkitMeta {
    @Override
    public @NotNull MinecraftVersion minecraftVersion() {
        if (org.bukkit.Bukkit.getServer() == null) {
            if (MetaAPI.instance().isPlatformPresent(Platforms.FORGE)) {
                Platform.Meta forge = ForgeData.create();
                if (forge != null) {
                    return forge.minecraftVersion();
                }
            } else if (MetaAPI.instance().isPlatformPresent(Platforms.NEOFORGE)) {
                Platform.Meta neoForge = new NeoForgeMeta();
                return neoForge.minecraftVersion();
            } else if (MetaAPI.instance().isPlatformPresent(Platforms.FABRIC)) {
                Platform.Meta fabric = new FabricMeta();
                return fabric.minecraftVersion();
            }
        }
        return super.minecraftVersion();
    }

    @Override
    public @NotNull Logger logger(@NotNull String modId) {
        if (org.bukkit.Bukkit.getServer() == null) {
            if (MetaAPI.instance().isPlatformPresent(Platforms.FORGE)) {
                Platform.Meta forge = ForgeData.create();
                if (forge != null) {
                    return forge.logger(modId);
                }
            } else if (MetaAPI.instance().isPlatformPresent(Platforms.NEOFORGE)) {
                Platform.Meta neoForge = new NeoForgeMeta();
                return neoForge.logger(modId);
            } else if (MetaAPI.instance().isPlatformPresent(Platforms.FABRIC)) {
                Platform.Meta fabric = new FabricMeta();
                return fabric.logger(modId);
            }
        }
        return super.logger(modId);
    }
}
