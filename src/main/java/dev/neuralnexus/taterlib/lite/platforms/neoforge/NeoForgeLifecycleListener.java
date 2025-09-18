/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@sperrer.ca
 * The project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLib/blob/dev/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterlib.lite.platforms.neoforge;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

public class NeoForgeLifecycleListener {
    private final Object loader;

    public NeoForgeLifecycleListener(Object loader) {
        this.loader = loader;
    }

    @SubscribeEvent
    public void onInit(FMLCommonSetupEvent event) {}
}
