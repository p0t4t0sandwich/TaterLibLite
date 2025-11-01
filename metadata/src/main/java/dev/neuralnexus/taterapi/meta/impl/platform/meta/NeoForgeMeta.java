/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.logger.impl.Slf4jLogger;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.ModInfo;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.Side;
import dev.neuralnexus.taterapi.meta.impl.WMinecraft;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.neoforge.NeoForgeData;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.stream.Collectors;

/** Stores data about the NeoForge platform */
public final class NeoForgeMeta implements Platform.Meta {
    @Override
    public @NonNull Object server() {
        if (this.side().isServer()) {
            return this.minecraft();
        }
        return this.client();
    }

    @Override
    public @NonNull Object client() {
        return WMinecraft.getInstance();
    }

    @Override
    public @NonNull Object minecraft() {
        if (this.side().isClient() && WMinecraft.hasServer()) {
            return WMinecraft.getServer();
        }
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public @NonNull Side side() {
        return WMinecraft.determineSide(this.isClient());
    }

    @Override
    public boolean isClient() {
        return NeoForgeData.dist().isClient();
    }

    @Override
    public @NonNull MinecraftVersion minecraftVersion() {
        return MinecraftVersion.of(NeoForgeData.versionInfo().mcVersion());
    }

    @Override
    public @NonNull String loaderVersion() {
        return NeoForgeData.versionInfo().fmlVersion();
    }

    @Override
    public @NonNull String apiVersion() {
        return NeoForgeData.versionInfo().neoForgeVersion();
    }

    @Override
    public @NonNull List<ModInfo> mods() {
        List<net.neoforged.fml.loading.moddiscovery.ModInfo> mods = null;
        if (ModList.get() != null) {
            mods = ModList.get().getMods();
        }
        if (mods == null || mods.isEmpty()) {
            mods = LoadingModList.get().getMods();
        }
        return mods.stream()
                .map(
                        modContainer ->
                                new ModInfoImpl(
                                        modContainer.getModId(),
                                        modContainer.getDisplayName(),
                                        modContainer.getVersion().toString(),
                                        Platforms.NEOFORGE))
                .collect(Collectors.toList());
    }

    @Override
    public @NonNull Logger logger(@NonNull String modId) {
        return new Slf4jLogger(modId);
    }
}
