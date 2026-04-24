/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterlib.lite.platforms;

import com.google.inject.Inject;

import dev.neuralnexus.taterlib.lite.TaterLibLite;

import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin(TaterLibLite.MOD_ID)
public class TLLSponge {
    private final Logger logger;
    private final PluginContainer pluginContainer;

    @Inject
    public TLLSponge(Logger logger, PluginContainer pluginContainer) {
        this.logger = logger;
        this.pluginContainer = pluginContainer;
    }

    @Listener
    public void onServerStarting(ConstructPluginEvent event) {}

    @Listener
    public void onServerStopping(StoppingEngineEvent<Server> event) {}
}
