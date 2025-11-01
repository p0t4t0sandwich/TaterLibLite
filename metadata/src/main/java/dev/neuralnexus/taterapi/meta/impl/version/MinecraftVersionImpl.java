/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.version;

import static dev.neuralnexus.taterapi.util.FlexVerComparator.compare;

import dev.neuralnexus.taterapi.meta.MinecraftVersion;

import org.jspecify.annotations.NonNull;

/** Implementation of {@link MinecraftVersion} */
public record MinecraftVersionImpl(String version) implements MinecraftVersion {
    public static MinecraftVersionImpl of(String version) {
        return new MinecraftVersionImpl(version);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MinecraftVersion that = (MinecraftVersion) obj;
        return compare(this.version(), that.version()) == 0;
    }

    @Override
    public @NonNull String toString() {
        return this.version;
    }
}
