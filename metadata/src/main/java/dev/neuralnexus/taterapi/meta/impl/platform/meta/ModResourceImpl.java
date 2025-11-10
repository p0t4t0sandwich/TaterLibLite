/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta;

import dev.neuralnexus.taterapi.meta.ModResource;

import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.util.function.Supplier;

public class ModResourceImpl implements ModResource {
    private final Supplier<Path> pathSupplier;

    public ModResourceImpl(final Supplier<Path> pathSupplier) {
        this.pathSupplier = pathSupplier;
    }

    @Override
    public @NonNull Path path() {
        return this.pathSupplier.get();
    }
}
