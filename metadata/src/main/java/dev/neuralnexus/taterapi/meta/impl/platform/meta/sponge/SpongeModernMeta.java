/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta.sponge;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.logger.impl.ApacheLogger;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.ModInfo;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.Side;
import dev.neuralnexus.taterapi.meta.impl.WMinecraft;
import dev.neuralnexus.taterapi.meta.impl.WMinecraftServer;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.ModInfoImpl;

import org.jspecify.annotations.NonNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.plugin.PluginContainer;

import java.util.List;
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
    public @NonNull MinecraftVersion minecraftVersion() {
        return MinecraftVersion.of(Sponge.platform().minecraftVersion().name());
    }

    @Override
    public @NonNull String loaderVersion() {
        Optional<PluginContainer> container = Sponge.pluginManager().plugin("sponge");
        if (container.isPresent()) {
            return container.get().metadata().version().toString();
        } else {
            return "Unknown";
        }
    }

    @Override
    public @NonNull String apiVersion() {
        Optional<PluginContainer> container = Sponge.pluginManager().plugin("sponge-api");
        if (container.isPresent()) {
            return container.get().metadata().version().toString();
        } else {
            return "Unknown";
        }
    }

    @Override
    public @NonNull List<ModInfo> mods() {
        return Sponge.pluginManager().plugins().stream()
                .map(
                        pluginContainer ->
                                new ModInfoImpl(
                                        pluginContainer.metadata().id(),
                                        pluginContainer.metadata().name().orElse(""),
                                        pluginContainer.metadata().version().toString(),
                                        Platforms.SPONGE))
                .collect(Collectors.toList());
    }

    @Override
    public @NonNull Logger logger(@NonNull String modId) {
        return new ApacheLogger(modId);
    }
}
