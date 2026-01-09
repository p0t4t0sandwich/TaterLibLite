/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.ModContainer;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.Side;

import org.jspecify.annotations.NonNull;

import space.vectrix.ignite.Ignite;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
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
    public @NonNull String loaderVersion() {
        return "0.0.0";
    }

    @Override
    public @NonNull String apiVersion() {
        return "0.0.0";
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T> Collection<ModContainer<T>> mods() {
        return Ignite.mods().containers().stream()
                .map(mc -> (ModContainer<T>) this.toContainer(mc))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T> Optional<ModContainer<T>> mod(@NonNull String modId) {
        Optional<space.vectrix.ignite.mod.ModContainer> container = Ignite.mods().container(modId);
        if (container.isEmpty()) {
            container = Ignite.mods().container(modId.toLowerCase(Locale.ROOT));
        }
        return container.map(mc -> (ModContainer<T>) this.toContainer(mc));
    }

    @Override
    public @NonNull Logger logger(@NonNull String pluginId) {
        return this.underlyingPlatform().logger(pluginId);
    }

    @Override
    public boolean isModLoaded(@NonNull String... modId) {
        for (final String id : modId) {
            if (Ignite.mods().container(id).isPresent()
                    || Ignite.mods().container(id.toLowerCase(Locale.ROOT)).isPresent()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean areModsLoaded(@NonNull String... modId) {
        final List<String> ids = List.of(modId);
        for (final String id : ids) {
            if (Ignite.mods().container(id).isEmpty()
                    && Ignite.mods().container(id.toLowerCase(Locale.ROOT)).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private @NonNull ModContainer<space.vectrix.ignite.mod.ModContainer> toContainer(
            final space.vectrix.ignite.mod.@NonNull ModContainer mc) {
        return new ModContainerImpl<>(
                mc,
                new ModInfoImpl(mc.id(), mc.id(), mc.version(), Platforms.IGNITE),
                new ModResourceImpl(() -> mc.resource().path()));
    }
}
