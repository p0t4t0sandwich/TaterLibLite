/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.platforms;

import net.neoforged.fml.common.Mod;

@Mod("tater_metadata")
public class NeoForgeEntrypoint {
    public NeoForgeEntrypoint() {
        TaterMetadata.initNeoForge();
    }
}
