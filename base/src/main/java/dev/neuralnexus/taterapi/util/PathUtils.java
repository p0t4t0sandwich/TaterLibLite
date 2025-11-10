/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.util;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/** Utility class for path operations */
public final class PathUtils {
    /** Get current working directory. */
    public static Path getCurrentWorkingDirectory() {
        return Paths.get("." + File.separator).toAbsolutePath().normalize();
    }

    /** Get the path to the mods folder. */
    public static Path getModsFolder() {
        return getCurrentWorkingDirectory().resolve("mods");
    }

    /** Get the path to the config folder. */
    public static Path getConfigFolder() {
        return getCurrentWorkingDirectory().resolve("config");
    }

    /** Get the path to the plugins folder. */
    public static Path getPluginsFolder() {
        return getCurrentWorkingDirectory().resolve("plugins");
    }

    /** Get the path of the JAR file containing the specified class. */
    public static @NotNull Path getPathFromClass(final @NotNull Class<?> cls) {
        try {
            String path =
                    URLDecoder.decode(
                            cls.getProtectionDomain().getCodeSource().getLocation().getPath(),
                            StandardCharsets.UTF_8);
            if (path.contains(".jar!/")) {
                path = path.substring(0, path.indexOf(".jar!/") + 4);
            } else if (!path.endsWith(".jar")) {
                throw new UnsupportedOperationException(
                        "Class " + cls.getName() + " is not loaded from a JAR, but from: " + path);
            }
            if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows")
                    && path.startsWith("/")) {
                path = path.substring(1);
            }
            return Paths.get(path).toAbsolutePath().normalize();
        } catch (Exception e) {
            throw new UnsupportedOperationException(
                    "Unable to determine JAR path for class: " + cls.getName(), e);
        }
    }
}
