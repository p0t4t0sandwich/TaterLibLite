/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
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
