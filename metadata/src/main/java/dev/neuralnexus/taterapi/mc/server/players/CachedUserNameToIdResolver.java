/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.mc.server.players;

import static dev.neuralnexus.taterapi.reflecto.MappingClass.builder;
import static dev.neuralnexus.taterapi.reflecto.MappingEntry.entry;
import static dev.neuralnexus.taterapi.reflecto.MappingMember.member;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.Wrapped;
import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.reflecto.MappingMember;
import dev.neuralnexus.taterapi.reflecto.Reflecto;

import org.jspecify.annotations.NonNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.stream.Collectors;

public final class CachedUserNameToIdResolver implements Wrapped<Object> {
    public static final String CACHED_USER_NAME_TO_ID_RESOLVER = "CachedUserNameToIdResolver";
    public static final String GET_PROFILES_BY_NAME = "getProfilesByName";

    private static boolean initialized = false;

    // spotless:off
    public static void init() {
        if (initialized) return;
        initialized = true;

        var cachedUserNameToIdResolver = builder(CACHED_USER_NAME_TO_ID_RESOLVER,
                entry(Mappings.MOJANG, "net.minecraft.server.players.GameProfileCache").max(MinecraftVersions.V21_8),
                entry(Mappings.MOJANG, "net.minecraft.server.players.CachedUserNameToIdResolver", MinecraftVersions.V21_9),
                entry(Mappings.LEGACY_SEARGE, "net.minecraft.server.management.PlayerProfileCache", MinecraftVersions.V7_6, MinecraftVersions.V16_5),
                entry(Mappings.SEARGE, "net.minecraft.server.players.GameProfileCache", MinecraftVersions.V17),
                entry(Mappings.YARN_INTERMEDIARY, "net.minecraft.class_3312"),
                entry(Mappings.CALAMUS, "net.minecraft.unmapped.C_09211509", MinecraftVersions.V7_6)
        ).build();

        var getProfilesByName = member(GET_PROFILES_BY_NAME, cachedUserNameToIdResolver, MappingMember.Type.FIELD_GETTER)
                .access(MappingMember.Access.PRIVATE)
                .modifier(MappingMember.Modifier.FINAL)
                .methodType(MethodType.methodType(Map.class))
                .mappings(
                        entry(Mappings.MOJANG, "profilesByName"),
                        entry(Mappings.LEGACY_SEARGE, "field_152661_c", MinecraftVersions.V7_6, MinecraftVersions.V16_5),
                        entry(Mappings.SEARGE, "f_10966_", MinecraftVersions.V17),
                        entry(Mappings.YARN_INTERMEDIARY, "field_14312"),
                        entry(Mappings.CALAMUS, "f_76385806", MinecraftVersions.V7_6));

        var gameProfileInfo = builder(GameProfileInfo.GAME_PROFILE_INFO,
                entry(Mappings.MOJANG, "net.minecraft.server.players.GameProfileCache$GameProfileInfo").max(MinecraftVersions.V21_8),
                entry(Mappings.MOJANG, "net.minecraft.server.players.CachedUserNameToIdResolver$GameProfileInfo", MinecraftVersions.V21_9),
                entry(Mappings.LEGACY_SEARGE, "net.minecraft.server.management.PlayerProfileCache$ProfileEntry", MinecraftVersions.V7_6, MinecraftVersions.V16_5),
                entry(Mappings.SEARGE, "net.minecraft.server.players.GameProfileCache$GameProfileInfo", MinecraftVersions.V17),
                entry(Mappings.YARN_INTERMEDIARY, "net.minecraft.class_3312$class_3313"),
                entry(Mappings.CALAMUS, "net.minecraft.unmapped.C_63363474", MinecraftVersions.V7_6, MinecraftVersions.V8),
                entry(Mappings.CALAMUS, "net.minecraft.unmapped.C_09211509$C_63363474", MinecraftVersions.V8_1)
        ).build();

        NameAndId.init();
        var nameAndId = member(GameProfileInfo.NAME_AND_ID, gameProfileInfo, MappingMember.Type.METHOD)
                .methodType(MethodType.methodType(GameProfile.class))
                .mappings(
                        entry(Mappings.MOJANG, "getProfile").max(MinecraftVersions.V21_8),
                        entry(Mappings.MOJANG, "nameAndId", MinecraftVersions.V21_9)
                                .methodType(MethodType.methodType(NameAndId.CLASS)),
                        entry(Mappings.LEGACY_SEARGE, "func_152668_a", MinecraftVersions.V7_6, MinecraftVersions.V16_5),
                        entry(Mappings.SEARGE, "m_11028_", MinecraftVersions.V17),
                        entry(Mappings.YARN_INTERMEDIARY, "method_14519").max(MinecraftVersions.V21_8),
                        entry(Mappings.YARN_INTERMEDIARY, "method_72369", MinecraftVersions.V21_9)
                                .methodType(MethodType.methodType(NameAndId.CLASS)),
                        entry(Mappings.CALAMUS, "m_45654766", MinecraftVersions.V7_6));

        Reflecto.register(getProfilesByName, nameAndId);
    }
    // spotless:on

    private final Object cachedUserNameToIdResolver;

    private CachedUserNameToIdResolver(final @NonNull Object cachedUserNameToIdResolver) {
        init();
        this.cachedUserNameToIdResolver = cachedUserNameToIdResolver;
    }

    public static @NonNull CachedUserNameToIdResolver wrap(
            final @NonNull Object cachedUserNameToIdResolver) {
        return new CachedUserNameToIdResolver(cachedUserNameToIdResolver);
    }

    @Override
    public @NonNull Object unwrap() {
        return this.cachedUserNameToIdResolver;
    }

    public Map<String, GameProfileInfo> getProfilesByName() {
        final MethodHandle mh =
                Reflecto.getHandle(CACHED_USER_NAME_TO_ID_RESOLVER, GET_PROFILES_BY_NAME);
        try {
            @SuppressWarnings("unchecked") // TODO: See if this works at runtime
            final Map<String, ?> profilesByName =
                    (Map<String, ?>) mh.invokeExact(this.cachedUserNameToIdResolver);
            return profilesByName.entrySet().stream()
                    .collect(
                            Collectors.toUnmodifiableMap(
                                    Map.Entry::getKey,
                                    entry -> GameProfileInfo.wrap(entry.getValue())));
        } catch (final Throwable e) {
            throw new RuntimeException("Failed to invoke getProfilesByName", e);
        }
    }

    public static final class GameProfileInfo implements Wrapped<Object> {
        public static final String GAME_PROFILE_INFO = "GameProfileInfo";
        public static final String NAME_AND_ID = "nameAndId";

        private final Object gameProfileInfo;

        private GameProfileInfo(final @NonNull Object gameProfileInfo) {
            this.gameProfileInfo = gameProfileInfo;
        }

        public static @NonNull GameProfileInfo wrap(final @NonNull Object gameProfileInfo) {
            return new GameProfileInfo(gameProfileInfo);
        }

        @Override
        public @NonNull Object unwrap() {
            return this.gameProfileInfo;
        }

        public NameAndId nameAndId() {
            return NameAndId.wrap(
                    Reflecto.invoke(GAME_PROFILE_INFO, NAME_AND_ID, this.gameProfileInfo));
        }
    }
}
