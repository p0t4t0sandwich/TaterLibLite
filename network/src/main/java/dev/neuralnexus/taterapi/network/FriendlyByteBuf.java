/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network;

import static dev.neuralnexus.taterapi.resources.Identifier.identifier;

import com.google.common.net.InetAddresses;

import dev.neuralnexus.taterapi.network.codec.StreamDecoder;
import dev.neuralnexus.taterapi.network.codec.StreamEncoder;
import dev.neuralnexus.taterapi.network.protocol.PacketFlow;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.util.ByteProcessor;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Utils copied (in part, as needed) from Minecraft's FriendlyByteBuf and Crypt implementations.
 * <br>
 * Given that, any who use this class must comply with Minecraft's EULA. This class exists purely
 * for compatibility's sake when dealing with multi-version code.
 */
@SuppressWarnings("UnusedReturnValue")
public final class FriendlyByteBuf extends ByteBuf {
    public static final int MAX_STRING_LENGTH = 65535; // 16 bits
    public static final int MAX_PAYLOAD_SIZE = 32767; // Default to serverbound limit of 32 KiB

    private final @NonNull ByteBuf source;

    public FriendlyByteBuf(final @NonNull ByteBuf buf) {
        this.source = buf;
    }

    public FriendlyByteBuf() {
        this.source = Unpooled.buffer();
    }

    @NullUnmarked // TODO: Further look into the nullability
    public static FriendlyByteBuf wrap(final @Nullable ByteBuf buf) {
        return switch (buf) {
            case null -> null;
            case FriendlyByteBuf fBuf -> fBuf;
            default -> new FriendlyByteBuf(buf);
        };
    }

    public static ByteBuf unwrap(final @Nullable ByteBuf buf) {
        return switch (buf) {
            case null -> null;
            case FriendlyByteBuf fBuf -> fBuf.unwrap();
            default -> buf;
        };
    }

    // ---------------- Addon methods -----------------
    public @NonNull InetAddress readAddress() {
        return InetAddresses.forString(this.readUtf());
    }

    public @NonNull FriendlyByteBuf readPayload(final int maxSize) {
        int i = this.readableBytes();
        if (i >= 0 && i <= maxSize) {
            return this.readBytes(i);
        } else {
            throw new DecoderException("Payload may not be larger than " + maxSize + " bytes");
        }
    }

    public @NonNull FriendlyByteBuf readPayload(final PacketFlow flow) {
        return this.readPayload(flow.maxPayloadSize());
    }

    public @NonNull FriendlyByteBuf readPayload() {
        return this.readPayload(MAX_PAYLOAD_SIZE);
    }

    public @NonNull FriendlyByteBuf writePayload(
            final @NonNull ByteBuf payload, final int length, final int maxSize) {
        if (length > maxSize) {
            throw new EncoderException("Payload may not be larger than " + maxSize + " bytes");
        }
        return this.writeBytes(payload, payload.readerIndex(), length);
    }

    public @NonNull FriendlyByteBuf writePayload(
            final @NonNull ByteBuf payload, final int length, final PacketFlow flow) {
        return this.writePayload(payload, length, flow.maxPayloadSize());
    }

    public @NonNull FriendlyByteBuf writePayload(
            final @NonNull ByteBuf payload, final int maxSize) {
        return this.writePayload(payload, payload.readableBytes(), maxSize);
    }

    public @NonNull FriendlyByteBuf writePayload(
            final @NonNull ByteBuf payload, final PacketFlow flow) {
        return this.writePayload(payload, payload.readableBytes(), flow.maxPayloadSize());
    }

    public @NonNull FriendlyByteBuf writePayload(final @NonNull ByteBuf payload) {
        return this.writePayload(payload, payload.readableBytes(), MAX_PAYLOAD_SIZE);
    }

    public @Nullable FriendlyByteBuf readNullablePayload(final int maxSize) {
        return this.readNullable((b) -> b.readPayload(maxSize));
    }

    public @Nullable FriendlyByteBuf readNullablePayload(final PacketFlow flow) {
        return this.readNullable((b) -> b.readPayload(flow));
    }

