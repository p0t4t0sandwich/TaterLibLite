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
import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.reflecto.MappingClass;
import dev.neuralnexus.taterapi.reflecto.MappingMember;
import dev.neuralnexus.taterapi.reflecto.Reflecto;

import org.jspecify.annotations.NonNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.UUID;

/**
 * A record that holds a name and an ID
 *
 * @param name the player name
 * @param id the player UUID
 */
public record NameAndId(@NonNull UUID id, @NonNull String name) implements Wrapped<Object> {
    public static final String NAME_AND_ID = "NameAndId";
    public static final String NAME_AND_ID_CONSTRUCTOR_ID_NAME = "NameAndIdConstructorIdName";
    public static final String NAME = "name";
    public static final String ID = "id";
    public static MappingClass nameAndIdClass;

    private static boolean initialized = false;

    // spotless:off
    private static void init() {
        if (initialized) return;
        initialized = true;

        nameAndIdClass = builder(NAME_AND_ID,
                entry(Mappings.MOJANG, "net.minecraft.server.players.NameAndId",
                        MinecraftVersions.V21_9),
                entry(Mappings.YARN_INTERMEDIARY, "net.minecraft.class_11560",
                        MinecraftVersions.V21_9))
                .build();

        var constructor = member(NAME_AND_ID_CONSTRUCTOR_ID_NAME, nameAndIdClass, MappingMember.Type.CONSTRUCTOR)
                .methodType(MethodType.methodType(void.class, UUID.class, String.class))
                .mappings(
                        entry(Mappings.MOJANG, "<init>",
                                MinecraftVersions.V21_9),
                        entry(Mappings.YARN_INTERMEDIARY, "<init>",
                                MinecraftVersions.V21_9));

        var id = member(ID, nameAndIdClass, MappingMember.Type.METHOD)
                .methodType(MethodType.methodType(UUID.class))
                .mappings(
                        entry(Mappings.MOJANG, "id",
                                MinecraftVersions.V21_9),
                        entry(Mappings.YARN_INTERMEDIARY, "comp_4422",
                                MinecraftVersions.V21_9));

        var name = member(NAME, nameAndIdClass, MappingMember.Type.METHOD)
                .methodType(MethodType.methodType(String.class))
                .mappings(
                        entry(Mappings.MOJANG, "name",
                                MinecraftVersions.V21_9),
                        entry(Mappings.YARN_INTERMEDIARY, "comp_4423",
                                MinecraftVersions.V21_9));

        Reflecto.register(constructor, id, name);
    }
    // spotless:on

    public static NameAndId wrap(final @NonNull Object nameAndId) {
        if (!initialized) init();
        if (!nameAndIdClass.clazz().isInstance(nameAndId)) {
            throw new IllegalArgumentException("Object is not an instance of NameAndId");
        }
        var id = (UUID) Reflecto.invoke(NAME_AND_ID, ID, nameAndId);
        var name = (String) Reflecto.invoke(NAME_AND_ID, NAME, nameAndId);
        return new NameAndId(id, name);
    }

    @Override
    public Object unwrap() {
        return Reflecto.invoke(NAME_AND_ID, NAME_AND_ID_CONSTRUCTOR_ID_NAME, this.id, this.name);
    }

    public NameAndId(final @NonNull GameProfile profile) {
        this(getId(profile), getName(profile));
    }

    public NameAndId(final com.mojang.authlib.yggdrasil.response.NameAndId profile) {
        this(profile.id(), profile.name());
    }

    private static final Logger logger = Logger.create("TaterLibLite/NameAndId");

    private static final MethodHandle nameHandle;
    private static final MethodHandle idHandle;

    /**
     * Gets the name from the given GameProfile
     *
     * @param profile the profile
     * @return the name
     */
    private static @NonNull String getName(final @NonNull GameProfile profile) {
        try {
            return (String) nameHandle.invokeExact(profile);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the id from the given GameProfile
     *
     * @param profile the profile
     * @return the id
     */
    private static @NonNull UUID getId(final @NonNull GameProfile profile) {
        try {
            return (UUID) idHandle.invokeExact(profile);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    static {
        final String name;
        final String id;
        // com.mojang:authlib:7.0.0 or newer
        if (Constraint.noLessThan(MinecraftVersions.V21_9).result()) {
            name = "name";
            id = "id";
        } else {
            name = "getName";
            id = "getId";
        }
        try {
            final MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            nameHandle =
                    lookup.findVirtual(
                            GameProfile.class, name, MethodType.methodType(String.class));
            idHandle = lookup.findVirtual(GameProfile.class, id, MethodType.methodType(UUID.class));
        } catch (final NoSuchMethodException | IllegalAccessException e) {
            logger.error("Failed to initialize GameProfile method handles", e);
            throw new RuntimeException(e);
        }
    }
}
