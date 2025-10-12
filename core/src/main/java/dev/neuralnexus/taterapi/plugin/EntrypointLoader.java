/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.plugin;

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
 * @param <T> The type of the plugin, which must extend {@link Entrypoint}.
 */
public final class EntrypointLoader<T extends Entrypoint> {
    private final ServiceLoader<T> loader;
    private final List<T> entrypoints = new ArrayList<>();

    /**
     * Constructs a new EntrypointLoader for the specified plugin class.
     *
     * @param pluginClass The interface of the entrypoint to load, which must extend {@link Entrypoint}.
     */
    public EntrypointLoader(Class<T> pluginClass) {
        loader = ServiceLoader.load(pluginClass);
    }

    /**
     * Loads all entrypoints that meet the specified constraints.
     * This method iterates through all discovered entrypoints and checks for
     * {@link AConstraints} and {@link AConstraint} annotations. If the constraints are met,
     * the entrypoint is added to the list of loaded entrypoints.
     */
    public void load() {
        for (T entrypoint : loader) {
            AConstraints constraints = entrypoint.getClass().getAnnotation(AConstraints.class);
            if (constraints != null && !Constraints.from(constraints).result()) {
                continue;
            }
            AConstraint constraint = entrypoint.getClass().getAnnotation(AConstraint.class);
            if (constraint != null && !Constraint.from(constraint).result()) {
                continue;
            }
            entrypoints.add(entrypoint);
        }
    }

    /** Calls the onInit method of all loaded entrypoints. */
    public void onInit() {
        for (T entrypoint : entrypoints) {
            entrypoint.onInit();
        }
    }

    /** Calls the onEnable method of all loaded entrypoints. */
    public void onEnable() {
        for (T entrypoint : entrypoints) {
            entrypoint.onEnable();
        }
    }

    /** Calls the onDisable method of all loaded entrypoints. */
    public void onDisable() {
        for (T entrypoint : entrypoints) {
            entrypoint.onDisable();
        }
    }
}