    public @Nullable FriendlyByteBuf readNullablePayload() {
        return this.readNullable(FriendlyByteBuf::readPayload);
    }

    public @NonNull FriendlyByteBuf writeNullablePayload(
            final @Nullable ByteBuf payload, int maxSize) {
        this.writeNullable(payload, (b, p) -> b.writePayload(p, maxSize));
        return this;
    }

    public @NonNull FriendlyByteBuf writeNullablePayload(
            final @Nullable ByteBuf payload, PacketFlow flow) {
        this.writeNullable(payload, (b, p) -> b.writePayload(p, flow));
        return this;
    }

    public @NonNull FriendlyByteBuf writeNullablePayload(final @Nullable ByteBuf payload) {
        this.writeNullablePayload(payload, MAX_PAYLOAD_SIZE);
        return this;
    }

    // ---------------- FriendlyByteBuf methods -----------------
    public @NonNull String readUtf() {
        return this.readUtf(MAX_STRING_LENGTH);
    }

    public @NonNull String readUtf(final int maxLength) {
        return Utf8String.read(this.source, maxLength);
    }

    public @NonNull FriendlyByteBuf writeUtf(final @NonNull String string, int maxLength) {
        return wrap(Utf8String.write(this.source, string, maxLength));
    }

    public @NonNull FriendlyByteBuf writeUtf(final @NonNull String string) {
        return wrap(Utf8String.write(this.source, string, MAX_STRING_LENGTH));
    }

    public int readVarInt() {
        return VarInt.read(this.source);
    }

    public @NonNull FriendlyByteBuf writeVarInt(final int varInt) {
        return wrap(VarInt.write(this.source, varInt));
    }

    public @NonNull UUID readUUID() {
        return new UUID(this.readLong(), this.readLong());
    }

    public @NonNull FriendlyByteBuf writeUUID(final @NonNull UUID uuid) {
        this.writeLong(uuid.getMostSignificantBits());
        this.writeLong(uuid.getLeastSignificantBits());
        return this;
    }

    public byte[] readByteArray(final int maxLength) {
        int i = this.readVarInt();
        if (i > maxLength) {
            throw new DecoderException(
                    "ByteArray with size " + i + " is bigger than allowed " + maxLength);
        } else {
            byte[] abyte = new byte[i];
            this.readBytes(abyte);
            return abyte;
        }
    }

    public @NonNull FriendlyByteBuf writeByteArray(final byte[] bytes, int maxLength) {
        if (bytes.length > maxLength) {
            throw new EncoderException(
                    "Byte array with size "
                            + bytes.length
                            + " is bigger than allowed "
                            + maxLength);
        } else {
            this.writeVarInt(bytes.length);
            return this.writeBytes(bytes);
        }
    }

    public @NonNull FriendlyByteBuf writeByteArray(final byte[] bytes) {
        return wrap(this.writeByteArray(bytes, MAX_PAYLOAD_SIZE));
    }

    public <T> @NonNull T readIdentifier() {
        return identifier(this.readUtf());
    }

    public @NonNull FriendlyByteBuf writeIdentifier(final @NonNull Object identifier) {
        return this.writeUtf(identifier.toString());
    }

    public <T> Optional<T> readOptional(
            final @NonNull StreamDecoder<? super FriendlyByteBuf, T> decoder) {
        return this.readBoolean() ? Optional.of(decoder.decode(this)) : Optional.empty();
    }

    public <T> void writeOptional(
            @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
                    final @NonNull Optional<T> optional,
            final @NonNull StreamEncoder<? super FriendlyByteBuf, T> encoder) {
        if (optional.isPresent()) {
            this.writeBoolean(true);
            encoder.encode(this, optional.get());
        } else {
            this.writeBoolean(false);
        }
    }

    public @Nullable <T> T readNullable(
            final @NonNull StreamDecoder<? super FriendlyByteBuf, T> decoder) {
        return this.readBoolean() ? decoder.decode(this) : null;
    }

    public <T> void writeNullable(
            final @Nullable T nullable,
            final @NonNull StreamEncoder<? super FriendlyByteBuf, T> encoder) {
        if (nullable != null) {
            this.writeBoolean(true);
            encoder.encode(this, nullable);
        } else {
            this.writeBoolean(false);
        }
    }

