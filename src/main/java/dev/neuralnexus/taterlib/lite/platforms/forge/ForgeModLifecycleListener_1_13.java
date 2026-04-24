/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterlib.lite.platforms.forge;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class ForgeModLifecycleListener_1_13 {
    private final Object loader;

    public ForgeModLifecycleListener_1_13(Object loader) {
        this.loader = loader;
    }

    @SubscribeEvent
    public void onInit(FMLCommonSetupEvent event) {}
}
