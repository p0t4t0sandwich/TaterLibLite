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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
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
    private final @Nullable ServiceLoader<T> loader;
    private final @NonNull Logger logger;
    private final Collection<@NonNull Class<? extends T>> providers;
    private final boolean useServiceLoader;
    private final boolean useOtherProviders;
    private final Collection<@NonNull T> entrypoints = new ArrayList<>();

    /**
     * Constructs a new EntrypointLoader for the specified entrypoint class.
     *
     * @param entrypointClass The interface of the entrypoint to load, which must extend {@link
     *     Entrypoint}.
     * @param logger The logger to use for logging messages.
     * @param providers A collection of additional provider classes to load.
     * @param useServiceLoader Whether to use the ServiceLoader mechanism.
     * @param useOtherProviders Whether to use additional provider classes.
     */
    @SuppressWarnings("unchecked")
    EntrypointLoader(
            final @NonNull Class<? extends Entrypoint> entrypointClass,
            final @NonNull Logger logger,
            final Collection<@NonNull Class<? extends Entrypoint>> providers,
            final boolean useServiceLoader,
            final boolean useOtherProviders) {
        if (!useServiceLoader) {
            this.loader = null;
        } else {
            this.loader = (ServiceLoader<T>) ServiceLoader.load(entrypointClass);
        }
        this.logger = logger;
        this.providers = (Collection<@NonNull Class<? extends T>>) (Object) providers;
        this.useServiceLoader = useServiceLoader;
        this.useOtherProviders = useOtherProviders;
    }

    /**
     * Constructs a new EntrypointLoader for the specified entrypoint class.
     *
     * @param entrypointClass The interface of the entrypoint to load, which must extend {@link
     *     Entrypoint}.
     * @param cl The class loader to use for loading entrypoints.
     * @param logger The logger to use for logging messages.
     * @param providers A collection of additional provider classes to load.
     * @param useServiceLoader Whether to use the ServiceLoader mechanism.
     * @param useOtherProviders Whether to use additional provider classes.
     */
    @SuppressWarnings("unchecked")
    EntrypointLoader(
            final @NonNull Class<? extends Entrypoint> entrypointClass,
            final @Nullable ClassLoader cl,
            final @NonNull Logger logger,
            final Collection<@NonNull Class<? extends Entrypoint>> providers,
            final boolean useServiceLoader,
            final boolean useOtherProviders) {
        if (!useServiceLoader) {
            this.loader = null;
        } else {
            if (cl == null) {
                this.loader = (ServiceLoader<T>) ServiceLoader.load(entrypointClass);
            } else {
                this.loader = (ServiceLoader<T>) ServiceLoader.load(entrypointClass, cl);
            }
        }
        this.logger = logger;
        this.providers = (Collection<@NonNull Class<? extends T>>) (Object) providers;
        this.useServiceLoader = useServiceLoader;
        this.useOtherProviders = useOtherProviders;
    }

    /**
     * Loads entrypoints using the ServiceLoader mechanism.
     *
     * @return A collection of loaded entrypoints.
     */
    private Collection<@NonNull T> loadServiceLoader() {
        final Collection<@NonNull T> loadedEntrypoints = new ArrayList<>();
        if (this.loader == null) {
            throw new IllegalStateException("ServiceLoader is not initialized.");
        }
        Iterator<T> iterator = loader.iterator();
        while (iterator.hasNext()) {
            try {
                final T entrypoint = iterator.next();
                final Class<? extends Entrypoint> clazz = entrypoint.getClass();
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
                loadedEntrypoints.add(entrypoint);
            } catch (ServiceConfigurationError e) {
                final StringWriter sw = new StringWriter();
                final PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                this.logger.debug("Failed to resolve entrypoint: \n" + sw);
            }
        }
        return loadedEntrypoints;
    }

    static Collection<Class<? extends Entrypoint>> readServiceFile(
            Logger logger, Path servicePath) {
        final Collection<Class<? extends Entrypoint>> classes = new ArrayList<>();
        try (final InputStream is = Files.newInputStream(servicePath);
                final BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                try {
                    final Class<? extends Entrypoint> clazz =
                            Class.forName(line, false, EntrypointLoader.class.getClassLoader())
                                    .asSubclass(Entrypoint.class);
                    classes.add(clazz);
                } catch (ClassCastException | ClassNotFoundException e) {
                    final StringWriter sw = new StringWriter();
                    final PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    logger.debug("Failed to load class from line: " + line + "\n" + sw);
                }
            }
        } catch (IOException e) {
            logger.debug("Failed to read service file at path: " + servicePath);
        }
        return classes;
    }

    /**
     * Manually loads entrypoints from the given service classes.
     *
     * @return A collection of loaded entrypoints.
     */
    private Collection<@NonNull T> loadProviderClasses() {
        final Collection<@NonNull T> loadedEntrypoints = new ArrayList<>();
        for (final Class<? extends T> provider : this.providers) {
            final String name = provider.getName();
            try {
                this.logger.debug("Resolving Entrypoint: " + name);

                final AConstraints constraints = provider.getAnnotation(AConstraints.class);
                if (constraints != null && !Constraints.from(constraints).result()) {
                    this.logger.debug("Skipping Entrypoint: " + name);
                    continue;
                }

                final AConstraint constraint = provider.getAnnotation(AConstraint.class);
                if (constraint != null && !Constraint.from(constraint).result()) {
                    this.logger.debug("Skipping Entrypoint: " + name);
                    continue;
                }

                final T instance = provider.getConstructor().newInstance();

                loadedEntrypoints.add(instance);
            } catch (InstantiationException
                    | IllegalAccessException
                    | InvocationTargetException
                    | NoSuchMethodException e) {
                final StringWriter sw = new StringWriter();
                final PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                this.logger.debug("Failed to resolve entrypoint: " + name + "\n" + sw);
            }
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
        // final boolean isJava9 = Float.parseFloat(System.getProperty("java.class.version")) >= 52;
        final boolean serviceLoaderBroken =
                Constraint.builder()
                        .platform(Platforms.FORGE)
                        .min(MinecraftVersions.V14_2)
                        .max(MinecraftVersions.V16_2)
                        .build()
                        .result();
        if (serviceLoaderBroken || this.useOtherProviders) {
            this.entrypoints.addAll(this.loadProviderClasses());
        }
        if (!serviceLoaderBroken && this.useServiceLoader) {
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

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Class<? extends Entrypoint> entrypointClass;
        private @Nullable ClassLoader classLoader = null;
        private @NonNull Logger logger = Logger.create("EntrypointLoader");
        private final Collection<@NonNull Class<? extends Entrypoint>> providers =
                new ArrayList<>();
        private boolean useServiceLoader = true;
        private boolean useOtherProviders = false;

        Builder() {}

        public Builder entrypointClass(final @NonNull Class<? extends Entrypoint> entrypointClass) {
            this.entrypointClass = entrypointClass;
            return this;
        }

        public Builder classLoader(final @NonNull ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        public Builder logger(final @NonNull Logger logger) {
            this.logger = logger;
            return this;
        }

        public Builder servicePaths(final Collection<@NonNull Path> servicePaths) {
            for (final Path servicePath : servicePaths) {
                this.providers.addAll(readServiceFile(this.logger, servicePath));
            }
            return this;
        }

        public Builder servicePaths(final @NonNull Path... servicePaths) {
            for (final Path servicePath : servicePaths) {
                this.providers.addAll(readServiceFile(this.logger, servicePath));
            }
            return this;
        }

        public Builder serviceNames(final Collection<@NonNull String> serviceNames) {
            for (final String serviceName : serviceNames) {
                try {
                    final Class<? extends Entrypoint> clazz =
                            Class.forName(
                                            serviceName,
                                            false,
                                            EntrypointLoader.class.getClassLoader())
                                    .asSubclass(Entrypoint.class);
                    this.providers.add(clazz);
                } catch (ClassCastException | ClassNotFoundException e) {
                    final StringWriter sw = new StringWriter();
                    final PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    this.logger.debug("Failed to load class from name: " + serviceName + "\n" + sw);
                }
            }
            return this;
        }

        public Builder serviceNames(final @NonNull String... serviceNames) {
            return this.serviceNames(Arrays.asList(serviceNames));
        }

        public Builder serviceClasses(
                final Collection<@NonNull Class<? extends Entrypoint>> serviceClasses) {
            this.providers.addAll(serviceClasses);
            return this;
        }

        public Builder serviceClasses(
                final @NonNull Class<? extends Entrypoint>... serviceClasses) {
            this.providers.addAll(Arrays.asList(serviceClasses));
            return this;
        }

        public Builder useServiceLoader(final boolean useServiceLoader) {
            this.useServiceLoader = useServiceLoader;
            return this;
        }

        public Builder useOtherProviders(final boolean useOtherProviders) {
            this.useOtherProviders = useOtherProviders;
            return this;
        }

        public @NonNull <T extends Entrypoint> EntrypointLoader<T> build() {
            if (this.classLoader != null) {
                return new EntrypointLoader<>(
                        this.entrypointClass,
                        this.classLoader,
                        this.logger,
                        this.providers,
                        this.useServiceLoader,
                        this.useOtherProviders);
            } else {
                return new EntrypointLoader<>(
                        this.entrypointClass,
                        this.logger,
                        this.providers,
                        this.useServiceLoader,
                        this.useOtherProviders);
            }
        }
    }
}