    public <T> T readOrElse(
            final @NonNull StreamDecoder<? super FriendlyByteBuf, T> decoder,
            final @NonNull T defaultValue) {
        return this.readBoolean() ? decoder.decode(this) : defaultValue;
    }

    public @NonNull Instant readInstant() {
        return Instant.ofEpochMilli(this.readLong());
    }

    public @NonNull FriendlyByteBuf writeInstant(final @NonNull Instant instant) {
        return this.writeLong(instant.toEpochMilli());
    }

    public @NonNull PublicKey readPublicKey() {
        try {
            return Crypt.byteToPublicKey(this.readByteArray(Crypt.MAX_PUBLIC_KEY_LENGTH));
        } catch (final CryptException e) {
            throw new DecoderException("Malformed public key bytes", e);
        }
    }

    public @NonNull FriendlyByteBuf writePublicKey(final @NonNull PublicKey publicKey) {
        return this.writeByteArray(publicKey.getEncoded(), Crypt.MAX_PUBLIC_KEY_LENGTH);
    }

    // ---------------- ByteBuf methods -----------------
    @Override
    public int capacity() {
        return this.source.capacity();
    }

    @Override
    public @NonNull FriendlyByteBuf capacity(final int newCapacity) {
        return wrap(this.source.capacity(newCapacity));
    }

    @Override
    public int maxCapacity() {
        return this.source.maxCapacity();
    }

    @Override
    public @NonNull ByteBufAllocator alloc() {
        return this.source.alloc();
    }

    @Deprecated
    @Override
    public @NonNull ByteOrder order() {
        return this.source.order();
    }

    @Deprecated
    @Override
    public @NonNull FriendlyByteBuf order(final @NonNull ByteOrder endianness) {
        return wrap(this.source.order(endianness));
    }

    @Override
    public @NonNull ByteBuf unwrap() {
        return this.source;
    }

    @Override
    public boolean isDirect() {
        return this.source.isDirect();
    }

    @Override
    public boolean isReadOnly() {
        return this.source.isReadOnly();
    }

    // Note: Added in Netty 4.1, not available below 1.12
    @Override
    public @NonNull FriendlyByteBuf asReadOnly() {
        return wrap(this.source.asReadOnly());
    }

    @Override
    public int readerIndex() {
        return this.source.readerIndex();
    }

    @Override
    public @NonNull FriendlyByteBuf readerIndex(final int readerIndex) {
        return wrap(this.source.readerIndex(readerIndex));
    }

    @Override
    public int writerIndex() {
        return this.source.writerIndex();
    }

    @Override
    public @NonNull FriendlyByteBuf writerIndex(final int writerIndex) {
        return wrap(this.source.writerIndex(writerIndex));
    }

    @Override
    public @NonNull FriendlyByteBuf setIndex(final int readerIndex, final int writerIndex) {
        return wrap(this.source.setIndex(readerIndex, writerIndex));
    }

    @Override
    public int readableBytes() {
        return this.source.readableBytes();
    }

    @Override
    public int writableBytes() {
        return this.source.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return this.source.maxWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return this.source.isReadable();
    }

    @Override
    public boolean isReadable(final int size) {
        return this.source.isReadable(size);
    }

    @Override
    public boolean isWritable() {
        return this.source.isWritable();
    }

    @Override
    public boolean isWritable(final int size) {
        return this.source.isWritable(size);
    }

    @Override
    public @NonNull FriendlyByteBuf clear() {
        return wrap(this.source.clear());
    }

    @Override
    public @NonNull FriendlyByteBuf markReaderIndex() {
        return wrap(this.source.markReaderIndex());
    }

    @Override
    public @NonNull FriendlyByteBuf resetReaderIndex() {
        return wrap(this.source.resetReaderIndex());
    }

    @Override
    public @NonNull FriendlyByteBuf markWriterIndex() {
        return wrap(this.source.markWriterIndex());
    }

    @Override
    public @NonNull FriendlyByteBuf resetWriterIndex() {
        return wrap(this.source.resetWriterIndex());
    }

