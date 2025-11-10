/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta;

import dev.neuralnexus.taterapi.Wrapped;
import dev.neuralnexus.taterapi.meta.version.VersionComparable;

public interface ModContainer<T> extends VersionComparable<ModContainer<T>>, Wrapped<T> {
    /**
     * Get the mod info
     *
     * @return The mod info
     */
    ModInfo info();

    /**
     * Get the mod's jar and resources
     *
     * @return The mod's jar and resources
     */
    ModResource resource();

    /**
     * Get the mod id
     *
     * @return The mod id
     */
    default String id() {
        return this.info().id();
    }

    /**
     * Get the mod name
     *
     * @return The mod name
     */
    default String name() {
        return this.info().name();
    }

    /**
     * Get the mod version
     *
     * @return The mod version
     */
    default String version() {
        return this.info().version();
    }

    /**
     * Get the mod's platform
     *
     * @return The mod's platform
     */
    default Platform platform() {
        return this.info().platform();
    }
}
