/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta;

import dev.neuralnexus.taterapi.meta.version.VersionComparable;

public interface ModInfo extends VersionComparable<ModInfo> {
    /**
     * Get the mod id
     *
     * @return The mod id
     */
    String id();

    /**
     * Get the mod name
     *
     * @return The mod name
     */
    String name();

    /**
     * Get the mod version
     *
     * @return The mod version
     */
    String version();

    /**
     * Get the mod's platform
     *
     * @return The mod's platform
     */
    Platform platform();
}