    @Override
    public @NonNull FriendlyByteBuf discardReadBytes() {
        return wrap(this.source.discardReadBytes());
    }

    @Override
    public @NonNull FriendlyByteBuf discardSomeReadBytes() {
        return wrap(this.source.discardSomeReadBytes());
    }

    @Override
    public @NonNull FriendlyByteBuf ensureWritable(final int minWritableBytes) {
        return wrap(this.source.ensureWritable(minWritableBytes));
    }

    @Override
    public int ensureWritable(final int minWritableBytes, final boolean force) {
        return this.source.ensureWritable(minWritableBytes, force);
    }

    @Override
    public boolean getBoolean(final int index) {
        return this.source.getBoolean(index);
    }

    @Override
    public byte getByte(final int index) {
        return this.source.getByte(index);
    }

    @Override
    public short getUnsignedByte(final int index) {
        return this.source.getUnsignedByte(index);
    }

    @Override
    public short getShort(final int index) {
        return this.source.getShort(index);
    }

    @Override
    public short getShortLE(final int index) {
        return this.source.getShortLE(index);
    }

    @Override
    public int getUnsignedShort(final int index) {
        return this.source.getUnsignedShort(index);
    }

    @Override
    public int getUnsignedShortLE(final int index) {
        return this.source.getUnsignedShortLE(index);
    }

    @Override
    public int getMedium(final int index) {
        return this.source.getMedium(index);
    }

    @Override
    public int getMediumLE(final int index) {
        return this.source.getMediumLE(index);
    }

    @Override
    public int getUnsignedMedium(final int index) {
        return this.source.getUnsignedMedium(index);
    }

    @Override
    public int getUnsignedMediumLE(final int index) {
        return this.source.getUnsignedMediumLE(index);
    }

    @Override
    public int getInt(final int index) {
        return this.source.getInt(index);
    }

    @Override
    public int getIntLE(final int index) {
        return this.source.getIntLE(index);
    }

    @Override
    public long getUnsignedInt(final int index) {
        return this.source.getUnsignedInt(index);
    }

    @Override
    public long getUnsignedIntLE(final int index) {
        return this.source.getUnsignedIntLE(index);
    }

    @Override
    public long getLong(final int index) {
        return this.source.getLong(index);
    }

    @Override
    public long getLongLE(final int index) {
        return this.source.getLongLE(index);
    }

    @Override
    public char getChar(final int index) {
        return this.source.getChar(index);
    }

    @Override
    public float getFloat(final int index) {
        return this.source.getFloat(index);
    }

    @Override
    public double getDouble(final int index) {
        return this.source.getDouble(index);
    }

    @Override
    public @NonNull FriendlyByteBuf getBytes(final int index, final @NonNull ByteBuf dst) {
        return wrap(this.source.getBytes(index, dst));
    }

    @Override
    public @NonNull FriendlyByteBuf getBytes(
            final int index, final @NonNull ByteBuf dst, final int length) {
        return wrap(this.source.getBytes(index, dst, length));
    }

    @Override
    public @NonNull FriendlyByteBuf getBytes(
            final int index, final @NonNull ByteBuf dst, final int dstIndex, final int length) {
        return wrap(this.source.getBytes(index, dst, dstIndex, length));
    }

    @Override
    public @NonNull FriendlyByteBuf getBytes(final int index, final byte[] dst) {
        return wrap(this.source.getBytes(index, dst));
    }

    @Override
    public @NonNull FriendlyByteBuf getBytes(
            final int index, final byte[] dst, final int dstIndex, final int length) {
        return wrap(this.source.getBytes(index, dst, dstIndex, length));
    }

    @Override
    public @NonNull FriendlyByteBuf getBytes(final int index, final @NonNull ByteBuffer dst) {
        return wrap(this.source.getBytes(index, dst));
    }

    @Override
    public @NonNull FriendlyByteBuf getBytes(
            final int index, final @NonNull OutputStream out, final int length) throws IOException {
        return wrap(this.source.getBytes(index, out, length));
    }

