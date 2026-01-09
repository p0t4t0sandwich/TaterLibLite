/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.logger.impl.SystemLogger;
import dev.neuralnexus.taterapi.meta.ModContainer;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Side;
import dev.neuralnexus.taterapi.meta.impl.WMinecraft;
import dev.neuralnexus.taterapi.meta.impl.WMinecraftServer;
import dev.neuralnexus.taterapi.meta.impl.version.provider.VanillaMCVProvider;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/** Stores information about the vanilla platform */
public final class VanillaMeta implements Platform.Meta {
    private static Object server = null;

    /**
     * Set the server object
     *
     * @param server The server object
     */
    @ApiStatus.Internal
    public static void setServer(Object server) {
        VanillaMeta.server = server;
    }

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
        if (server == null) {
            throw new IllegalStateException("Server has not been set");
        }
        return server;
    }

    @Override
    public @NonNull Side side() {
        if (server == null) {
            return Side.CLIENT;
        }
        return WMinecraftServer.isDedicatedServer(server) ? Side.INTEGRATED : Side.SERVER;
    }

    @Override
    public boolean isClient() {
        return this.side().isClient();
    }

    @Override
    public @NonNull String loaderVersion() {
        return new VanillaMCVProvider().get().toString();
    }

    @Override
    public @NonNull String apiVersion() {
        return new VanillaMCVProvider().get().toString();
    }

    @Override
    public @NonNull <T> Collection<ModContainer<T>> mods() {
        return Collections.emptyList();
    }

    @Override
    public @NonNull <T> Optional<ModContainer<T>> mod(@NonNull String modId) {
        return Optional.empty();
    }

    @Override
    public @NonNull Logger logger(@NonNull String modId) {
        // TODO: Do some version parsing and grab the vanilla logger factory
        return new SystemLogger(modId);
    }

    @Override
    public boolean isModLoaded(@NonNull String... modId) {
        return false;
    }

    @Override
    public boolean areModsLoaded(@NonNull String... modId) {
        return false;
    }
}
