/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network;

import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.Packet;

import io.netty.buffer.ByteBuf;

import org.jspecify.annotations.NonNull;

public record ProtocolInfo<T extends Packet>(
        @NonNull Class<? extends Packet> clazz,
        @NonNull String identifier,
        @NonNull StreamCodec<? extends ByteBuf, ? extends Packet> codec) {}
