/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.status;

import dev.neuralnexus.taterapi.network.FriendlyByteBuf;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.Packet;
import dev.neuralnexus.taterapi.network.protocol.PacketType;
import dev.neuralnexus.taterapi.network.protocol.PacketTypes;

public final class ServerboundStatusRequestPacket implements Packet {
    public static final ServerboundStatusRequestPacket INSTANCE =
            new ServerboundStatusRequestPacket();
    public static final StreamCodec<FriendlyByteBuf, ServerboundStatusRequestPacket> STREAM_CODEC =
            StreamCodec.unit(INSTANCE);

    private ServerboundStatusRequestPacket() {}

    @Override
    public PacketType<ServerboundStatusRequestPacket> type() {
        return PacketTypes.STATUS.SERVERBOUND_STATUS_REQUEST;
    }
}
