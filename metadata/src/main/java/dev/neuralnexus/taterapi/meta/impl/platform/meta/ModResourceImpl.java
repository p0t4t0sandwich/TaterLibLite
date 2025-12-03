/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta;

import dev.neuralnexus.taterapi.meta.ModResource;

import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

public class ModResourceImpl implements ModResource {
    private final @NonNull Supplier<@NonNull Path> pathSupplier;
    private FileSystem fileSystem;
    private int refCount = 0;

    public ModResourceImpl(final @NonNull Supplier<@NonNull Path> pathSupplier) {
        this.pathSupplier = pathSupplier;
    }

    @Override
    public @NonNull Path path() {
        return this.pathSupplier.get();
    }

    @Override
    public @NonNull FileSystem fileSystem() throws IOException {
        if (this.fileSystem != null && this.fileSystem.isOpen()) {
            this.refCount++;
            return this.fileSystem;
        }

        URI uri = URI.create("jar:" + this.path().toUri() + "!/");
        try {
            this.fileSystem = new FSWrapper(FileSystems.newFileSystem(uri, Collections.emptyMap()));
        } catch (FileSystemAlreadyExistsException e) {
            this.fileSystem = FileSystems.getFileSystem(uri);
        }

        this.refCount = 1;
        return this.fileSystem;
    }

    @Override
    public void close() throws Exception {
        this.refCount--;
        if (this.refCount <= 0
                && this.fileSystem != null
                && this.fileSystem.isOpen()
                && this.fileSystem instanceof FSWrapper fsWrapper) {
            fsWrapper.closeDelegate();
        }
    }

    private static final class FSWrapper extends FileSystem {
        private final FileSystem delegate;

        public FSWrapper(final FileSystem delegate) {
            this.delegate = delegate;
        }

        @Override
        public FileSystemProvider provider() {
            return this.delegate.provider();
        }

        @SuppressWarnings("RedundantThrows")
        @Override
        public void close() throws IOException {
            throw new UnsupportedOperationException(
                    "This FileSystem must not be closed directly, please use the ModResource#close() method.");
        }

        @Override
        public boolean isOpen() {
            return this.delegate.isOpen();
        }

        @Override
        public boolean isReadOnly() {
            return this.delegate.isReadOnly();
        }

        @Override
        public String getSeparator() {
            return this.delegate.getSeparator();
        }

        @Override
        public Iterable<Path> getRootDirectories() {
            return this.delegate.getRootDirectories();
        }

        @Override
        public Iterable<FileStore> getFileStores() {
            return this.delegate.getFileStores();
        }

        @Override
        public Set<String> supportedFileAttributeViews() {
            return this.delegate.supportedFileAttributeViews();
        }

        @Override
        public @NonNull Path getPath(@NonNull String first, String @NonNull ... more) {
            return this.delegate.getPath(first, more);
        }

        @Override
        public PathMatcher getPathMatcher(String syntaxAndPattern) {
            return this.delegate.getPathMatcher(syntaxAndPattern);
        }

        @Override
        public UserPrincipalLookupService getUserPrincipalLookupService() {
            return this.delegate.getUserPrincipalLookupService();
        }

        @Override
        public WatchService newWatchService() throws IOException {
            return this.delegate.newWatchService();
        }

        public void closeDelegate() throws IOException {
            this.delegate.close();
        }
    }
}
