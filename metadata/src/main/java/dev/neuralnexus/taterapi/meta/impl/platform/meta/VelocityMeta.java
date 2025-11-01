/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta;

import static dev.neuralnexus.taterapi.util.PathUtils.getPluginsFolder;

import com.google.inject.Inject;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.ProxyServer;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.logger.impl.Slf4jLogger;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.ModInfo;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.Side;

import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.List;
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

    @Override
    public @NonNull List<ModInfo> mods() {
        return proxyServer.getPluginManager().getPlugins().stream()
                .map(
                        plugin ->
                                new ModInfoImpl(
                                        plugin.getDescription().getId(),
                                        plugin.getDescription().getName().orElse("Unknown"),
                                        plugin.getDescription().getVersion().orElse("Unknown"),
                                        Platforms.VELOCITY))
                .collect(Collectors.toList());
    }

    @Override
    public @NonNull Logger logger(@NonNull String modId) {
        return new Slf4jLogger(modId);
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
