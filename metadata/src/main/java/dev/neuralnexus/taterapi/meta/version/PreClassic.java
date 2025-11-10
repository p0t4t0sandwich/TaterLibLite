/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.version;

import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.impl.version.MinecraftVersionImpl;

// 1
@SuppressWarnings("unused")
public interface PreClassic {
    MinecraftVersion RD_132211 = MinecraftVersionImpl.of("rd-132211");
    MinecraftVersion RD_132328 = MinecraftVersionImpl.of("rd-132328");
    MinecraftVersion RD_20090515 = MinecraftVersionImpl.of("rd-20090515");
    MinecraftVersion RD_160052 = MinecraftVersionImpl.of("rd-160052");
    MinecraftVersion RD_161348 = MinecraftVersionImpl.of("rd-161348");
}
