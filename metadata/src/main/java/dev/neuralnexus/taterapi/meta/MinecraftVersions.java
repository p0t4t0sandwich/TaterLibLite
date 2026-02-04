/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta;

import dev.neuralnexus.taterapi.meta.impl.version.MinecraftVersionImpl;
import dev.neuralnexus.taterapi.meta.version.Alpha;
import dev.neuralnexus.taterapi.meta.version.Beta;
import dev.neuralnexus.taterapi.meta.version.Calver;
import dev.neuralnexus.taterapi.meta.version.Classic;
import dev.neuralnexus.taterapi.meta.version.Indev;
import dev.neuralnexus.taterapi.meta.version.Infdev;
import dev.neuralnexus.taterapi.meta.version.PreClassic;
import dev.neuralnexus.taterapi.meta.version.Release;

public final class MinecraftVersions
        implements PreClassic, Classic, Indev, Infdev, Alpha, Beta, Release, Calver {
    public static final MinecraftVersion UNKNOWN = MinecraftVersionImpl.of("unknown");

    /**
     * Create a MinecraftVersion from a string.
     *
     * @param version The version to create
     * @return The MinecraftVersion
     */
    static MinecraftVersion of(String version) {
        if (version.contains("(MC: 1.7.3)")) {
            return B1_7_3;
        }
        return MinecraftVersionImpl.of(version);
    }
}
