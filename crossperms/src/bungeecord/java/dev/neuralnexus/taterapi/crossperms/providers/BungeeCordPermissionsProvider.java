/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@sperrer.ca
 * The project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLib/blob/dev/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.crossperms.providers;

import dev.neuralnexus.taterapi.crossperms.HasPermission;
import dev.neuralnexus.taterapi.crossperms.PermissionsProvider;
import dev.neuralnexus.taterapi.crossperms.TriState;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;

import net.md_5.bungee.api.CommandSender;

import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;

/** BungeeCord permissions provider */
public class BungeeCordPermissionsProvider implements PermissionsProvider {
    @Override
    public @NonNull String id() {
        return Platforms.BUNGEECORD.name();
    }

    @Override
    public @NonNull Platform platform() {
        return Platforms.BUNGEECORD;
    }

    @Override
    public @NonNull Collection<HasPermission<?, ?>> handlers() {
        return List.of(
                new HasPermission<String, CommandSender>() {
                    @Override
                    public @NonNull TriState hasPermission(@NonNull CommandSender subject, @NonNull String permission) {
                        return subject.hasPermission(permission) ? TriState.TRUE : TriState.DEFAULT;
                    }
                });
    }
}
