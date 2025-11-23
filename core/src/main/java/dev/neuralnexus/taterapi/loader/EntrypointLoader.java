/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.loader;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.Constraints;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.AConstraints;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

/**
 * A loader for entrypoint interfaces that extend the {@link Entrypoint} interface. This loader
 * utilizes Java's {@link ServiceLoader} to discover and load entrypoints at runtime. It also checks
 * for constraints specified via annotations to determine if an entrypoints should be loaded.
 *
 * @param <T> The type of entrypoint, which must extend {@link Entrypoint}.
 */
public final class EntrypointLoader<T extends Entrypoint> {
    private final @NonNull ServiceLoader<T> loader;
    private final @NonNull Logger logger;
    private final Collection<@NonNull Path> servicePaths;
    private final boolean forceFallback;
    private final Collection<@NonNull T> entrypoints = new ArrayList<>();

    /**
     * Constructs a new EntrypointLoader for the specified entrypoint class.
     *
     * @param entrypointClass The interface of the entrypoint to load, which must extend {@link
     *     Entrypoint}.
     * @param logger The logger to use for logging messages.
     * @param servicePaths The collection of service paths to use for fallback loading.
     * @param forceFallback Whether to force fallback loading from service paths.
     */
    private EntrypointLoader(
            final @NonNull Class<T> entrypointClass,
            final @NonNull Logger logger,
            final Collection<@NonNull Path> servicePaths,
            final boolean forceFallback) {
        this.loader = ServiceLoader.load(entrypointClass);
        this.logger = logger;
        this.servicePaths = servicePaths;
        this.forceFallback = forceFallback;
    }

    /**
     * Constructs a new EntrypointLoader for the specified entrypoint class.
     *
     * @param entrypointClass The interface of the entrypoint to load, which must extend {@link
     *     Entrypoint}.
     * @param cl The class loader to use for loading entrypoints.
     * @param logger The logger to use for logging messages.
     * @param servicePaths The collection of service paths to use for fallback loading.
     * @param forceFallback Whether to force fallback loading from service paths.
     */
    private EntrypointLoader(
            final @NonNull Class<T> entrypointClass,
            final @Nullable ClassLoader cl,
            final @NonNull Logger logger,
            final Collection<@NonNull Path> servicePaths,
            final boolean forceFallback) {
        this.loader = ServiceLoader.load(entrypointClass, cl);
        this.logger = logger;
        this.servicePaths = servicePaths;
        this.forceFallback = forceFallback;
    }

    /**
     * Loads entrypoints using the ServiceLoader mechanism.
     *
     * @return A collection of loaded entrypoints.
     */
    private Collection<@NonNull T> loadServiceLoader() {
        final Collection<@NonNull T> loadedEntrypoints = new ArrayList<>();
        for (final ServiceLoader.Provider<T> p : loader.stream().toList()) {
            try {
                final Class<? extends T> clazz = p.type();
                final String name = clazz.getName();
                this.logger.debug("Resolving Entrypoint: " + name);

                final AConstraints constraints = clazz.getAnnotation(AConstraints.class);
                if (constraints != null && !Constraints.from(constraints).result()) {
                    this.logger.debug("Skipping Entrypoint: " + name);
                    continue;
                }
                final AConstraint constraint = clazz.getAnnotation(AConstraint.class);
                if (constraint != null && !Constraint.from(constraint).result()) {
                    this.logger.debug("Skipping Entrypoint: " + name);
                    continue;
                }

                this.logger.debug("Loading Entrypoint: " + name);
                final T entrypoint = p.get();

                loadedEntrypoints.add(entrypoint);
            } catch (ServiceConfigurationError e) {
                final StringWriter sw = new StringWriter();
                final PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                this.logger.debug("Failed to load entrypoint: " + sw);
            } catch (Throwable e) {
                final StringWriter sw = new StringWriter();
                final PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                this.logger.debug("An unexpected error occurred while loading entrypoint: " + sw);
            }
        }
        return loadedEntrypoints;
    }

