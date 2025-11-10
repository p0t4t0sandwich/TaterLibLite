/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta.sponge;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.logger.impl.Slf4jLogger;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.ModContainer;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.Side;
import dev.neuralnexus.taterapi.meta.impl.WMinecraft;
import dev.neuralnexus.taterapi.meta.impl.WMinecraftServer;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.ModContainerImpl;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.ModInfoImpl;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.ModResourceImpl;

import org.jspecify.annotations.NonNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/** Stores data about the Sponge platform */
final class SpongeLegacyMeta implements Platform.Meta {
    @Override
    public @NonNull Object server() {
        if (this.side().isServer()) {
            return Sponge.getServer();
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
        if (Sponge.getServer() == null) {
            return Side.CLIENT;
        }
        return WMinecraftServer.isDedicatedServer(Sponge.getServer())
                ? Side.INTEGRATED
                : Side.SERVER;
    }

    @Override
    public boolean isClient() {
        return this.side().isClient();
    }

    @Override
    public @NonNull MinecraftVersion minecraftVersion() {
        return MinecraftVersion.of(Sponge.getPlatform().getMinecraftVersion().getName());
    }

    @Override
    public @NonNull String loaderVersion() {
        return Sponge.getPluginManager()
                .getPlugin("sponge")
                .map(p -> p.getVersion().toString())
                .orElse("Unknown");
    }

    @Override
    public @NonNull String apiVersion() {
        return Sponge.getPluginManager()
                .getPlugin("sponge-api")
                .map(p -> p.getVersion().toString())
                .orElse("Unknown");
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T> Collection<ModContainer<T>> mods() {
        return Sponge.getPluginManager().getPlugins().stream()
                .map(pc -> (ModContainer<T>) this.toContainer(pc))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T> Optional<ModContainer<T>> mod(@NonNull String modId) {
        return Sponge.getPluginManager()
                .getPlugin(modId)
                .map(pc -> (ModContainer<T>) this.toContainer(pc));
    }

    @Override
    public @NonNull Logger logger(final @NonNull String modId) {
        return new Slf4jLogger(modId);
    }

    @Override
    public boolean isModLoaded(final @NonNull String... modId) {
        for (final String id : modId) {
            if (Sponge.getPluginManager().isLoaded(id)
                    || Sponge.getPluginManager().isLoaded(id.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean areModsLoaded(final @NonNull String... modId) {
        for (final String id : modId) {
            if (!Sponge.getPluginManager().isLoaded(id)
                    && !Sponge.getPluginManager().isLoaded(id.toLowerCase(Locale.ROOT))) {
                return false;
            }
        }
        return true;
    }

    private @NonNull ModContainer<PluginContainer> toContainer(
            final @NonNull PluginContainer container) {
        return new ModContainerImpl<>(
                container,
                new ModInfoImpl(
                        container.getId(),
                        container.getName(),
                        container.getVersion().orElse("Unknown"),
                        Platforms.SPONGE),
                new ModResourceImpl(
                        () ->
                                container
                                        .getSource()
                                        .orElseThrow(
                                                () ->
                                                        new IllegalStateException(
                                                                "Plugin source not available"))));
    }
}