    @Override
    public int getBytes(final int index, final @NonNull GatheringByteChannel out, final int length)
            throws IOException {
        return this.source.getBytes(index, out, length);
    }

    @Override
    public int getBytes(
            final int index, final @NonNull FileChannel out, final long position, final int length)
            throws IOException {
        return this.source.getBytes(index, out, position, length);
    }

    @Override
    public @NonNull CharSequence getCharSequence(
            final int index, final int length, final @NonNull Charset charset) {
        return this.source.getCharSequence(index, length, charset);
    }

    @Override
    public @NonNull FriendlyByteBuf setBoolean(final int index, final boolean value) {
        return wrap(this.source.setBoolean(index, value));
    }

    @Override
    public @NonNull FriendlyByteBuf setByte(final int index, final int value) {
        return wrap(this.source.setByte(index, value));
    }

    @Override
    public @NonNull FriendlyByteBuf setShort(final int index, final int value) {
        return wrap(this.source.setShort(index, value));
    }

    @Override
    public @NonNull FriendlyByteBuf setShortLE(final int index, final int value) {
        return wrap(this.source.setShortLE(index, value));
    }

    @Override
    public @NonNull FriendlyByteBuf setMedium(final int index, final int value) {
        return wrap(this.source.setMedium(index, value));
    }

    @Override
    public @NonNull FriendlyByteBuf setMediumLE(final int index, final int value) {
        return wrap(this.source.setMediumLE(index, value));
    }

    @Override
    public @NonNull FriendlyByteBuf setInt(final int index, final int value) {
        return wrap(this.source.setInt(index, value));
    }

    @Override
    public @NonNull FriendlyByteBuf setIntLE(final int index, final int value) {
        return wrap(this.source.setIntLE(index, value));
    }

    @Override
    public @NonNull FriendlyByteBuf setLong(final int index, final long value) {
        return wrap(this.source.setLong(index, value));
    }

    @Override
    public @NonNull FriendlyByteBuf setLongLE(final int index, final long value) {
        return wrap(this.source.setLongLE(index, value));
    }

    @Override
    public @NonNull FriendlyByteBuf setChar(final int index, final int value) {
        return wrap(this.source.setChar(index, value));
    }

    @Override
    public @NonNull FriendlyByteBuf setFloat(final int index, final float value) {
        return wrap(this.source.setFloat(index, value));
    }

    @Override
    public @NonNull FriendlyByteBuf setDouble(final int index, final double value) {
        return wrap(this.source.setDouble(index, value));
    }

    @Override
    public @NonNull FriendlyByteBuf setBytes(final int index, final @NonNull ByteBuf src) {
        return wrap(this.source.setBytes(index, src));
    }

    @Override
    public @NonNull FriendlyByteBuf setBytes(
            final int index, final @NonNull ByteBuf src, final int length) {
        return wrap(this.source.setBytes(index, src, length));
    }

    @Override
    public @NonNull FriendlyByteBuf setBytes(
            final int index, final @NonNull ByteBuf src, final int srcIndex, final int length) {
        return wrap(this.source.setBytes(index, src, srcIndex, length));
    }

    @Override
    public @NonNull FriendlyByteBuf setBytes(final int index, final byte[] src) {
        return wrap(this.source.setBytes(index, src));
    }

    @Override
    public @NonNull FriendlyByteBuf setBytes(
            final int index, final byte[] src, final int srcIndex, final int length) {
        return wrap(this.source.setBytes(index, src, srcIndex, length));
    }

    @Override
    public @NonNull FriendlyByteBuf setBytes(final int index, final @NonNull ByteBuffer src) {
        return wrap(this.source.setBytes(index, src));
    }

    @Override
    public int setBytes(final int index, final @NonNull InputStream in, final int length)
            throws IOException {
        return this.source.setBytes(index, in, length);
    }

    @Override
    public int setBytes(final int index, final @NonNull ScatteringByteChannel in, final int length)
            throws IOException {
        return this.source.setBytes(index, in, length);
    }

    @Override
    public int setBytes(
            final int index, final @NonNull FileChannel in, final long position, final int length)
            throws IOException {
        return this.source.setBytes(index, in, position, length);
    }

