/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.platforms;

import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin("tater_metadata")
public class Sponge8Entrypoint {
    public Sponge8Entrypoint() {
        TaterMetadata.initSponge();
    }
}
