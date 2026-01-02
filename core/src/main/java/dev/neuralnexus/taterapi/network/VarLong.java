/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network;

import io.netty.buffer.ByteBuf;

import org.jspecify.annotations.NonNull;

/**
 * Utils copied (in part, as needed) from Minecraft's VarLong implementation. <br>
 * Given that, any who use this class must comply with Minecraft's EULA. This class exists purely
 * for compatibility's sake when dealing with multi-version code.
 */
public final class VarLong {
    private static final int MAX_VARLONG_SIZE = 10;
    private static final int DATA_BITS_MASK = 127;
    private static final int CONTINUATION_BIT_MASK = 128;
    private static final int DATA_BITS_PER_BYTE = 7;

    public static int getByteSize(final long bytes) {
        for (int i = 1; i < MAX_VARLONG_SIZE; i++) {
            if ((bytes & -1L << i * DATA_BITS_PER_BYTE) == 0L) {
                return i;
            }
        }

        return MAX_VARLONG_SIZE;
    }

    public static boolean hasContinuationBit(final byte b) {
        return (b & CONTINUATION_BIT_MASK) == CONTINUATION_BIT_MASK;
    }

    public static long read(final @NonNull ByteBuf buf) {
        long l = 0L;
        int i = 0;

        byte b;
        do {
            b = buf.readByte();
            l |= (long) (b & DATA_BITS_MASK) << i++ * DATA_BITS_PER_BYTE;
            if (i > MAX_VARLONG_SIZE) {
                throw new RuntimeException("VarLong too big");
            }
        } while (hasContinuationBit(b));

        return l;
    }

    public static ByteBuf write(final @NonNull ByteBuf buf, long varLong) {
        while ((varLong & -CONTINUATION_BIT_MASK) != 0L) {
            buf.writeByte((int) (varLong & DATA_BITS_MASK) | CONTINUATION_BIT_MASK);
            varLong >>>= DATA_BITS_PER_BYTE;
        }
        buf.writeByte((int) varLong);
        return buf;
    }
}
