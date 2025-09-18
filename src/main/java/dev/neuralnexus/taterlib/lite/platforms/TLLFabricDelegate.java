package dev.neuralnexus.taterlib.lite.platforms;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;

public class TLLFabricDelegate implements ClientModInitializer, DedicatedServerModInitializer {
    private boolean hasInitialized = false;

    @Override
    public void onInitializeClient() {
        if (this.hasInitialized) {
            return;
        }
        this.hasInitialized = true;
    }

    @Override
    public void onInitializeServer() {
        if (this.hasInitialized) {
            return;
        }
        this.hasInitialized = true;
    }
}
