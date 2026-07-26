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

import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.List;

/** Bukkit permissions provider */
public class BukkitPermissionsProvider implements PermissionsProvider {
    @Override
    public @NonNull String id() {
        return Platforms.BUKKIT.name();
    }

    @Override
    public @NonNull Platform platform() {
        return Platforms.BUKKIT;
    }

    @Override
    public @NonNull Collection<HasPermission<?, ?>> handlers() {
        return List.of(
                new HasPermission<Integer, CommandSender>() {
                    @Override
                    public @NonNull TriState hasPermission(final @NonNull CommandSender subject, final @NonNull Integer permission) {
                        return subject.isOp() ? TriState.TRUE : TriState.DEFAULT;
                    }
                },
                new HasPermission<String, CommandSender>() {
                    @Override
                    public @NonNull TriState hasPermission(final @NonNull CommandSender subject, final @NonNull String permission) {
                        return subject.hasPermission(permission) ? TriState.TRUE : TriState.DEFAULT;
                    }
                });
    }
}
