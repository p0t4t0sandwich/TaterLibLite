/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.plugin;

/** Note: When TaterLib is loaded, all instances of this interface will be initialized */
// TODO: Make "dependency", "version(s)", "notVersion"
// TODO: Convert this all to annotations, just using the service loader to locate classes
public interface ResolvableEntrypoint {
    // TODO: Consider adding a primaryPlatform() method

    /** Called when the plugin is initialized, equivalent to running code in the constructor */
    void onInit();

    /** Called during the platform's common enable phase */
    default void onEnable() {}
}
