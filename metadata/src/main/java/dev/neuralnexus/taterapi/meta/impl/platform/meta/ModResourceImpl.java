/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta;

import dev.neuralnexus.taterapi.meta.ModResource;

import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.function.Supplier;

public class ModResourceImpl implements ModResource {
    private final Supplier<Path> pathSupplier;
    private FileSystem fileSystem;

    public ModResourceImpl(final Supplier<Path> pathSupplier) {
        this.pathSupplier = pathSupplier;
    }

    @Override
    public @NonNull Path path() {
        return this.pathSupplier.get();
    }

    @Override
    public @NonNull FileSystem fileSystem() throws IOException {
        if (this.fileSystem == null || !this.fileSystem.isOpen()) {
            URI uri = URI.create("jar:" + this.path().toUri() + "!/");
            this.fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
        }
        return this.fileSystem;
    }
}
