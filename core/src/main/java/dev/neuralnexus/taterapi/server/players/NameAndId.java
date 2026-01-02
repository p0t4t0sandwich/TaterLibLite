/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.server.players;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

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
public record NameAndId(@NonNull String name, @NonNull UUID id) {
    public NameAndId(final @NonNull GameProfile profile) {
        this(getName(profile), getId(profile));
    }

    private static final Logger logger = Logger.create("TaterLibLite/NameAndId");

    // com.mojang:authlib:7.0.0 or newer
    private static final Constraint V21_9 =
            Constraint.builder().min(MinecraftVersions.V21_9).build();

    private static MethodHandle nameHandle;
    private static MethodHandle idHandle;

    static {
        if (V21_9.result()) {
            nameHandle = null;
            idHandle = null;
        } else {
            try {
                final MethodHandles.Lookup lookup = MethodHandles.lookup();
                //noinspection JavaLangInvokeHandleSignature
                nameHandle =
                        lookup.findVirtual(
                                GameProfile.class, "getName", MethodType.methodType(String.class));
                //noinspection JavaLangInvokeHandleSignature
                idHandle =
                        lookup.findVirtual(
                                GameProfile.class, "getId", MethodType.methodType(UUID.class));
            } catch (final NoSuchMethodException | IllegalAccessException e) {
                logger.error("Failed to initialize GameProfile method handles", e);
            }
        }
    }

    /**
     * Gets the name from the given GameProfile
     *
     * @param profile the profile
     * @return the name
     */
    static @NonNull String getName(final @NonNull GameProfile profile) {
        if (V21_9.result()) {
            return profile.name();
        } else {
            try {
                return (String) nameHandle.invokeExact(profile);
            } catch (final Throwable e) {
                throw new IllegalStateException("Failed to get name from GameProfile", e);
            }
        }
    }

    /**
     * Gets the id from the given GameProfile
     *
     * @param profile the profile
     * @return the id
     */
    static @NonNull UUID getId(final @NonNull GameProfile profile) {
        if (V21_9.result()) {
            return profile.id();
        } else {
            try {
                return (UUID) idHandle.invokeExact(profile);
            } catch (final Throwable e) {
                throw new IllegalStateException("Failed to get id from GameProfile", e);
            }
        }
    }
}
