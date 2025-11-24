/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta;

import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/** Represents a mod's jar and resources */
public interface ModResource {
    /**
     * Get the path to the mod's file
     *
     * @return The path to the mod's file
     */
    @NonNull Path path();

    /**
     * Get the mod's file as a File object
     *
     * @return The mod's file as a File object
     */
    default @NonNull File asFile() {
        return this.path().toFile();
    }

    /**
     * Get the mod's file as a URI
     *
     * @return The mod's file as a URI
     */
    default @NonNull URI asUri() {
        return this.path().toUri();
    }

    /**
     * Get the FileSystem of the mod's jar file
     *
     * @return The FileSystem of the mod's jar file
     */
    @NonNull FileSystem fileSystem() throws IOException;

    /**
     * Get a resource from the mod's jar file
     *
     * @param path The path to the resource
     * @return An Optional containing the Path to the resource, or empty if not found
     */
    default Optional<Path> getResource(final @NonNull String path) {
        Objects.requireNonNull(path, "path cannot be null");
        try {
            return Optional.of(this.fileSystem().getPath(path));
        } catch (IOException ignored) {
        }
        return Optional.empty();
    }

    /**
     * Get a resource from the mod's jar file, throwing an exception if not found
     *
     * @param path The path to the resource
     * @return The Path to the resource
     * @throws RuntimeException If the resource is not found or an error occurs
     */
    default @NonNull Path getResourceOrThrow(final @NonNull String path) throws RuntimeException {
        Objects.requireNonNull(path, "path cannot be null");
        try {
            return this.fileSystem().getPath(path);
        } catch (IOException e) {
            throw new RuntimeException("Error accessing resource: " + path, e);
        }
    }
}