    @Override
    public @NonNull FriendlyByteBuf setZero(final int index, final int length) {
        return wrap(this.source.setZero(index, length));
    }

    @Override
    public int setCharSequence(
            final int index, final @NonNull CharSequence sequence, final @NonNull Charset charset) {
        return this.source.setCharSequence(index, sequence, charset);
    }

    @Override
    public boolean readBoolean() {
        return this.source.readBoolean();
    }

    @Override
    public byte readByte() {
        return this.source.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return this.source.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return this.source.readShort();
    }

    @Override
    public short readShortLE() {
        return this.source.readShortLE();
    }

    @Override
    public int readUnsignedShort() {
        return this.source.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return this.source.readUnsignedShortLE();
    }

    @Override
    public int readMedium() {
        return this.source.readMedium();
    }

    @Override
    public int readMediumLE() {
        return this.source.readMediumLE();
    }

    @Override
    public int readUnsignedMedium() {
        return this.source.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return this.source.readUnsignedMediumLE();
    }

    @Override
    public int readInt() {
        return this.source.readInt();
    }

    @Override
    public int readIntLE() {
        return this.source.readIntLE();
    }

    @Override
    public long readUnsignedInt() {
        return this.source.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return this.source.readUnsignedIntLE();
    }

    @Override
    public long readLong() {
        return this.source.readLong();
    }

    @Override
    public long readLongLE() {
        return this.source.readLongLE();
    }

    @Override
    public char readChar() {
        return this.source.readChar();
    }

    @Override
    public float readFloat() {
        return this.source.readFloat();
    }

    @Override
    public double readDouble() {
        return this.source.readDouble();
    }

    @Override
    public @NonNull FriendlyByteBuf readBytes(final int length) {
        return wrap(this.source.readBytes(length));
    }

    @Override
    public @NonNull FriendlyByteBuf readSlice(final int length) {
        return wrap(this.source.readSlice(length));
    }

    @Override
    public @NonNull FriendlyByteBuf readRetainedSlice(final int length) {
        return wrap(this.source.readRetainedSlice(length));
    }

    @Override
    public @NonNull FriendlyByteBuf readBytes(final @NonNull ByteBuf dst) {
        return wrap(this.source.readBytes(dst));
    }

    @Override
    public @NonNull FriendlyByteBuf readBytes(final @NonNull ByteBuf dst, final int length) {
        return wrap(this.source.readBytes(dst, length));
    }

    @Override
    public @NonNull FriendlyByteBuf readBytes(
            final @NonNull ByteBuf dst, final int dstIndex, final int length) {
        return wrap(this.source.readBytes(dst, dstIndex, length));
    }

    @Override
    public @NonNull FriendlyByteBuf readBytes(final byte[] dst) {
        return wrap(this.source.readBytes(dst));
    }

    @Override
    public @NonNull FriendlyByteBuf readBytes(
            final byte[] dst, final int dstIndex, final int length) {
        return wrap(this.source.readBytes(dst, dstIndex, length));
    }

    @Override
    public @NonNull FriendlyByteBuf readBytes(final @NonNull ByteBuffer dst) {
        return wrap(this.source.readBytes(dst));
    }

    @Override
    public @NonNull FriendlyByteBuf readBytes(final @NonNull OutputStream out, final int length)
            throws IOException {
        return wrap(this.source.readBytes(out, length));
    }

    @Override
    public int readBytes(final @NonNull GatheringByteChannel out, final int length)
            throws IOException {
        return this.source.readBytes(out, length);
    }

    @Override
    public @NonNull CharSequence readCharSequence(
            final int length, final @NonNull Charset charset) {
        return this.source.readCharSequence(length, charset);
    }

    @Override
    public int readBytes(final @NonNull FileChannel out, final long position, final int length)
            throws IOException {
        return this.source.readBytes(out, position, length);
    }

    @Override
    public @NonNull FriendlyByteBuf skipBytes(final int length) {
        return wrap(this.source.skipBytes(length));
    }

    @Override
    public @NonNull FriendlyByteBuf writeBoolean(final boolean value) {
        return wrap(this.source.writeBoolean(value));
    }

