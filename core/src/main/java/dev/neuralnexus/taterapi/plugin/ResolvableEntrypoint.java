/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.plugin;

/**
 * An entrypoint interface for use with {@link PluginLoader}, usage requires this interface to be
 * extended to avoid potential conflicts.
 */
public interface ResolvableEntrypoint {
    /** Called when the plugin is initialized, equivalent to running code in the constructor */
    void onInit();

    /** Called during the platform's common enable phase */
    default void onEnable() {}
}
