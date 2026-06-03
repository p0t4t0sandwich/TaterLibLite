/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@sperrer.ca
 * The project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLib/blob/dev/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.crossperms;

import dev.neuralnexus.taterapi.crossperms.mc.WMinecraftServer;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;

import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;

/** NeoForge permissions provider */
public class NeoForgePermissionsProvider implements PermissionsProvider {
    @Override
    public @NonNull String id() {
        return Platforms.NEOFORGE.name();
    }

    @Override
    public @NonNull Platform platform() {
        return Platforms.NEOFORGE;
    }

    @Override
    public @NonNull Collection<HasPermission<?, ?>> providers() {
        return List.of(
                new HasPermission<String, Object>() {
                    @Override
                    public @NonNull TriState hasPermission(@NonNull Object subject, @NonNull String permission) {
                        return playerHasPermission(subject, permission) ? TriState.TRUE : TriState.DEFAULT;
                    }
                });
    }

    public boolean playerHasPermission(Object subject, String permission) {
        return WMinecraftServer.getPlayer(subject)
                .filter(
                        player ->
                                PermissionAPI.getRegisteredNodes().stream()
                                        .filter(node -> node.getType() == PermissionTypes.BOOLEAN)
                                        .filter(node -> node.getNodeName().equals(permission))
                                        .anyMatch(
                                                node ->
                                                        (boolean)
                                                                node.getDefaultResolver()
                                                                        .resolve(
                                                                                player.unwrap(),
                                                                                player.getUUID())))
                .isPresent();
    }
}
