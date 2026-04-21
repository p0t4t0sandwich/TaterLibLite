/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.proxy.bungeecord;

import dev.neuralnexus.taterapi.network.FriendlyByteBuf;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.PayloadTypes;
import dev.neuralnexus.taterapi.network.protocol.common.custom.CustomPacketPayload;

import io.netty.buffer.ByteBuf;

import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

/**
 * <a href="https://www.spigotmc.org/wiki/bukkit-bungee-plugin-messaging-channel">BuneeCord plugin
 * messaging docs</a>
 *
 * @param subchannel The subchannel
 * @param data The raw payload
 */
public record BungeeCordPayload(@NonNull SubChannel subchannel, @NonNull FriendlyByteBuf data)
        implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, BungeeCordPayload> STREAM_CODEC =
            CustomPacketPayload.codec(BungeeCordPayload::encode, BungeeCordPayload::decode);

    private BungeeCordPayload(@NonNull SubChannel subchannel) {
        this(subchannel, new FriendlyByteBuf());
    }

    private static BungeeCordPayload decode(final @NonNull FriendlyByteBuf input) {
        return new BungeeCordPayload(SubChannel.fromId(input.readUtf()), input.readPayload());
    }

    private void encode(final @NonNull FriendlyByteBuf output) {
        output.writeUtf(this.subchannel.id());
        output.writePayload(this.data);
    }

    @Override
    public @NonNull Type<BungeeCordPayload> type() {
        return PayloadTypes.CUSTOM.BUNGEECORD;
    }

    public void handle(final @NonNull BungeeCordPayloadHandler handler) {
        handler.accept(this);
    }

    @FunctionalInterface
    public interface BungeeCordPayloadHandler extends Consumer<BungeeCordPayload> {
        @Override
        void accept(final @NonNull BungeeCordPayload payload);
    }

    // ----------------------------- REQUESTS -----------------------------

    public static BungeeCordPayload Connect(final @NonNull String server) {
        final FriendlyByteBuf buf = new FriendlyByteBuf();
        buf.writeUtf(server);
        return new BungeeCordPayload(SubChannel.Connect, buf);
    }

    public static BungeeCordPayload ConnectOther(
            final @NonNull String player, final @NonNull String server) {
        final FriendlyByteBuf buf = new FriendlyByteBuf();
        buf.writeUtf(player).writeUtf(server);
        return new BungeeCordPayload(SubChannel.ConnectOther, buf);
    }

    public static BungeeCordPayload ForwardBase(
            final @NonNull SubChannel subchannel,
            final @NonNull String dst,
            final @NonNull String channel,
            final @NonNull ByteBuf payload) {
        final FriendlyByteBuf buf = new FriendlyByteBuf();
        buf.writeUtf(dst);
        buf.writeUtf(channel);
        buf.writeShort(payload.readableBytes());
        buf.writePayload(payload);
        return new BungeeCordPayload(subchannel, buf);
    }

    @SuppressWarnings("unchecked")
    public static <T extends CustomPacketPayload> BungeeCordPayload ForwardBase(
            final @NonNull SubChannel subchannel,
            final @NonNull String dst,
            final @NonNull T payload) {
        final FriendlyByteBuf buf = new FriendlyByteBuf();
        ((CustomPacketPayload.Type<T>) payload.type()).codec().encode(buf, payload);
        return ForwardBase(subchannel, dst, payload.type().id(), buf);
    }

    public static BungeeCordPayload Forward(
            final @NonNull String server,
            final @NonNull String channel,
            final @NonNull ByteBuf payload) {
        return ForwardBase(SubChannel.Forward, server, channel, payload);
    }

    public static BungeeCordPayload ForwardAll(
            final @NonNull String channel, final @NonNull ByteBuf payload) {
        return Forward("ALL", channel, payload);
    }

    public static BungeeCordPayload ForwardOnline(
            final @NonNull String channel, final @NonNull ByteBuf payload) {
        return Forward("ONLINE", channel, payload);
    }

    public static BungeeCordPayload Forward(
            final @NonNull String server, final @NonNull CustomPacketPayload payload) {
        return ForwardBase(SubChannel.Forward, server, payload);
    }

    public static BungeeCordPayload ForwardAll(final @NonNull CustomPacketPayload payload) {
        return Forward("ALL", payload);
    }

    public static BungeeCordPayload ForwardOnline(final @NonNull CustomPacketPayload payload) {
        return Forward("ONLINE", payload);
    }

    public static BungeeCordPayload ForwardToPlayer(
            final @NonNull String player,
            final @NonNull String channel,
            final @NonNull ByteBuf payload) {
        return ForwardBase(SubChannel.ForwardToPlayer, player, channel, payload);
    }

    public static BungeeCordPayload ForwardToPlayer(
            final @NonNull String player, final @NonNull CustomPacketPayload payload) {
        return ForwardBase(SubChannel.ForwardToPlayer, player, payload);
    }

    public static BungeeCordPayload GetServer() {
        return new BungeeCordPayload(SubChannel.GetServer);
    }

    public static BungeeCordPayload GetServers() {
        return new BungeeCordPayload(SubChannel.GetServers);
    }

    public static BungeeCordPayload GetPlayerServer(final @NonNull String player) {
        final FriendlyByteBuf buf = new FriendlyByteBuf();
        buf.writeUtf(player);
        return new BungeeCordPayload(SubChannel.GetPlayerServer, buf);
    }

    public static BungeeCordPayload IP() {
        return new BungeeCordPayload(SubChannel.IP);
    }

    public static BungeeCordPayload IPOther(final @NonNull String player) {
        final FriendlyByteBuf buf = new FriendlyByteBuf();
        buf.writeUtf(player);
        return new BungeeCordPayload(SubChannel.IPOther, buf);
    }

    public static BungeeCordPayload KickPlayer(
            final @NonNull String player, final @NonNull String reason) {
        final FriendlyByteBuf buf = new FriendlyByteBuf();
        buf.writeUtf(player).writeUtf(reason);
        return new BungeeCordPayload(SubChannel.KickPlayer, buf);
    }

    public static BungeeCordPayload KickPlayerRaw(
            final @NonNull String player, final @NonNull String rawReason) {
        final FriendlyByteBuf buf = new FriendlyByteBuf();
        buf.writeUtf(player).writeUtf(rawReason);
        return new BungeeCordPayload(SubChannel.KickPlayerRaw, buf);
    }

    public static BungeeCordPayload Message(
            final @NonNull String player, final @NonNull String message) {
        final FriendlyByteBuf buf = new FriendlyByteBuf();
        buf.writeUtf(player).writeUtf(message);
        return new BungeeCordPayload(SubChannel.Message, buf);
    }

    public static BungeeCordPayload MessageRaw(
            final @NonNull String player, final @NonNull String rawMessage) {
        final FriendlyByteBuf buf = new FriendlyByteBuf();
        buf.writeUtf(player).writeUtf(rawMessage);
        return new BungeeCordPayload(SubChannel.MessageRaw, buf);
    }

    public static BungeeCordPayload PlayerCount(final @NonNull String server) {
        final FriendlyByteBuf buf = new FriendlyByteBuf();
        buf.writeUtf(server);
        return new BungeeCordPayload(SubChannel.PlayerCount, buf);
    }

    public static BungeeCordPayload PlayerCountAll() {
        return PlayerCount("ALL");
    }

    public static BungeeCordPayload PlayerList(final @NonNull String server) {
        final FriendlyByteBuf buf = new FriendlyByteBuf();
        buf.writeUtf(server);
        return new BungeeCordPayload(SubChannel.PlayerList, buf);
    }

    public static BungeeCordPayload PlayerListAll() {
        return PlayerList("ALL");
    }

    public static BungeeCordPayload ServerIP(final @NonNull String server) {
        final FriendlyByteBuf buf = new FriendlyByteBuf();
        buf.writeUtf(server);
        return new BungeeCordPayload(SubChannel.ServerIP, buf);
    }

    public static BungeeCordPayload UUID() {
        return new BungeeCordPayload(SubChannel.UUID);
    }

    public static BungeeCordPayload UUIDOther(final @NonNull String player) {
        final FriendlyByteBuf buf = new FriendlyByteBuf();
        buf.writeUtf(player);
        return new BungeeCordPayload(SubChannel.UUIDOther, buf);
    }
}
