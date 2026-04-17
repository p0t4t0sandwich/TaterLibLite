/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.mc.world.entity.player;

import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.Crypt.MAX_KEY_SIGNATURE_SIZE;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readByteArray;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readInstant;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readPublicKey;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.writeByteArray;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.writeInstant;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.writePublicKey;

import io.netty.buffer.ByteBuf;

import org.jspecify.annotations.NonNull;

import java.security.PublicKey;
import java.time.Instant;

public record ProfilePublicKey(@NonNull Data data) {
    public record Data(@NonNull Instant expiresAt, @NonNull PublicKey key, byte[] keySignature) {
        public Data(final @NonNull ByteBuf input) {
            this(
                    readInstant(input),
                    readPublicKey(input),
                    readByteArray(input, MAX_KEY_SIGNATURE_SIZE));
        }

        public void write(final @NonNull ByteBuf output) {
            writeInstant(output, this.expiresAt);
            writePublicKey(output, this.key);
            writeByteArray(output, this.keySignature);
        }
    }
}
