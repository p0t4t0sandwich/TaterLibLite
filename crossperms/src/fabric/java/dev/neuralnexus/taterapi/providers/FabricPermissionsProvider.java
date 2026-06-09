/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@sperrer.ca
 * The project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLib/blob/dev/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.providers;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.crossperms.CrossPerms;
import dev.neuralnexus.taterapi.crossperms.HasPermission;
import dev.neuralnexus.taterapi.crossperms.PermissionsProvider;
import dev.neuralnexus.taterapi.crossperms.PermsAPI;
import dev.neuralnexus.taterapi.crossperms.TriState;
import dev.neuralnexus.taterapi.crossperms.mc.WCommandSource;
import dev.neuralnexus.taterapi.crossperms.mc.WEntity;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;

import me.lucko.fabric.api.permissions.v0.Permissions;

import org.jspecify.annotations.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/** Fabric permissions provider */
public class FabricPermissionsProvider implements PermissionsProvider {
    @Override
    public @NonNull String id() {
        return Platforms.FABRIC.name();
    }

    @Override
    public @NonNull Platform platform() {
        return Platforms.FABRIC;
    }

    @Override
    public @NonNull Collection<HasPermission<?, ?>> handlers() {
        return List.of(
                new HasPermission<String, Object>() {
                    @Override
                    public @NonNull TriState hasPermission(final @NonNull Object subject, final @NonNull String permission) {
                        return profileHasPermission(subject, permission) ? TriState.TRUE : TriState.DEFAULT;
                    }
                },
                new HasPermission<String, Object>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public Class<Object> subjectType() {
                        return (Class<Object>) WCommandSource.getClazz();
                    }

                    @Override
                    public @NonNull TriState hasPermission(final @NonNull Object subject, final @NonNull String permission) {
                        return commandSourceHasPermission(subject, permission) ? TriState.TRUE : TriState.DEFAULT;
                    }
                },
                new HasPermission<String, Object>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public Class<Object> subjectType() {
                        return (Class<Object>) WEntity.getClazz();
                    }

                    @Override
                    public @NonNull TriState hasPermission(final @NonNull Object subject, final @NonNull String permission) {
                        return entityHasPermission(subject, permission) ? TriState.TRUE : TriState.DEFAULT;
                    }
                });
    }

    /**
     * Get if a GameProfile has a permission
     *
     * @param subject The GameProfile to check
     * @param permission The permission to check
     * @return If the GameProfile has the permission
     */
    private boolean profileHasPermission(Object subject, String permission) {
        try {
            Optional<GameProfile> profile = PermsAPI.instance().getGameProfile(subject);
            if (profile.isEmpty()) {
                return false;
            }
            return Permissions.check(profile.get(), permission).get();
        } catch (ExecutionException | InterruptedException e) {
            return false;
        }
    }

    private static final Method CHECK_ENTITY;

    static {
        Method checkEntity = null;
        try {
            checkEntity =
                    Permissions.class.getDeclaredMethod("check", WEntity.getClazz(), String.class);
        } catch (NoSuchMethodException e) {
            CrossPerms.instance()
                    .logger()
                    .error("Failed to find check method in Permissions class", e);
        }
        CHECK_ENTITY = checkEntity;
    }

    /**
     * Get if an entity has a permission
     *
     * @param subject The entity to check
     * @param permission The permission to check
     * @return If the entity has the permission
     */
    private static boolean entityHasPermission(Object subject, String permission) {
        try {
            return (boolean) CHECK_ENTITY.invoke(null, subject, permission);
        } catch (IllegalAccessException | InvocationTargetException e) {
            CrossPerms.instance()
                    .logger()
                    .error("Failed to invoke check method in Permissions class", e);
        }
        return false;
    }

    private static final Method CHECK_COMMAND_SOURCE;

    static {
        Method checkSsp = null;
        try {
            checkSsp =
                    Permissions.class.getDeclaredMethod(
                            "check", WCommandSource.getClazz(), String.class);
        } catch (NoSuchMethodException e) {
            CrossPerms.instance()
                    .logger()
                    .error("Failed to find check method in Permissions class", e);
        }
        CHECK_COMMAND_SOURCE = checkSsp;
    }

    /**
     * Get if a CommandSource has a permission
     *
     * @param subject The CommandSource to check
     * @param permission The permission to check
     * @return If the CommandSource has the permission
     */
    private static boolean commandSourceHasPermission(Object subject, String permission) {
        try {
            return (boolean) CHECK_COMMAND_SOURCE.invoke(null, subject, permission);
        } catch (IllegalAccessException | InvocationTargetException e) {
            CrossPerms.instance()
                    .logger()
                    .error("Failed to invoke check method in Permissions class", e);
        }
        return false;
    }
}
