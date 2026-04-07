/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta.forge;

import dev.neuralnexus.taterapi.meta.ModContainer;
import dev.neuralnexus.taterapi.meta.Platform;

import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import org.jspecify.annotations.NonNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO: Replace with a proper source set
/** Stores data about the FMLLoader platform */
final class FMLLoaderMeta_26 extends FMLLoaderMeta implements Platform.Meta {
    private static MethodHandle getLoadedModsHandle;
    private static MethodHandle getModContainerByIdHandle;
    private static MethodHandle isLoadedHandle;

    @SuppressWarnings({"unchecked", "JavaReflectionMemberAccess"})
    private static List<net.minecraftforge.fml.ModContainer> getLoadedMods() {
        if (getLoadedModsHandle == null) {
            try {
                final Class<?> modListClass = Class.forName("net.minecraftforge.fml.ModList");
                final Method method = modListClass.getDeclaredMethod("getLoadedMods");
                getLoadedModsHandle = MethodHandles.publicLookup().unreflect(method);
            } catch (final ClassNotFoundException
                    | IllegalAccessException
                    | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return (List<net.minecraftforge.fml.ModContainer>) getLoadedModsHandle.invokeExact();
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"unchecked", "JavaReflectionMemberAccess"})
    private static Optional<? extends net.minecraftforge.fml.ModContainer> getModContainerById(
            final @NonNull String modId) {
        if (getModContainerByIdHandle == null) {
            try {
                final Class<?> modListClass = Class.forName("net.minecraftforge.fml.ModList");
                final Method method =
                        modListClass.getDeclaredMethod("getModContainerById", String.class);
                getModContainerByIdHandle = MethodHandles.publicLookup().unreflect(method);
            } catch (final ClassNotFoundException
                    | IllegalAccessException
                    | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return (Optional<? extends net.minecraftforge.fml.ModContainer>)
                    getModContainerByIdHandle.invokeExact(modId);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    private static boolean isLoaded(final @NonNull String modTarget) {
        if (isLoadedHandle == null) {
            try {
                final Class<?> modListClass = Class.forName("net.minecraftforge.fml.ModList");
                final Method method = modListClass.getDeclaredMethod("isLoaded", String.class);
                isLoadedHandle = MethodHandles.publicLookup().unreflect(method);
            } catch (final ClassNotFoundException
                    | IllegalAccessException
                    | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return (boolean) isLoadedHandle.invokeExact(modTarget);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static MethodHandle getModsHandle;
    private static MethodHandle getModFileByIdHandle;

    @SuppressWarnings("unchecked")
    private static List<ModInfo> getMods() {
        if (getModsHandle == null) {
            try {
                final Class<?> loadingModListClass =
                        Class.forName("net.minecraftforge.fml.loading.LoadingModList");
                final Method method = loadingModListClass.getDeclaredMethod("getMods");
                getModsHandle = MethodHandles.publicLookup().unreflect(method);
            } catch (final ClassNotFoundException
                    | IllegalAccessException
                    | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return (List<ModInfo>) getModsHandle.invokeExact();
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static ModFileInfo getModFileById(final @NonNull String modId) {
        if (getModFileByIdHandle == null) {
            try {
                final Class<?> loadingModListClass =
                        Class.forName("net.minecraftforge.fml.loading.LoadingModList");
                final Method method =
                        loadingModListClass.getDeclaredMethod("getModFileById", String.class);
                getModFileByIdHandle = MethodHandles.publicLookup().unreflect(method);
            } catch (final ClassNotFoundException
                    | IllegalAccessException
                    | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return (ModFileInfo) getModFileByIdHandle.invokeExact(modId);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T> Collection<ModContainer<T>> mods() {
        if (getLoadedMods() != null) {
            return getLoadedMods().stream()
                    .map(mc -> (ModContainer<T>) this.toContainer(mc))
                    .collect(Collectors.toList());
        } else {
            return getMods().stream()
                    .map(info -> (ModContainer<T>) this.toContainer(info))
                    .collect(Collectors.toList());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T> Optional<ModContainer<T>> mod(final @NonNull String modId) {
        if (getLoadedMods() != null) {
            Optional<? extends net.minecraftforge.fml.ModContainer> container =
                    getModContainerById(modId);
            if (container.isPresent()) {
                return Optional.of((ModContainer<T>) this.toContainer(container.get()));
            }
            container = getModContainerById(modId.toLowerCase(Locale.ROOT));
            if (container.isPresent()) {
                return Optional.of((ModContainer<T>) this.toContainer(container.get()));
            }
        } else {
            return getMods().stream()
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
            if (getLoadedMods() != null) {
                if (isLoaded(id) || isLoaded(id.toLowerCase(Locale.ROOT))) {
                    return true;
                }
            } else {
                if (getModFileById(id) != null
                        || getModFileById(id.toLowerCase(Locale.ROOT)) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean areModsLoaded(final @NonNull String... modId) {
        for (final String id : modId) {
            if (getLoadedMods() != null) {
                if (!isLoaded(id) && !isLoaded(id.toLowerCase(Locale.ROOT))) {
                    return false;
                }
            } else {
                if (getModFileById(id) == null
                        && getModFileById(id.toLowerCase(Locale.ROOT)) == null) {
                    return false;
                }
            }
        }
        return true;
    }
}
