/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@sperrer.ca
 * The project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLib/blob/dev/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.crossperms.providers;

import dev.neuralnexus.taterapi.crossperms.HasPermission;
import dev.neuralnexus.taterapi.crossperms.PermissionsProvider;
import dev.neuralnexus.taterapi.crossperms.PermsAPI;
import dev.neuralnexus.taterapi.crossperms.TriState;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;

import net.minecraftforge.server.permission.PermissionAPI;

import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;

/** Forge permissions provider */
public class ForgePermissionsProvider_13_17 implements PermissionsProvider {
    @Override
    public @NonNull String id() {
        return Platforms.FORGE.name();
    }

    @Override
    public @NonNull Platform platform() {
        return Platforms.FORGE;
    }

    @Override
    public @NonNull Collection<HasPermission<?, ?>> providers() {
        return List.of(
                new HasPermission<String, Object>() {
                    @Override
                    public @NonNull TriState hasPermission(@NonNull Object subject, @NonNull String permission) {
                        return profileHasPermission(subject, permission) ? TriState.TRUE : TriState.DEFAULT;
                    }
                });
    }

    private boolean profileHasPermission(Object subject, String permission) {
        return PermsAPI.instance()
                .getGameProfile(subject)
                .filter(
                        profile ->
                                PermissionAPI.getPermissionHandler()
                                        .hasPermission(profile, permission, null))
                .isPresent();
    }
}
