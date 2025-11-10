/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta;

import static dev.neuralnexus.taterapi.util.PathUtils.getPluginsFolder;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.logger.impl.JavaLogger;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.ModContainer;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.Side;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/** Stores data about the BungeeCord platform */
public final class BungeeCordMeta implements Platform.Meta {
    @Override
    public @NonNull Object server() {
        return ProxyServer.getInstance();
    }

    @Override
    public @NonNull Object client() {
        throw new UnsupportedOperationException("BungeeCord does not run on the client");
    }

    @Override
    public @NonNull Object minecraft() {
        throw new UnsupportedOperationException("BungeeCord does not have a MinecraftServer");
    }

    @Override
    public @NonNull Side side() {
        return Side.PROXY;
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NonNull MinecraftVersion minecraftVersion() {
        return MinecraftVersion.of(ProxyServer.getInstance().getGameVersion());
    }

    @Override
    public @NonNull String loaderVersion() {
        return ProxyServer.getInstance().getVersion();
    }

    @Override
    public @NonNull String apiVersion() {
        return ProxyServer.getInstance().getVersion();
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T> Collection<ModContainer<T>> mods() {
        return ProxyServer.getInstance().getPluginManager().getPlugins().stream()
                .map(p -> (ModContainer<T>) this.toContainer(p))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T> Optional<ModContainer<T>> mod(final @NonNull String modId) {
        @UnknownNullability
        Plugin plugin = ProxyServer.getInstance().getPluginManager().getPlugin("pluginName");
        if (plugin != null) {
            return Optional.of((ModContainer<T>) this.toContainer(plugin));
        }
        return Optional.empty();
    }

    @Override
    public @NonNull Logger logger(final @NonNull String modId) {
        return new JavaLogger(modId, ProxyServer.getInstance().getLogger());
    }

    @Override
    public boolean isModLoaded(final @NonNull String... modId) {
        for (final String id : modId) {
            if (ProxyServer.getInstance().getPluginManager().getPlugin(id) != null
                    || ProxyServer.getInstance()
                                    .getPluginManager()
                                    .getPlugin(id.toLowerCase(Locale.ROOT))
                            != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean areModsLoaded(final @NonNull String... modId) {
        for (final String id : modId) {
            if (ProxyServer.getInstance().getPluginManager().getPlugin(id) == null
                    && ProxyServer.getInstance()
                                    .getPluginManager()
                                    .getPlugin(id.toLowerCase(Locale.ROOT))
                            == null) {
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

    private @NonNull ModContainer<Plugin> toContainer(final @NonNull Plugin plugin) {
        return new ModContainerImpl<>(
                plugin,
                new ModInfoImpl(
                        plugin.getDescription().getName(),
                        plugin.getDescription().getName(),
                        plugin.getDescription().getVersion(),
                        Platforms.BUNGEECORD),
                new ModResourceImpl(() -> plugin.getFile().toPath()));
    }
}
