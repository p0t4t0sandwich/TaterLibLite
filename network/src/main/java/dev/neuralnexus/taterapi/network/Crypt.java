/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network;

import org.jspecify.annotations.NonNull;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public final class Crypt {
    public static final String ASYMMETRIC_ALGORITHM = "RSA";
    public static final int MAX_KEY_SIGNATURE_SIZE = 4096;
    public static final int MAX_PUBLIC_KEY_LENGTH = 512;

    public static @NonNull PublicKey byteToPublicKey(final byte[] bytes) throws CryptException {
        try {
            final EncodedKeySpec encodedkeyspec = new X509EncodedKeySpec(bytes);
            final KeyFactory keyfactory = KeyFactory.getInstance(ASYMMETRIC_ALGORITHM);
            return keyfactory.generatePublic(encodedkeyspec);
        } catch (final Exception exception) {
            throw new CryptException(exception);
        }
    }
}
