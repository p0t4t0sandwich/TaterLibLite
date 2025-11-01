/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.ModInfo;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.Side;

import org.jspecify.annotations.NonNull;

import space.vectrix.ignite.Ignite;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class IgniteMeta implements Platform.Meta {
    private Platform.@NonNull Meta underlyingPlatform() {
        final MetaAPI api = MetaAPI.instance();
        Optional<Platform.Meta> meta = Optional.empty();
        if (api.isPlatformPresent(Platforms.VELOCITY)) {
            meta = api.meta(Platforms.VELOCITY);
        } else if (api.isPlatformPresent(Platforms.BUNGEECORD)) {
            meta = api.meta(Platforms.BUNGEECORD);
        } else if (api.isPlatformPresent(Platforms.PAPER)) {
            meta = api.meta(Platforms.PAPER);
        } else if (api.isPlatformPresent(Platforms.SPIGOT)) {
            meta = api.meta(Platforms.SPIGOT);
        } else if (api.isPlatformPresent(Platforms.BUKKIT)) {
            meta = api.meta(Platforms.BUKKIT);
        }
        if (meta.isEmpty()) {
            throw new IllegalStateException("Ignite is not running a supported server platform.");
        }
        return meta.get();
    }

    @Override
    public @NonNull Object server() {
        return this.underlyingPlatform().server();
    }

    @Override
    public @NonNull Object client() {
        return this.underlyingPlatform().client();
    }

    @Override
    public @NonNull Object minecraft() {
        return this.underlyingPlatform().minecraft();
    }

    @Override
    public @NonNull Side side() {
        return this.underlyingPlatform().side();
    }

    @Override
    public boolean isClient() {
        return this.underlyingPlatform().isClient();
    }

    @Override
    public @NonNull MinecraftVersion minecraftVersion() {
        return this.underlyingPlatform().minecraftVersion();
    }

    @Override
    public @NonNull String loaderVersion() {
        return "0.0.0";
    }

    @Override
    public @NonNull String apiVersion() {
        return "0.0.0";
    }

    @Override
    public @NonNull List<ModInfo> mods() {
        return Ignite.mods().containers().stream()
                .map(
                        modContainer ->
                                new ModInfoImpl(
                                        modContainer.id(),
                                        modContainer.id(),
                                        modContainer.version(),
                                        Platforms.IGNITE))
                .collect(Collectors.toList());
    }

    @Override
    public @NonNull Logger logger(@NonNull String pluginId) {
        return this.underlyingPlatform().logger(pluginId);
    }
}
