/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol;

import static dev.neuralnexus.taterapi.network.protocol.PayloadType.custom;

import dev.neuralnexus.taterapi.network.protocol.common.custom.BrandPayload;
import dev.neuralnexus.taterapi.network.protocol.common.custom.CustomPacketPayload;
import dev.neuralnexus.taterapi.network.proxy.bungeecord.BungeeCordPayload;
import dev.neuralnexus.taterapi.registries.AdapterRegistry;

public interface PayloadTypes {
    // spotless:off
    interface CUSTOM {
        CustomPacketPayload.Type<BrandPayload> BRAND =
                custom(BrandPayload.class, "minecraft:brand")
                        .flow(PacketFlow.BIDIRECTIONAL).codec(BrandPayload.STREAM_CODEC).build();

        CustomPacketPayload.Type<BungeeCordPayload> BUNGEECORD =
                custom(BungeeCordPayload.class, "bungeecord:main")
                        .flow(PacketFlow.BIDIRECTIONAL).codec(BungeeCordPayload.STREAM_CODEC).build();
    }
    interface QUERY {}
    interface QUERY_ANSWER {}
    // spotless:on

    AdapterRegistry ADAPTERS = new AdapterRegistry();
}
