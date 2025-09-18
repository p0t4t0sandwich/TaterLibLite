package dev.neuralnexus.taterlib.lite.platforms;

import cpw.mods.fml.common.Mod;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import dev.neuralnexus.taterlib.lite.TaterLibLite;

@Mod(modid = TaterLibLite.MOD_ID)
public class TLLForgeLegacy {
    public TLLForgeLegacy() {}

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {}

    @Mod.EventHandler
    public void onDisable(FMLServerStoppedEvent event) {}
}
