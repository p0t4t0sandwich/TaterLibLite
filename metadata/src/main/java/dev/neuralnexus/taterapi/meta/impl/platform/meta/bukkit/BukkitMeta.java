/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta.bukkit;

import static dev.neuralnexus.taterapi.util.PathUtils.getPluginsFolder;
import static dev.neuralnexus.taterapi.util.ReflectionUtil.checkForMethod;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.logger.impl.JavaLogger;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.ModInfo;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.Side;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.ModInfoImpl;

import org.bukkit.Bukkit;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** Stores data about the Bukkit platform */
public final class BukkitMeta implements Platform.Meta {
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
    public @NonNull MinecraftVersion minecraftVersion() {
        if (MetaAPI.instance().isHybrid()) {
            return BukkitHybridMeta.minecraftVersion();
        }
        String version = Bukkit.getVersion();
        if (MetaAPI.instance().isPlatformPresent(Platforms.PAPER)
                && checkForMethod("org.bukkit.Bukkit", "getMinecraftVersion")) {
            version = PaperMeta.getMinecraftVersion();
        }
        return MinecraftVersion.of(version);
    }

    @Override
    public @NonNull String loaderVersion() {
        return Bukkit.getBukkitVersion();
    }

    @Override
    public @NonNull String apiVersion() {
        return Bukkit.getBukkitVersion();
    }

    @Override
    public @NonNull List<ModInfo> mods() {
        return Arrays.stream(Bukkit.getServer().getPluginManager().getPlugins())
                .map(
                        plugin ->
                                new ModInfoImpl(
                                        plugin.getDescription().getName(),
                                        plugin.getDescription().getName(),
                                        plugin.getDescription().getVersion(),
                                        Platforms.BUKKIT))
                .collect(Collectors.toList());
    }

    @Override
    public @NonNull Logger logger(@NonNull String modId) {
        if (MetaAPI.instance().isHybrid()) {
            return BukkitHybridMeta.logger(modId);
        }
        return new JavaLogger(modId, Bukkit.getLogger());
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
