package dev.neuralnexus.taterlib.lite.platforms;

import dev.neuralnexus.taterlib.lite.TaterLibLite;

import dev.neuralnexus.taterlib.lite.platforms.forge.ForgeLifecycleListener_1_13;
import dev.neuralnexus.taterlib.lite.platforms.forge.ForgeLifecycleListener_1_8;
import dev.neuralnexus.taterlib.lite.platforms.forge.ForgeModLifecycleListener_1_13;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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
