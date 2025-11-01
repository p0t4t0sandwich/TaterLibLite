/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta;

import static dev.neuralnexus.taterapi.util.PathUtils.getPluginsFolder;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.logger.impl.JavaLogger;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.ModInfo;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.Side;

import net.md_5.bungee.api.ProxyServer;

import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.List;
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

    @Override
    public @NonNull List<ModInfo> mods() {
        return ProxyServer.getInstance().getPluginManager().getPlugins().stream()
                .map(
                        plugin ->
                                new ModInfoImpl(
                                        plugin.getDescription().getName(),
                                        plugin.getDescription().getName(),
                                        plugin.getDescription().getVersion(),
                                        Platforms.BUNGEECORD))
                .collect(Collectors.toList());
    }

    @Override
    public @NonNull Logger logger(@NonNull String modId) {
        return new JavaLogger(modId, ProxyServer.getInstance().getLogger());
    }

    @Override
    public @NonNull Path modsFolder() {
        return getPluginsFolder();
    }

    @Override
    public @NonNull Path configFolder() {
        return getPluginsFolder();
    }
}
