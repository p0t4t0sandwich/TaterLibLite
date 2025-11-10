/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta;

import static dev.neuralnexus.taterapi.util.PathUtils.getPluginsFolder;

import com.google.inject.Inject;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.logger.impl.Slf4jLogger;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.ModContainer;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.Side;

import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/** Stores data about the Velocity platform. */
public final class VelocityMeta implements Platform.Meta {
    @Inject private ProxyServer proxyServer;

    @Override
    public @NonNull Object server() {
        return proxyServer;
    }

    @Override
    public @NonNull Object client() {
        throw new UnsupportedOperationException("Velocity does not run on the client");
    }

    @Override
    public @NonNull Object minecraft() {
        throw new UnsupportedOperationException("Velocity does not have a MinecraftServer");
    }

    @Override
    public @NonNull Side side() {
        return Side.PROXY;
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public @NonNull MinecraftVersion minecraftVersion() {
        return MinecraftVersion.of(ProtocolVersion.MAXIMUM_VERSION.toString());
    }

    @Override
    public @NonNull String loaderVersion() {
        return proxyServer.getVersion().getVersion();
    }

    @Override
    public @NonNull String apiVersion() {
        return proxyServer.getVersion().getVersion();
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T> Collection<ModContainer<T>> mods() {
        return proxyServer.getPluginManager().getPlugins().stream()
                .map(p -> (ModContainer<T>) this.toContainer(p))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T> Optional<ModContainer<T>> mod(final @NonNull String modId) {
        Optional<PluginContainer> plugin = proxyServer.getPluginManager().getPlugin(modId);
        if (plugin.isEmpty()) {
            plugin = proxyServer.getPluginManager().getPlugin(modId.toLowerCase(Locale.ROOT));
        }
        return plugin.map(p -> (ModContainer<T>) this.toContainer(p));
    }

    @Override
    public @NonNull Logger logger(final @NonNull String modId) {
        return new Slf4jLogger(modId);
    }

    @Override
    public boolean isModLoaded(final @NonNull String... modId) {
        for (final String id : modId) {
            if (proxyServer.getPluginManager().isLoaded(id)
                    || proxyServer.getPluginManager().isLoaded(id.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean areModsLoaded(final @NonNull String... modId) {
        for (final String id : modId) {
            if (!proxyServer.getPluginManager().isLoaded(id)
                    && !proxyServer.getPluginManager().isLoaded(id.toLowerCase(Locale.ROOT))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NonNull Path modsFolder() {
        return getPluginsFolder();
    }

    @Override
    public @NonNull Path configFolder() {
        return getPluginsFolder();
    }

    private @NonNull ModContainer<PluginContainer> toContainer(
            final @NonNull PluginContainer container) {
        return new ModContainerImpl<>(
                container,
                new ModInfoImpl(
                        container.getDescription().getId(),
                        container.getDescription().getName().orElse("Unknown"),
                        container.getDescription().getVersion().orElse("Unknown"),
                        Platforms.VELOCITY),
                new ModResourceImpl(
                        () ->
                                container
                                        .getDescription()
                                        .getSource()
                                        .orElseThrow(
                                                () ->
                                                        new IllegalStateException(
                                                                "Plugin source not available for "
                                                                        + container
                                                                                .getDescription()
                                                                                .getId()))));
    }
}
