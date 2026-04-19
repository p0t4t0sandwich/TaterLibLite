/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.login;

import dev.neuralnexus.taterapi.mc.world.entity.player.ProfilePublicKey;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.network.FriendlyByteBuf;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.Packet;
import dev.neuralnexus.taterapi.network.protocol.PacketType;
import dev.neuralnexus.taterapi.network.protocol.PacketTypes;

import io.netty.handler.codec.EncoderException;

import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.UUID;

//    1.7 - 1.18.2 -> (name)
//            1.19 -> (name, publicKey?)
// 1.19.1 - 1.19.2 -> (name, publicKey?, profileId?)
// 1.19.3 - 1.20.1 -> (name, profileId?)
//         1.20.2+ -> (name, profileId)
public record ServerboundHelloPacket(
        @NonNull String name, Optional<ProfilePublicKey.Data> publicKey, Optional<UUID> profileId)
        implements Packet {
    public static final int MAX_NAME_LENGTH = 16;
    // spotless:off
    public static final StreamCodec<FriendlyByteBuf, ServerboundHelloPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ServerboundHelloPacket>versioned()
            .add(ServerboundHelloPacket::encode_7, ServerboundHelloPacket::decode_7, MinecraftVersions.V7, MinecraftVersions.V18_2)
            .add(ServerboundHelloPacket::encode_19, ServerboundHelloPacket::decode_19, MinecraftVersions.V19)
            .add(ServerboundHelloPacket::encode_19_1, ServerboundHelloPacket::decode_19_1, MinecraftVersions.V19_1, MinecraftVersions.V19_2)
            .add(ServerboundHelloPacket::encode_19, ServerboundHelloPacket::decode_19, MinecraftVersions.V19_3, MinecraftVersions.V20_1)
            .add(ServerboundHelloPacket::encode_20_2, ServerboundHelloPacket::decode_20_2, MinecraftVersions.V20_2)
            .build();
    // spotless:on

    public ServerboundHelloPacket {
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "Name may not be longer than " + MAX_NAME_LENGTH + " characters!");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Empty username!");
        }
    }

    private static ServerboundHelloPacket decode_7(final @NonNull FriendlyByteBuf input) {
        return new ServerboundHelloPacket(input.readUtf(), null, null);
    }

    private void encode_7(final @NonNull FriendlyByteBuf output) {
        output.writeUtf(this.name);
    }

    private static ServerboundHelloPacket decode_19(final @NonNull FriendlyByteBuf input) {
        return new ServerboundHelloPacket(
                input.readUtf(), input.readOptional(ProfilePublicKey.Data::new), null);
    }

    private void encode_19(final @NonNull FriendlyByteBuf output) {
        output.writeUtf(this.name);
        output.writeOptional(this.publicKey, (buf, data) -> data.write(buf));
    }

    private static ServerboundHelloPacket decode_19_1(final @NonNull FriendlyByteBuf input) {
        return new ServerboundHelloPacket(
                input.readUtf(),
                input.readOptional(ProfilePublicKey.Data::new),
                input.readOptional(FriendlyByteBuf::readUUID));
    }

    private void encode_19_1(final @NonNull FriendlyByteBuf output) {
        output.writeUtf(this.name);
        output.writeOptional(this.publicKey, (buf, data) -> data.write(buf));
        output.writeOptional(this.profileId, FriendlyByteBuf::writeUUID);
    }

    private static ServerboundHelloPacket decode_20_2(final @NonNull FriendlyByteBuf input) {
        return new ServerboundHelloPacket(
                input.readUtf(), Optional.empty(), Optional.of(input.readUUID()));
    }

    private void encode_20_2(final @NonNull FriendlyByteBuf output) {
        output.writeUtf(this.name);
        output.writeUUID(
                this.profileId.orElseThrow(
                        () -> new EncoderException("Profile ID is required for 1.20.2+")));
    }

    public PacketType<ServerboundHelloPacket> type() {
        return PacketTypes.LOGIN.SERVERBOUND_HELLO;
    }
}
