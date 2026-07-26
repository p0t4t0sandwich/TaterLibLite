/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@sperrer.ca
 * The project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLib/blob/dev/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.crossperms.providers;

import dev.neuralnexus.taterapi.crossperms.CrossPerms;
import dev.neuralnexus.taterapi.crossperms.HasPermission;
import dev.neuralnexus.taterapi.crossperms.PermissionsProvider;
import dev.neuralnexus.taterapi.crossperms.TriState;
import dev.neuralnexus.taterapi.crossperms.mc.WMinecraftServer;
import dev.neuralnexus.taterapi.crossperms.mc.WServerPlayer;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;

import net.legacyfabric.fabric.api.permission.v1.PermissibleCommandSource;
import net.legacyfabric.fabric.api.permission.v1.PermissionsApiHolder;
import net.legacyfabric.fabric.api.permission.v1.PlayerPermissionsApi;

import org.jspecify.annotations.NonNull;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/** Legacy Fabric permissions provider */
@SuppressWarnings({"deprecation", "UnstableApiUsage"})
public class LegacyFabricPermissionsProvider implements PermissionsProvider {
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
                new HasPermission<String, PermissibleCommandSource>() {
                    @Override
                    public @NonNull TriState hasPermission(
                            final @NonNull PermissibleCommandSource subject, final @NonNull String permission) {
                        return subject.hasPermission(permission) ? TriState.TRUE : TriState.DEFAULT;
                    }
                },
                new HasPermission<String, Object>() {
                    @Override
                    public @NonNull TriState hasPermission(final @NonNull Object subject, final @NonNull String permission) {
                        return playerObjHasPermission(subject, permission) ? TriState.TRUE : TriState.DEFAULT;
                    }
                });
    }

    public boolean playerObjHasPermission(Object subject, String permission) {
        return WMinecraftServer.getPlayer(subject)
                .map(WServerPlayer::unwrap)
                .filter(player -> this.playerHasPermission(player, permission))
                .isPresent();
    }

    private static final Method HAS_PERMISSION;

    static {
        Method method = null;
        try {
            method =
                    PlayerPermissionsApi.class.getDeclaredMethod(
                            "hasPermission", WServerPlayer.getClazz(), String.class);
        } catch (NoSuchMethodException e) {
            CrossPerms.instance().logger().error("Failed to get method", e);
        }
        HAS_PERMISSION = method;
    }

    /**
     * Get if a player has a permission
     *
     * @param subject The player to check
     * @param permission The permission to check
     * @return If the player has the permission
     */
    private boolean playerHasPermission(Object subject, String permission) {
        if (WServerPlayer.instanceOf(subject)) {
            try {
                return (boolean)
                        HAS_PERMISSION.invoke(
                                PermissionsApiHolder.getPlayerPermissionsApi(),
                                subject,
                                permission);
            } catch (Exception e) {
                CrossPerms.instance().logger().error("Failed to invoke method", e);
            }
        }
        return false;
    }
}