    @Override
    public @NonNull FriendlyByteBuf writeByte(final int value) {
        return wrap(this.source.writeByte(value));
    }

    @Override
    public @NonNull FriendlyByteBuf writeShort(final int value) {
        return wrap(this.source.writeShort(value));
    }

    @Override
    public @NonNull FriendlyByteBuf writeShortLE(final int value) {
        return wrap(this.source.writeShortLE(value));
    }

    @Override
    public @NonNull FriendlyByteBuf writeMedium(final int value) {
        return wrap(this.source.writeMedium(value));
    }

    @Override
    public @NonNull FriendlyByteBuf writeMediumLE(final int value) {
        return wrap(this.source.writeMediumLE(value));
    }

    @Override
    public @NonNull FriendlyByteBuf writeInt(final int value) {
        return wrap(this.source.writeInt(value));
    }

    @Override
    public @NonNull FriendlyByteBuf writeIntLE(final int value) {
        return wrap(this.source.writeIntLE(value));
    }

    @Override
    public @NonNull FriendlyByteBuf writeLong(final long value) {
        return wrap(this.source.writeLong(value));
    }

    @Override
    public @NonNull FriendlyByteBuf writeLongLE(final long value) {
        return wrap(this.source.writeLongLE(value));
    }

    @Override
    public @NonNull FriendlyByteBuf writeChar(final int value) {
        return wrap(this.source.writeChar(value));
    }

    @Override
    public @NonNull FriendlyByteBuf writeFloat(final float value) {
        return wrap(this.source.writeFloat(value));
    }

    @Override
    public @NonNull FriendlyByteBuf writeDouble(final double value) {
        return wrap(this.source.writeDouble(value));
    }

    @Override
    public @NonNull FriendlyByteBuf writeBytes(final @NonNull ByteBuf src) {
        return wrap(this.source.writeBytes(src));
    }

    @Override
    public @NonNull FriendlyByteBuf writeBytes(final @NonNull ByteBuf src, final int length) {
        return wrap(this.source.writeBytes(src, length));
    }

    @Override
    public @NonNull FriendlyByteBuf writeBytes(
            final @NonNull ByteBuf src, final int srcIndex, final int length) {
        return wrap(this.source.writeBytes(src, srcIndex, length));
    }

    @Override
    public @NonNull FriendlyByteBuf writeBytes(final byte[] src) {
        return wrap(this.source.writeBytes(src));
    }

    @Override
    public @NonNull FriendlyByteBuf writeBytes(
            final byte[] src, final int srcIndex, final int length) {
        return wrap(this.source.writeBytes(src, srcIndex, length));
    }

    @Override
    public @NonNull FriendlyByteBuf writeBytes(final @NonNull ByteBuffer src) {
        return wrap(this.source.writeBytes(src));
    }

    @Override
    public int writeBytes(final @NonNull InputStream in, final int length) throws IOException {
        return this.source.writeBytes(in, length);
    }

    @Override
    public int writeBytes(final @NonNull ScatteringByteChannel in, final int length)
            throws IOException {
        return this.source.writeBytes(in, length);
    }

    @Override
    public int writeBytes(final @NonNull FileChannel in, final long position, final int length)
            throws IOException {
        return this.source.writeBytes(in, position, length);
    }

    @Override
    public @NonNull FriendlyByteBuf writeZero(final int length) {
        return wrap(this.source.writeZero(length));
    }

    @Override
    public int writeCharSequence(
            final @NonNull CharSequence sequence, final @NonNull Charset charset) {
        return this.source.writeCharSequence(sequence, charset);
    }

    @Override
    public int indexOf(final int fromIndex, final int toIndex, final byte value) {
        return this.source.indexOf(fromIndex, toIndex, value);
    }

    @Override
    public int bytesBefore(final byte value) {
        return this.source.bytesBefore(value);
    }

    @Override
    public int bytesBefore(final int length, final byte value) {
        return this.source.bytesBefore(length, value);
    }

    @Override
    public int bytesBefore(final int index, final int length, final byte value) {
        return this.source.bytesBefore(index, length, value);
    }

