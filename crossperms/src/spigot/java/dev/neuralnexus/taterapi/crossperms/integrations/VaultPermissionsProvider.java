/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@sperrer.ca
 * The project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLib/blob/dev/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.crossperms.integrations;

import dev.neuralnexus.taterapi.crossperms.HasPermission;
import dev.neuralnexus.taterapi.crossperms.PermissionsProvider;
import dev.neuralnexus.taterapi.crossperms.TriState;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.List;

public class VaultPermissionsProvider implements PermissionsProvider {
    private static Permission vault;

    @SuppressWarnings("DataFlowIssue")
    public VaultPermissionsProvider() {
        vault =
                Bukkit.getServer()
                        .getServicesManager()
                        .getRegistration(Permission.class)
                        .getProvider();
    }

    @Override
    public @NonNull String id() {
        return "Vault";
    }

    @Override
    public @NonNull Platform platform() {
        return Platforms.BUKKIT;
    }

    @Override
    public @NonNull Collection<HasPermission<?, ?>> providers() {
        return List.of(new HasPermission<String, CommandSender>() {
            @Override
            public @NonNull TriState hasPermission(final @NonNull CommandSender subject, final @NonNull String permission) {
                return vault.has(subject, permission) ? TriState.TRUE : TriState.DEFAULT;
            }
        });
    }
}
