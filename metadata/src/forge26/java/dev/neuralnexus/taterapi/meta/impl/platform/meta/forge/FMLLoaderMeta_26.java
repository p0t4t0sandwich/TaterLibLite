/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta.forge;

import dev.neuralnexus.taterapi.meta.ModContainer;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Side;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.server.ServerLifecycleHooks;

import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/** Stores data about the FMLLoader platform */
final class FMLLoaderMeta_26 extends FMLLoaderMeta implements Platform.Meta {
    @Override
    public @NonNull Object server() {
        if (this.side().isServer()) {
            return this.minecraft();
        }
        return this.client();
    }

    @Override
    public @NonNull Object client() {
        return Minecraft.getInstance();
    }

    @SuppressWarnings({"DataFlowIssue", "UnstableApiUsage"})
    @Override
    public @NonNull Object minecraft() {
        if (this.side().isClient() && Minecraft.getInstance().hasSingleplayerServer()) {
            return Minecraft.getInstance().getSingleplayerServer();
        }
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public @NonNull Side side() {
        Side side = Side.SERVER;
        if (this.isClient()) {
            if (Minecraft.getInstance().hasSingleplayerServer()) {
                side = Side.INTEGRATED;
            } else {
                side = Side.CLIENT;
            }
        }
        return side;
    }

    @SuppressWarnings({"unchecked", "UnstableApiUsage"})
    @Override
    public @NonNull <T> Collection<ModContainer<T>> mods() {
        if (ModList.getLoadedMods() != null) {
            return ModList.getLoadedMods().stream()
                    .map(mc -> (ModContainer<T>) this.toContainer(mc))
                    .collect(Collectors.toList());
        } else {
            return LoadingModList.getMods().stream()
                    .map(info -> (ModContainer<T>) this.toContainer(info))
                    .collect(Collectors.toList());
        }
    }

    @SuppressWarnings({"unchecked", "UnstableApiUsage"})
    @Override
    public @NonNull <T> Optional<ModContainer<T>> mod(final @NonNull String modId) {
        if (ModList.getLoadedMods() != null) {
            Optional<? extends net.minecraftforge.fml.ModContainer> container =
                    ModList.getModContainerById(modId);
            if (container.isPresent()) {
                return Optional.of((ModContainer<T>) this.toContainer(container.get()));
            }
            container = ModList.getModContainerById(modId.toLowerCase(Locale.ROOT));
            if (container.isPresent()) {
                return Optional.of((ModContainer<T>) this.toContainer(container.get()));
            }
        } else {
            return LoadingModList.getMods().stream()
                    .filter(
                            m ->
                                    m.getModId().equals(modId)
                                            || m.getModId().equals(modId.toLowerCase(Locale.ROOT)))
                    .findFirst()
                    .map(i -> (ModContainer<T>) this.toContainer(i));
        }
        return Optional.empty();
    }

    @Override
    public boolean isModLoaded(final @NonNull String... modId) {
        for (final String id : modId) {
            if (ModList.getLoadedMods() != null) {
                if (ModList.isLoaded(id) || ModList.isLoaded(id.toLowerCase(Locale.ROOT))) {
                    return true;
                }
            } else {
                if (LoadingModList.getModFileById(id) != null
                        || LoadingModList.getModFileById(id.toLowerCase(Locale.ROOT)) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean areModsLoaded(final @NonNull String... modId) {
        for (final String id : modId) {
            if (ModList.getLoadedMods() != null) {
                if (!ModList.isLoaded(id) && !ModList.isLoaded(id.toLowerCase(Locale.ROOT))) {
                    return false;
                }
            } else {
                if (LoadingModList.getModFileById(id) == null
                        && LoadingModList.getModFileById(id.toLowerCase(Locale.ROOT)) == null) {
                    return false;
                }
            }
        }
        return true;
    }
}