    @Override
    public int forEachByte(final @NonNull ByteProcessor processor) {
        return this.source.forEachByte(processor);
    }

    @Override
    public int forEachByte(
            final int index, final int length, final @NonNull ByteProcessor processor) {
        return this.source.forEachByte(index, length, processor);
    }

    @Override
    public int forEachByteDesc(final @NonNull ByteProcessor processor) {
        return this.source.forEachByteDesc(processor);
    }

    @Override
    public int forEachByteDesc(
            final int index, final int length, final @NonNull ByteProcessor processor) {
        return this.source.forEachByteDesc(index, length, processor);
    }

    @Override
    public @NonNull FriendlyByteBuf copy() {
        return wrap(this.source.copy());
    }

    @Override
    public @NonNull FriendlyByteBuf copy(final int index, final int length) {
        return wrap(this.source.copy(index, length));
    }

    @Override
    public @NonNull FriendlyByteBuf slice() {
        return wrap(this.source.slice());
    }

    @Override
    public @NonNull FriendlyByteBuf retainedSlice() {
        return wrap(this.source.retainedSlice());
    }

    @Override
    public @NonNull FriendlyByteBuf slice(final int index, final int length) {
        return wrap(this.source.slice(index, length));
    }

    @Override
    public @NonNull FriendlyByteBuf retainedSlice(final int index, final int length) {
        return wrap(this.source.retainedSlice(index, length));
    }

    @Override
    public @NonNull FriendlyByteBuf duplicate() {
        return wrap(this.source.duplicate());
    }

    @Override
    public @NonNull FriendlyByteBuf retainedDuplicate() {
        return wrap(this.source.retainedDuplicate());
    }

    @Override
    public int nioBufferCount() {
        return this.source.nioBufferCount();
    }

    @Override
    public @NonNull ByteBuffer nioBuffer() {
        return this.source.nioBuffer();
    }

    @Override
    public @NonNull ByteBuffer nioBuffer(final int index, final int length) {
        return this.source.nioBuffer(index, length);
    }

    @Override
    public @NonNull ByteBuffer internalNioBuffer(final int index, final int length) {
        return this.source.internalNioBuffer(index, length);
    }

    @Override
    public @NonNull ByteBuffer[] nioBuffers() {
        return this.source.nioBuffers();
    }

    @Override
    public @NonNull ByteBuffer[] nioBuffers(final int index, final int length) {
        return this.source.nioBuffers(index, length);
    }

    @Override
    public boolean hasArray() {
        return this.source.hasArray();
    }

    @Override
    public byte[] array() {
        return this.source.array();
    }

    @Override
    public int arrayOffset() {
        return this.source.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return this.source.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return this.source.memoryAddress();
    }

    @Override
    public @NonNull String toString(final @NonNull Charset charset) {
        return this.source.toString(charset);
    }

    @Override
    public @NonNull String toString(
            final int index, final int length, final @NonNull Charset charset) {
        return this.source.toString(index, length, charset);
    }

    @Override
    public int hashCode() {
        return this.source.hashCode();
    }

    @Override
    public boolean equals(final @NonNull Object o) {
        if (!(o instanceof ByteBuf)) {
            return false;
        }
        return this.source.equals(o);
    }

    @Override
    public int compareTo(final @NonNull ByteBuf byteBuffer) {
        return this.source.compareTo(byteBuffer);
    }

    @Override
    public @NonNull String toString() {
        return this.source.toString();
    }

    @Override
    public @NonNull FriendlyByteBuf retain(final int increment) {
        return wrap(this.source.retain(increment));
    }

    @Override
    public int refCnt() {
        return this.source.refCnt();
    }

    @Override
    public @NonNull FriendlyByteBuf retain() {
        return wrap(this.source.retain());
    }

    @Override
    public @NonNull FriendlyByteBuf touch() {
        return wrap(this.source.touch());
    }

    @Override
    public @NonNull FriendlyByteBuf touch(final @NonNull Object hint) {
        return wrap(this.source.touch(hint));
    }

    @Override
    public boolean release() {
        return this.source.release();
    }

    @Override
    public boolean release(final int decrement) {
        return this.source.release(decrement);
    }
}
