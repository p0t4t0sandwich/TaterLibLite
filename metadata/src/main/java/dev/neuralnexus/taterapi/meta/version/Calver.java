/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.version;

import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.impl.version.MinecraftVersionImpl;

public interface Calver {
    // TODO: Add snapshots
    MinecraftVersion V26_1 = MinecraftVersionImpl.of("26.1");
}
