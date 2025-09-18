/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@sperrer.ca
 * The project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLib/blob/dev/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterlib.lite.platforms.forge;

import cpw.mods.fml.common.event.FMLServerStoppedEvent;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public class ForgeLifecycleListener_1_8 {
    private final Object loader;

    public ForgeLifecycleListener_1_8(Object loader) {
        this.loader = loader;
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {}

    @Mod.EventHandler
    public void onDisable(FMLServerStoppedEvent event) {}
}
