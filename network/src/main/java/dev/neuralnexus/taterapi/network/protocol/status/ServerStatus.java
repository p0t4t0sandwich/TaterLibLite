/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import dev.neuralnexus.taterapi.mc.server.players.NameAndId;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.serialization.Codec;
import dev.neuralnexus.taterapi.serialization.Result;

import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public record ServerStatus(
        JsonObject description, // TODO: Create proper abstraction for Component
        Optional<Players> players,
        Optional<Version> version,
        Optional<String> favicon,
        boolean enforcesSecureChat) {
    private static final Gson gson =
            new GsonBuilder().registerTypeAdapterFactory(OptionalTypeAdapter.FACTORY).create();

    public static final Codec<ServerStatus, String> CODEC =
            Codec.of(
                    status -> Result.success(gson.toJson(status)),
                    string -> Result.success(gson.fromJson(string, ServerStatus.class)));

    // TODO: Use this later
    public record Favicon(byte[] iconBytes) {
        private static final String PREFIX = "data:image/png;base64,";
        public static final Codec<Favicon, String> CODEC =
                Codec.of(Favicon::encode, Favicon::decode);

        private static <T extends String> Result<Favicon> decode(T string) {
            if (!string.startsWith(PREFIX)) {
                return Result.error("Unknown format");
            } else {
                try {
                    final String base64 = string.substring(PREFIX.length()).replaceAll("\n", "");
                    final byte[] iconBytes =
                            Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
                    return Result.success(new Favicon(iconBytes));
                } catch (final IllegalArgumentException e) {
                    return Result.error("Malformed base64 server icon");
                }
            }
        }

        private static Result<String> encode(Favicon favicon) {
            final String faviconString =
                    new String(
                            Base64.getEncoder().encode(favicon.iconBytes), StandardCharsets.UTF_8);
            return Result.success(PREFIX + faviconString);
        }
    }

    public record Players(int max, int online, List<NameAndId> sample) {}

    public record Version(@NonNull String name, int protocol) {
        public static Version current() {
            final MinecraftVersion version = MetaAPI.instance().version();
            // TODO: Add name and protocolVersion to MinecraftVersion
            return new Version(version.version(), -1);
        }
    }

    /**
     * Type adapter for Optional, <a
     * href="https://stackoverflow.com/questions/12161366/how-to-serialize-optionalt-classes-with-gson/25078422#25078422">
     * found in this StackOverflow post</a>
     *
     * @param <E> The inner type of the Optional
     */
    public static class OptionalTypeAdapter<E> extends TypeAdapter<Optional<E>> {
        public static final TypeAdapterFactory FACTORY =
                new TypeAdapterFactory() {
                    @SuppressWarnings({"rawtypes", "unchecked"})
                    @Override
                    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
                        Class<T> rawType = (Class<T>) type.getRawType();
                        if (rawType != Optional.class) {
                            return null;
                        }
                        final ParameterizedType parameterizedType =
                                (ParameterizedType) type.getType();
                        final Type actualType = parameterizedType.getActualTypeArguments()[0];
                        final TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(actualType));
                        return new OptionalTypeAdapter(adapter);
                    }
                };
        private final TypeAdapter<E> adapter;

        public OptionalTypeAdapter(TypeAdapter<E> adapter) {
            this.adapter = adapter;
        }

        @Override
        public void write(JsonWriter out, Optional<E> value) throws IOException {
            if (value.isPresent()) {
                adapter.write(out, value.get());
            } else {
                out.nullValue();
            }
        }

        @Override
        public Optional<E> read(JsonReader in) throws IOException {
            final JsonToken peek = in.peek();
            if (peek != JsonToken.NULL) {
                return Optional.ofNullable(adapter.read(in));
            }

            in.nextNull();
            return Optional.empty();
        }
    }
}
