package dev.neuralnexus.taterlib.lite.platforms;

import com.google.inject.Inject;
import dev.neuralnexus.taterlib.lite.TaterLibLite;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

@Plugin(id = TaterLibLite.MOD_ID)
public class TLLSpongeLegacy {
    @Inject
    public TLLSpongeLegacy(PluginContainer container) {}

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {}

    @Listener
    public void onServerStopped(GameStoppedServerEvent event) {}
}
