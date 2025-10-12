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

public final class PluginLoader<T extends ResolvableEntrypoint> {
    private final ServiceLoader<T> loader;
    private final List<T> plugins = new ArrayList<>();

    public PluginLoader(Class<T> pluginClass) {
        loader = ServiceLoader.load(pluginClass);
    }

    public void load() {
        for (T plugin : loader) {
            AConstraints constraints = plugin.getClass().getAnnotation(AConstraints.class);
            if (constraints != null && !Constraints.from(constraints).result()) {
                continue;
            }
            AConstraint constraint = plugin.getClass().getAnnotation(AConstraint.class);
            if (constraint != null && !Constraint.from(constraint).result()) {
                continue;
            }
            plugins.add(plugin);
        }
    }

    public void init() {
        for (T plugin : plugins) {
            plugin.onInit();
        }
    }
}
