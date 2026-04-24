/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
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
