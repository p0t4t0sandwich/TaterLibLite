/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta.sponge;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.logger.impl.ApacheLogger;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.ModContainer;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.Side;
import dev.neuralnexus.taterapi.meta.impl.WMinecraft;
import dev.neuralnexus.taterapi.meta.impl.WMinecraftServer;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.FabricMeta;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.ModContainerImpl;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.ModInfoImpl;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.ModResourceImpl;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.NeoForgeMeta;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.forge.ForgeData;
import dev.neuralnexus.taterapi.util.PathUtils;

import org.jspecify.annotations.NonNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.plugin.PluginContainer;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/** Stores data about the Sponge platform */
final class SpongeModernMeta implements Platform.Meta {
    @Override
    public @NonNull Object server() {
        if (this.side().isServer()) {
            return Sponge.server();
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
        return this.server();
    }

    @Override
    public @NonNull Side side() {
        if (Sponge.server() == null) {
            return Side.CLIENT;
        }
        return WMinecraftServer.isDedicatedServer(Sponge.server()) ? Side.INTEGRATED : Side.SERVER;
    }

    @Override
    public boolean isClient() {
        return this.side().isClient();
    }

    @Override
    public @NonNull String loaderVersion() {
        return Sponge.pluginManager()
                .plugin("sponge")
                .map(p -> p.metadata().version().toString())
                .orElse("Unknown");
    }

    @Override
    public @NonNull String apiVersion() {
        return Sponge.pluginManager()
                .plugin("sponge-api")
                .map(p -> p.metadata().version().toString())
                .orElse("Unknown");
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T> Collection<ModContainer<T>> mods() {
        return Sponge.pluginManager().plugins().stream()
                .map(pc -> (ModContainer<T>) this.toContainer(pc))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T> Optional<ModContainer<T>> mod(@NonNull String modId) {
        // TODO: Add mechanism to unwrap and use underlying platform's ModContainer
        // Defer to parent platforms to prevent null pointer mentioned below
        if (MetaAPI.instance().isPlatformPresent(Platforms.FORGE)) {
            Platform.Meta forge = ForgeData.create();
            if (forge != null && forge.isModLoaded(modId)) {
                return Optional.empty();
            }
        } else if (MetaAPI.instance().isPlatformPresent(Platforms.NEOFORGE)) {
            if (new NeoForgeMeta().isModLoaded(modId)) {
                return Optional.empty();
            }
        } else if (MetaAPI.instance().isPlatformPresent(Platforms.FABRIC)) {
            if (new FabricMeta().isModLoaded(modId)) {
                return Optional.empty();
            }
        }
        return Sponge.pluginManager()
                .plugin(modId)
                .map(pc -> (ModContainer<T>) this.toContainer(pc));
    }

    @Override
    public @NonNull Logger logger(final @NonNull String modId) {
        return new ApacheLogger(modId);
    }

    @Override
    public boolean isModLoaded(final @NonNull String... modId) {
        for (final String id : modId) {
            if (Sponge.pluginManager().plugin(id).isPresent()
                    || Sponge.pluginManager().plugin(id.toLowerCase(Locale.ROOT)).isPresent()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean areModsLoaded(final @NonNull String... modId) {
        for (final String id : modId) {
            if (Sponge.pluginManager().plugin(id).isEmpty()
                    && Sponge.pluginManager().plugin(id.toLowerCase(Locale.ROOT)).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private @NonNull ModContainer<PluginContainer> toContainer(
            final @NonNull PluginContainer container) {
        // TODO: Doesn't work from inside Neo/Forge mod's constructor, since
        // ForgePluginContainer#instance is null
        return new ModContainerImpl<>(
                container,
                new ModInfoImpl(
                        container.metadata().id(),
                        container.metadata().name().orElse("Unknown"),
                        container.metadata().version().toString(),
                        Platforms.SPONGE),
                new ModResourceImpl(
                        () -> PathUtils.getPathFromClass(container.instance().getClass())));
    }
}
