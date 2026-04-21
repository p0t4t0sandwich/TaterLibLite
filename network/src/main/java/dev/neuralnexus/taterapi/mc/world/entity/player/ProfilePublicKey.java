/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.mc.world.entity.player;

import static dev.neuralnexus.taterapi.network.Crypt.MAX_KEY_SIGNATURE_SIZE;

import dev.neuralnexus.taterapi.network.FriendlyByteBuf;

import org.jspecify.annotations.NonNull;

import java.security.PublicKey;
import java.time.Instant;

public record ProfilePublicKey(@NonNull Data data) {
    public record Data(@NonNull Instant expiresAt, @NonNull PublicKey key, byte[] keySignature) {
        public Data(final @NonNull FriendlyByteBuf input) {
            this(
                    input.readInstant(),
                    input.readPublicKey(),
                    input.readByteArray(MAX_KEY_SIGNATURE_SIZE));
        }

        public void write(final @NonNull FriendlyByteBuf output) {
            output.writeInstant(this.expiresAt);
            output.writePublicKey(this.key);
            output.writeByteArray(this.keySignature, MAX_KEY_SIGNATURE_SIZE);
        }
    }
}
