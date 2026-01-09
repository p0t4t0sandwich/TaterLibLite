/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta.bukkit;

import static dev.neuralnexus.taterapi.util.PathUtils.getPluginsFolder;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.logger.impl.JavaLogger;
import dev.neuralnexus.taterapi.meta.ModContainer;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.Side;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.ModContainerImpl;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.ModInfoImpl;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.ModResourceImpl;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/** Stores data about the Bukkit platform */
public final class BukkitMeta implements Platform.Meta {
    private static Field pluginFileField;

    static {
        try {
            pluginFileField = JavaPlugin.class.getDeclaredField("file");
            pluginFileField.setAccessible(true);
        } catch (final NoSuchFieldException ignored) {
        }
    }

    @Override
    public @NonNull Object server() {
        return Bukkit.getServer();
    }

    @Override
    public @NonNull Object client() {
        throw new UnsupportedOperationException("Bukkit does not run on the client");
    }

    // TODO: Cache and move to MethodHandles
    // Consider making a common utils subproject and add Bukkit reflection stuffs, as it's used in
    // TaterLib
    @Override
    public @NonNull Object minecraft() {
        try {
            String clazz = Bukkit.getServer().getClass().getPackage().getName() + ".CraftServer";
            Class<?> craftServer = Class.forName(clazz);
            return craftServer.getDeclaredMethod("getServer").invoke(Bukkit.getServer());
        } catch (ClassNotFoundException
                | InvocationTargetException
                | IllegalAccessException
                | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NonNull Side side() {
        return Side.SERVER;
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public @NonNull String loaderVersion() {
        return Bukkit.getBukkitVersion();
    }

    @Override
    public @NonNull String apiVersion() {
        return Bukkit.getBukkitVersion();
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T> Collection<ModContainer<T>> mods() {
        return Arrays.stream(Bukkit.getServer().getPluginManager().getPlugins())
                .map(p -> (ModContainer<T>) this.toContainer(p))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T> Optional<ModContainer<T>> mod(final @NonNull String modId) {
        @UnknownNullability Plugin plugin = Bukkit.getPluginManager().getPlugin(modId);
        if (plugin != null) {
            return Optional.of((ModContainer<T>) this.toContainer(plugin));
        }
        plugin = Bukkit.getPluginManager().getPlugin(modId.toLowerCase(Locale.ROOT));
        if (plugin != null) {
            return Optional.of((ModContainer<T>) this.toContainer(plugin));
        }
        return Optional.empty();
    }

    @Override
    public @NonNull Logger logger(final @NonNull String modId) {
        return new JavaLogger(modId, Bukkit.getLogger());
    }

    @Override
    public boolean isModLoaded(final @NonNull String... modId) {
        for (final String id : modId) {
            if (Bukkit.getPluginManager().getPlugin(id) != null
                    || Bukkit.getPluginManager().getPlugin(id.toLowerCase(Locale.ROOT)) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean areModsLoaded(final @NonNull String... modId) {
        for (final String id : modId) {
            if (Bukkit.getPluginManager().getPlugin(id) == null
                    && Bukkit.getPluginManager().getPlugin(id.toLowerCase(Locale.ROOT)) == null) {
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
                        Platforms.BUKKIT),
                new ModResourceImpl(
                        () -> {
                            try {
                                return ((File) pluginFileField.get(plugin)).toPath();
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }));
    }
}
