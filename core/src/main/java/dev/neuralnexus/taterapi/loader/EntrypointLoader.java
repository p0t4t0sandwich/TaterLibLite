/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.loader;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.Constraints;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.AConstraints;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * A loader for entrypoint interfaces that extend the {@link Entrypoint} interface. This loader
 * utilizes Java's {@link ServiceLoader} to discover and load entrypoints at runtime. It also checks
 * for constraints specified via annotations to determine if an entrypoints should be loaded.
 *
 * @param <T> The type of entrypoint, which must extend {@link Entrypoint}.
 */
public final class EntrypointLoader<T extends Entrypoint> {
    private final ServiceLoader<T> loader;
    private final Logger logger;
    private final List<T> entrypoints = new ArrayList<>();

    /**
     * Constructs a new EntrypointLoader for the specified entrypoint class.
     *
     * @param entrypointClass The interface of the entrypoint to load, which must extend {@link
     *     Entrypoint}.
     */
    public EntrypointLoader(Class<T> entrypointClass, Logger logger) {
        this.loader = ServiceLoader.load(entrypointClass);
        this.logger = logger;
    }

    /**
     * Loads all entrypoints that meet the specified constraints. This method iterates through all
     * discovered entrypoints and checks for {@link AConstraints} and {@link AConstraint}
     * annotations. If the constraints are met, the entrypoint is added to the list of loaded
     * entrypoints.
     */
    public void load() {
        for (T entrypoint : this.loader) {
            Class<?> clazz = entrypoint.getClass();
            String name = clazz.getName();
            this.logger.debug("Resolving Entrypoint: " + name);

            AConstraints constraints = clazz.getAnnotation(AConstraints.class);
            if (constraints == null) {
                this.logger.debug(
                        "Entrypoint " + name + " does not contain an AConstraints annotation");
            } else if (!Constraints.from(constraints).result()) {
                this.logger.debug("Skipping Entrypoint: " + name);
                continue;
            }
            AConstraint constraint = clazz.getAnnotation(AConstraint.class);
            if (constraint == null) {
                this.logger.debug(
                        "Entrypoint " + name + " does not contain an AConstraint annotation");
            } else if (!Constraint.from(constraint).result()) {
                this.logger.debug("Skipping Entrypoint: " + name);
                continue;
            }
            this.logger.debug("Loading Entrypoint: " + name);
            this.entrypoints.add(entrypoint);
        }
    }

    /** Calls the onInit method of all loaded entrypoints. */
    public void onInit() {
        for (T entrypoint : this.entrypoints) {
            entrypoint.onInit();
        }
    }

    /** Calls the onEnable method of all loaded entrypoints. */
    public void onEnable() {
        for (T entrypoint : this.entrypoints) {
            entrypoint.onEnable();
        }
    }

    /** Calls the onDisable method of all loaded entrypoints. */
    public void onDisable() {
        for (T entrypoint : this.entrypoints) {
            entrypoint.onDisable();
        }
    }
}