    /**
     * Manually loads entrypoints from the specified service file.
     *
     * @param servicePath The path to the service file.
     * @return A collection of loaded entrypoints.
     */
    private Collection<@NonNull T> loadFallbackPath(@NonNull Path servicePath) {
        final Collection<@NonNull T> loadedEntrypoints = new ArrayList<>();
        try (final InputStream is = Files.newInputStream(servicePath);
                final BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                try {
                    final Class<?> clazz =
                            Class.forName(line, false, this.getClass().getClassLoader());
                    final String name = clazz.getName();
                    this.logger.debug("Resolving Entrypoint: " + name);

                    final AConstraints constraints = clazz.getAnnotation(AConstraints.class);
                    if (constraints != null && !Constraints.from(constraints).result()) {
                        this.logger.debug("Skipping Entrypoint: " + name);
                        continue;
                    }

                    final AConstraint constraint = clazz.getAnnotation(AConstraint.class);
                    if (constraint != null && !Constraint.from(constraint).result()) {
                        this.logger.debug("Skipping Entrypoint: " + name);
                        continue;
                    }

                    @SuppressWarnings("unchecked")
                    final T instance = (T) clazz.getConstructor().newInstance();

                    loadedEntrypoints.add(instance);
                } catch (ClassNotFoundException
                        | InstantiationException
                        | IllegalAccessException
                        | InvocationTargetException
                        | NoSuchMethodException e) {
                    final StringWriter sw = new StringWriter();
                    final PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    this.logger.debug("Failed to load entrypoint from line: " + line + "\n" + sw);
                }
            }
        } catch (IOException e) {
            this.logger.debug("Failed to read service file at path: " + servicePath);
        }
        return loadedEntrypoints;
    }

    /**
     * Loads all entrypoints that meet the specified constraints. This method iterates through all
     * discovered entrypoints and checks for {@link AConstraints} and {@link AConstraint}
     * annotations. If the constraints are met, the entrypoint is added to the list of loaded
     * entrypoints.
     */
    public void load() {
        // TODO: Add Java class versions to Constraint
        final boolean isJava9 = Integer.parseInt(System.getProperty("java.class.version")) >= 52;
        final boolean serviceLoaderBroken =
                Constraint.builder()
                        .platform(Platforms.FORGE)
                        .min(MinecraftVersions.V14_2)
                        .max(MinecraftVersions.V16_2)
                        .build()
                        .result();
        if (!isJava9 || serviceLoaderBroken || this.forceFallback) {
            for (final @NonNull Path servicePath : this.servicePaths) {
                this.entrypoints.addAll(this.loadFallbackPath(servicePath));
            }
        } else {
            this.entrypoints.addAll(this.loadServiceLoader());
        }
    }

    /** Calls the onInit method of all loaded entrypoints. */
    public void onInit() {
        for (final @NonNull T entrypoint : this.entrypoints) {
            entrypoint.onInit();
        }
    }

    /** Calls the onEnable method of all loaded entrypoints. */
    public void onEnable() {
        for (final @NonNull T entrypoint : this.entrypoints) {
            entrypoint.onEnable();
        }
    }

    /** Calls the onDisable method of all loaded entrypoints. */
    public void onDisable() {
        for (final @NonNull T entrypoint : this.entrypoints) {
            entrypoint.onDisable();
        }
    }

    public static <T extends Entrypoint> Builder<T> builder() {
        return new Builder<>();
    }

    public static final class Builder<T extends Entrypoint> {
        private Class<T> entrypointClass;
        private @Nullable ClassLoader classLoader = null;
        private @NonNull Logger logger = Logger.create("EntrypointLoader");
        private final Collection<@NonNull Path> servicePaths = new ArrayList<>();
        private boolean forceFallback = false;

        private Builder() {}

        @SuppressWarnings("unchecked")
        public Builder<T> entrypointClass(final @NonNull Class<?> entrypointClass) {
            this.entrypointClass = (Class<T>) entrypointClass;
            return this;
        }

        public Builder<T> classLoader(final @NonNull ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        public Builder<T> logger(final @NonNull Logger logger) {
            this.logger = logger;
            return this;
        }

        public Builder<T> servicePaths(final Collection<@NonNull Path> servicePaths) {
            this.servicePaths.addAll(servicePaths);
            return this;
        }

        public Builder<T> servicePaths(final @NonNull Path... servicePaths) {
            this.servicePaths.addAll(List.of(servicePaths));
            return this;
        }

        public Builder<T> forceFallback(final boolean forceFallback) {
            this.forceFallback = forceFallback;
            return this;
        }

        public @NonNull EntrypointLoader<T> build() {
            if (this.classLoader != null) {
                return new EntrypointLoader<>(
                        this.entrypointClass,
                        this.classLoader,
                        this.logger,
                        this.servicePaths,
                        this.forceFallback);
            } else {
                return new EntrypointLoader<>(
                        this.entrypointClass, this.logger, this.servicePaths, this.forceFallback);
            }
        }
    }
}
