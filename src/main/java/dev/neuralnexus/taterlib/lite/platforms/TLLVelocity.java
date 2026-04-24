/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterlib.lite.platforms;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;

import dev.neuralnexus.taterlib.lite.TaterLibLite;

import org.slf4j.Logger;

@Plugin(id = TaterLibLite.MOD_ID)
public class TLLVelocity {
    private final PluginContainer plugin;
    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public TLLVelocity(PluginContainer plugin, ProxyServer server, Logger logger) {
        this.plugin = plugin;
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {}

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {}
}
