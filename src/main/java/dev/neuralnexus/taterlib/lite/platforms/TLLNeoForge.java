package dev.neuralnexus.taterlib.lite.platforms;

import dev.neuralnexus.taterlib.lite.TaterLibLite;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

@Mod(TaterLibLite.MOD_ID)
public class TLLNeoForge {
    public TLLNeoForge(IEventBus eventBus) {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onDisable(ServerStoppedEvent event) {}
}
