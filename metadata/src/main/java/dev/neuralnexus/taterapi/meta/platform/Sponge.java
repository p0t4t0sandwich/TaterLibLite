/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.platform;

import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.impl.platform.PlatformImpl;

public interface Sponge {
    Platform SPONGE = new PlatformImpl("Sponge", "org.spongepowered.api.Sponge");

    // TODO: Decide if these should be added
    // Platform SPONGEVANILLA = new PlatformImpl("SpongeVanilla");
    // Platform SPONGEFORGE = new PlatformImpl("SpongeForge");
    // Platform LOOFAH = new PlatformImpl("Loofah");
}
