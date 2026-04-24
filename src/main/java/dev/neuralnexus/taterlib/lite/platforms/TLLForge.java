/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterlib.lite.platforms;

import dev.neuralnexus.taterlib.lite.TaterLibLite;

import net.minecraftforge.fml.common.Mod;

@Mod(value = TaterLibLite.MOD_ID, modid = TaterLibLite.MOD_ID)
public class TLLForge {
    public TLLForge() {
        //        if (MetaAPI.instance().version().isOlderThan(MinecraftVersions.V13)) {
        //            MinecraftForge.EVENT_BUS.register(new ForgeLifecycleListener_1_8());
        //        } else {
        //            MinecraftForge.EVENT_BUS.register(new ForgeLifecycleListener_1_13());
        //            IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        //            if (bus != null) {
        //                bus.register(new ForgeModLifecycleListener_1_13());
        //            } else {
        //                TaterLibLite.logger().warn("Failed to register events to mod event bus");
        //            }
        //        }
    }
}
