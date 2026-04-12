/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.logger.impl.Slf4jLogger;
import dev.neuralnexus.taterapi.meta.ModContainer;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.Side;
import dev.neuralnexus.taterapi.meta.impl.WMinecraft;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.neoforge.NeoForgeData;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforgespi.language.IModInfo;

import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/** Stores data about the NeoForge platform */
public final class NeoForgeMeta implements Platform.Meta {
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
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public @NonNull Side side() {
        return WMinecraft.determineSide(this.isClient());
    }

    @Override
    public boolean isClient() {
        return NeoForgeData.dist().isClient();
    }

    @Override
    public @NonNull String loaderVersion() {
        return NeoForgeData.versionInfo().fmlVersion();
    }

    @Override
    public @NonNull String apiVersion() {
        return NeoForgeData.versionInfo().neoForgeVersion();
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T> Collection<ModContainer<T>> mods() {
        if (ModList.get() != null) {
            return ModList.get().getSortedMods().stream()
                    .map(mc -> (ModContainer<T>) this.toContainer(mc))
                    .collect(Collectors.toList());
        } else {
            return LoadingModList.get().getMods().stream()
                    .map(info -> (ModContainer<T>) this.toContainer(info))
                    .collect(Collectors.toList());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T> Optional<ModContainer<T>> mod(final @NonNull String modId) {
        if (ModList.get() != null) {
            Optional<? extends net.neoforged.fml.ModContainer> container =
                    ModList.get().getModContainerById(modId);
            if (container.isEmpty()) {
                container = ModList.get().getModContainerById(modId.toLowerCase(Locale.ROOT));
            }
            return container.map(mc -> (ModContainer<T>) this.toContainer(mc));
        } else {
            return LoadingModList.get().getMods().stream()
                    .filter(
                            m ->
                                    m.getModId().equals(modId)
                                            || m.getModId().equals(modId.toLowerCase(Locale.ROOT)))
                    .findFirst()
                    .map(i -> (ModContainer<T>) this.toContainer(i));
        }
    }

    @Override
    public @NonNull Logger logger(final @NonNull String modId) {
        return new Slf4jLogger(modId);
    }

    @Override
    public boolean isModLoaded(final @NonNull String... modId) {
        for (final String id : modId) {
            if (ModList.get() != null) {
                if (ModList.get().isLoaded(id)
                        || ModList.get().isLoaded(id.toLowerCase(Locale.ROOT))) {
                    return true;
                }
            } else {
                if (LoadingModList.get().getModFileById(id) != null
                        || LoadingModList.get().getModFileById(id.toLowerCase(Locale.ROOT))
                                != null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean areModsLoaded(final @NonNull String... modId) {
        for (final String id : modId) {
            if (ModList.get() != null) {
                if (!ModList.get().isLoaded(id)
                        && !ModList.get().isLoaded(id.toLowerCase(Locale.ROOT))) {
                    return false;
                }
            } else {
                if (LoadingModList.get().getModFileById(id) == null
                        && LoadingModList.get().getModFileById(id.toLowerCase(Locale.ROOT))
                                == null) {
                    return false;
                }
            }
        }
        return true;
    }

    private @NonNull ModContainer<net.neoforged.fml.ModContainer> toContainer(
            final net.neoforged.fml.@NonNull ModContainer container) {
        return new ModContainerImpl<>(
                container,
                new ModInfoImpl(
                        container.getModId(),
                        container.getModInfo().getDisplayName(),
                        container.getModInfo().getVersion().toString(),
                        Platforms.NEOFORGE),
                new ModResourceImpl(
                        () -> container.getModInfo().getOwningFile().getFile().getFilePath()));
    }

    private @NonNull ModContainer<IModInfo> toContainer(final IModInfo info) {
        return new ModContainerImpl<>(
                info,
                new ModInfoImpl(
                        info.getModId(),
                        info.getDisplayName(),
                        info.getVersion().toString(),
                        Platforms.NEOFORGE),
                new ModResourceImpl(() -> info.getOwningFile().getFile().getFilePath()));
    }
}
