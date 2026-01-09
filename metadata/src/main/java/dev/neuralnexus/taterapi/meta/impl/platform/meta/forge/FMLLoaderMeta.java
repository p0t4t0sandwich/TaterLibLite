/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta.forge;

import static dev.neuralnexus.taterapi.util.ReflectionUtil.checkForClass;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.logger.impl.ApacheLogger;
import dev.neuralnexus.taterapi.logger.impl.Slf4jLogger;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.meta.ModContainer;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.Side;
import dev.neuralnexus.taterapi.meta.impl.WMinecraft;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.ModContainerImpl;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.ModInfoImpl;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.ModResourceImpl;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LauncherVersion;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;

import org.jspecify.annotations.NonNull;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/** Stores data about the FMLLoader platform */
final class FMLLoaderMeta implements Platform.Meta {
    private final boolean oldLifeCycleHooks =
            checkForClass("net.minecraftforge.fml.server.ServerLifecycleHooks");

    private static Field modsField;

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
        if (this.oldLifeCycleHooks) {
            return net.minecraftforge.fml.server.ServerLifecycleHooks.getCurrentServer();
        }
        return net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public @NonNull Side side() {
        return WMinecraft.determineSide(this.isClient());
    }

    @Override
    public boolean isClient() {
        return FMLLoader.getDist().isClient();
    }

    @Override
    public @NonNull String loaderVersion() {
        return LauncherVersion.getVersion();
    }

    @Override
    public @NonNull String apiVersion() {
        if (MetaAPI.instance().version().lessThan(MinecraftVersions.V17)) {
            return ForgeVersion_13_16.forgeVersion();
        }
        return ForgeVersion_17_21.forgeVersion();
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T> Collection<ModContainer<T>> mods() {
        if (ModList.get() != null) {
            final MinecraftVersion version = MetaAPI.instance().version();
            if (version.isInRange(MinecraftVersions.V13, MinecraftVersions.V21_3)) {
                if (modsField == null) {
                    try {
                        modsField = ModList.class.getDeclaredField("mods");
                        modsField.setAccessible(true);
                    } catch (final NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    if (modsField != null) {
                        return ((List<net.minecraftforge.fml.ModContainer>)
                                        modsField.get(ModList.get()))
                                .stream()
                                        .map(mc -> (ModContainer<T>) this.toContainer(mc))
                                        .collect(Collectors.toList());
                    }
                } catch (final IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            return ModList.get().getLoadedMods().stream()
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
            Optional<? extends net.minecraftforge.fml.ModContainer> container =
                    ModList.get().getModContainerById(modId);
            if (container.isPresent()) {
                return Optional.of((ModContainer<T>) this.toContainer(container.get()));
            }
            container = ModList.get().getModContainerById(modId.toLowerCase(Locale.ROOT));
            if (container.isPresent()) {
                return Optional.of((ModContainer<T>) this.toContainer(container.get()));
            }
        } else {
            return LoadingModList.get().getMods().stream()
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
    public @NonNull Logger logger(final @NonNull String modId) {
        if (MetaAPI.instance().version().lessThan(MinecraftVersions.V18_2)) {
            return new ApacheLogger(modId);
        }
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

    private @NonNull ModContainer<net.minecraftforge.fml.ModContainer> toContainer(
            final net.minecraftforge.fml.@NonNull ModContainer container) {
        return new ModContainerImpl<>(
                container,
                new ModInfoImpl(
                        container.getModId(),
                        container.getModInfo().getDisplayName(),
                        container.getModInfo().getVersion().toString(),
                        Platforms.FORGE),
                new ModResourceImpl(
                        () -> {
                            if (MetaAPI.instance().version().noLessThan(MinecraftVersions.V17_1)) {
                                return container
                                        .getModInfo()
                                        .getOwningFile()
                                        .getFile()
                                        .getFilePath();
                            }
                            return ((ModFileInfo) container.getModInfo().getOwningFile())
                                    .getFile()
                                    .getFilePath();
                        }));
    }

    private @NonNull ModContainer<IModInfo> toContainer(final IModInfo info) {
        return new ModContainerImpl<>(
                info,
                new ModInfoImpl(
                        info.getModId(),
                        info.getDisplayName(),
                        info.getVersion().toString(),
                        Platforms.FORGE),
                new ModResourceImpl(
                        () -> {
                            if (MetaAPI.instance().version().noLessThan(MinecraftVersions.V17_1)) {
                                return info.getOwningFile().getFile().getFilePath();
                            }
                            return ((ModFileInfo) info.getOwningFile()).getFile().getFilePath();
                        }));
    }
}
