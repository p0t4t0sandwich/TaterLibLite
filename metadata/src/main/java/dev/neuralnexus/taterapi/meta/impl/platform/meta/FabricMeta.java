/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.logger.impl.ApacheLogger;
import dev.neuralnexus.taterapi.logger.impl.Slf4jLogger;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.meta.ModContainer;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.Side;
import dev.neuralnexus.taterapi.meta.impl.WMinecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/** Stores data about the Fabric platform */
public final class FabricMeta implements Platform.Meta {
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
    @SuppressWarnings("deprecation")
    public @NonNull Object minecraft() {
        if (this.side().isClient() && WMinecraft.hasServer()) {
            return WMinecraft.getServer();
        }
        return FabricLoader.getInstance().getGameInstance();
    }

    @Override
    public @NonNull Side side() {
        return WMinecraft.determineSide(this.isClient());
    }

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public @NonNull MinecraftVersion minecraftVersion() {
        return MinecraftVersion.of(
                FabricLoader.getInstance()
                        .getModContainer("minecraft")
                        .get()
                        .getMetadata()
                        .getVersion()
                        .getFriendlyString());
    }

    @Override
    public @NonNull String loaderVersion() {
        Optional<net.fabricmc.loader.api.ModContainer> container =
                FabricLoader.getInstance().getModContainer("fabric-loader");
        if (container.isPresent()) {
            return container.get().getMetadata().getVersion().getFriendlyString();
        } else {
            return "Unknown";
        }
    }

    @Override
    public @NonNull String apiVersion() {
        Optional<net.fabricmc.loader.api.ModContainer> container =
                FabricLoader.getInstance().getModContainer("fabric-api-base");
        if (container.isPresent()) {
            return container.get().getMetadata().getVersion().getFriendlyString();
        } else {
            return "Unknown";
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T> Collection<ModContainer<T>> mods() {
        return FabricLoader.getInstance().getAllMods().stream()
                .map(mc -> (ModContainer<T>) this.toContainer(mc))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T> Optional<ModContainer<T>> mod(final @NonNull String modId) {
        return FabricLoader.getInstance()
                .getModContainer(modId)
                .map(mc -> (ModContainer<T>) this.toContainer(mc));
    }

    @Override
    public @NonNull Logger logger(final @NonNull String modId) {
        final MinecraftVersion version = this.minecraftVersion();
        if (version.isOlderThan(MinecraftVersions.V18)) {
            return new ApacheLogger(modId);
        }
        return new Slf4jLogger(modId);
    }

    @Override
    public boolean isModLoaded(final @NonNull String... modId) {
        for (final String id : modId) {
            if (FabricLoader.getInstance().isModLoaded(id)
                    || FabricLoader.getInstance().isModLoaded(id.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean areModsLoaded(final @NonNull String... modId) {
        for (final String id : modId) {
            if (!FabricLoader.getInstance().isModLoaded(id)
                    && !FabricLoader.getInstance().isModLoaded(id.toLowerCase(Locale.ROOT))) {
                return false;
            }
        }
        return true;
    }

    private @NonNull ModContainer<net.fabricmc.loader.api.ModContainer> toContainer(
            @NotNull net.fabricmc.loader.api.ModContainer container) {
        return new ModContainerImpl<>(
                container,
                new ModInfoImpl(
                        container.getMetadata().getId(),
                        container.getMetadata().getName(),
                        container.getMetadata().getVersion().getFriendlyString(),
                        Platforms.FABRIC),
                new ModResourceImpl(() -> container.getRootPaths().getFirst()));
    }
}
