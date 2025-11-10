/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta;

import dev.neuralnexus.taterapi.meta.ModContainer;
import dev.neuralnexus.taterapi.meta.ModInfo;
import dev.neuralnexus.taterapi.meta.ModResource;

import org.jspecify.annotations.NonNull;

public record ModContainerImpl<T>(T unwrap, ModInfo info, ModResource resource)
        implements ModContainer<T> {
    @Override
    public boolean parseRange(final @NonNull String rangeString) {
        return this.info.parseRange(rangeString);
    }
}
