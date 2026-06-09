/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@sperrer.ca
 * The project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLib/blob/dev/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.crossperms.providers;

import dev.neuralnexus.taterapi.crossperms.HasPermission;
import dev.neuralnexus.taterapi.crossperms.PermissionsProvider;
import dev.neuralnexus.taterapi.crossperms.PermsAPI;
import dev.neuralnexus.taterapi.crossperms.TriState;
import dev.neuralnexus.taterapi.crossperms.mc.WCommandSource;
import dev.neuralnexus.taterapi.crossperms.mc.WEntity;
import dev.neuralnexus.taterapi.crossperms.mc.WPlayerList;
import dev.neuralnexus.taterapi.crossperms.mc.WServerPlayer;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;

import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;

/** Vanilla permissions provider */
public class VanillaPermissionsProvider implements PermissionsProvider {
    @Override
    public @NonNull String id() {
        return Platforms.VANILLA.name();
    }

    @Override
    public @NonNull Platform platform() {
        return Platforms.VANILLA;
    }

    @Override
    public @NonNull Collection<HasPermission<?, ?>> handlers() {
        return List.of(
                new HasPermission<Integer, Object>() {
                    @Override
                    public @NonNull TriState hasPermission(final @NonNull Object subject, final @NonNull Integer permission) {
                        return playerHasPermission(subject, permission) ? TriState.TRUE : TriState.DEFAULT;
                    }
                });
    }

    private boolean playerHasPermission(@NonNull Object subject, int permissionLevel) {
        // TODO: Query Bukkit vanilla objects. It's gonna suck to get all those obsfed mappings
        if (WPlayerList.is13_up) {
            if (WCommandSource.instanceOf(subject)) {
                return WCommandSource.wrap(subject).hasPermission(permissionLevel);
            } else if (WEntity.instanceOf(subject)) {
                return WServerPlayer.wrap(subject).hasPermission(permissionLevel);
            }
        }
        return PermsAPI.instance()
                .getGameProfile(subject)
                .filter(profile -> WPlayerList.hasPermissionLevel(profile, permissionLevel))
                .isPresent();
    }
}
