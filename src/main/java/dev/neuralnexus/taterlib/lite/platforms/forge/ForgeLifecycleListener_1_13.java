/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@sperrer.ca
 * The project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLib/blob/dev/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterlib.lite.platforms.forge;


import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeLifecycleListener_1_13 {
    private final Object loader;

    public ForgeLifecycleListener_1_13(Object loader) {
        this.loader = loader;
    }

    @SubscribeEvent
    public void onDisable(ServerStoppedEvent event) {}
}
