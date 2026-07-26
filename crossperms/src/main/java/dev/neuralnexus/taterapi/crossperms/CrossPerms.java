/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@sperrer.ca
 * The project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLib/blob/dev/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.crossperms;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.crossperms.impl.integrations.LuckPermsPermissionsProvider;
import dev.neuralnexus.taterapi.crossperms.impl.integrations.PermissionsExPermissionsProvider;
import dev.neuralnexus.taterapi.crossperms.integrations.VaultPermissionsProvider;
import dev.neuralnexus.taterapi.crossperms.providers.BukkitPermissionsProvider;
import dev.neuralnexus.taterapi.crossperms.providers.BungeeCordPermissionsProvider;
import dev.neuralnexus.taterapi.crossperms.providers.FabricPermissionsProvider;
import dev.neuralnexus.taterapi.crossperms.providers.ForgePermissionsProvider_13_17;
import dev.neuralnexus.taterapi.crossperms.providers.ForgePermissionsProvider;
import dev.neuralnexus.taterapi.crossperms.providers.LegacyFabricPermissionsProvider;
import dev.neuralnexus.taterapi.crossperms.providers.SpongePermissionsProvider;
import dev.neuralnexus.taterapi.crossperms.providers.VanillaPermissionsProvider;
import dev.neuralnexus.taterapi.crossperms.providers.VelocityPermissionsProvider;
import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.reflecto.MappingEntry;

import java.util.UUID;

public class CrossPerms {
    private static final Logger logger = Logger.create("CrossPerms");
    private static final CrossPerms INSTANCE = new CrossPerms();

    private CrossPerms() {}

    public static CrossPerms instance() {
        return INSTANCE;
    }

    public Logger logger() {
        return logger;
    }

    /** Initialize CrossPerms <br> */
    public void onInit() {
        MetaAPI meta = MetaAPI.instance();
        PermsAPI api = PermsAPI.instance();
        if (meta.isProxy()) {
            if (meta.isPlatformPresent(Platforms.BUNGEECORD)) {
                api.register(new BungeeCordPermissionsProvider());
            } else if (meta.isPlatformPresent(Platforms.VELOCITY)) {
                api.register(new VelocityPermissionsProvider());
            }
            return;
        }
        this.register();

        // Register vanilla permissions provider
        // Only if the server isn't Bukkit/Spigot, or Paper older than 1.20.5
        if (!meta.isPlatformPresent(Platforms.BUKKIT, Platforms.SPIGOT)
                && !(meta.isPlatformPresent(Platforms.PAPER)
                        && meta.version().noGreaterThan(MinecraftVersions.V20_5))) {
            api.register(new VanillaPermissionsProvider());
        }
        if (meta.isPlatformPresent(Platforms.BUKKIT)) {
            api.register(new BukkitPermissionsProvider());
        }
        if (meta.isPlatformPresent(Platforms.FABRIC)) {
            if (meta.version().noLessThan(MinecraftVersions.V14)
                    && meta.isModLoaded("fabric-permissions-api-v0")) {
                api.register(new FabricPermissionsProvider());
            } else if (meta.isModLoaded("legacy-fabric-permissions-api-v1")) {
                api.register(new LegacyFabricPermissionsProvider());
            }
        }
        if (meta.isPlatformPresent(Platforms.FORGE)) {
            if (meta.version().noLessThan(MinecraftVersions.V18_2)) {
                api.register(new ForgePermissionsProvider());
            } else {
                api.register(new ForgePermissionsProvider_13_17());
            }
        }
        if (meta.isPlatformPresent(Platforms.SPONGE)) {
            api.register(new SpongePermissionsProvider());
        }
    }

    public void onEnable() {
        MetaAPI meta = MetaAPI.instance();
        PermsAPI api = PermsAPI.instance();

        if (meta.isModLoaded("luckperms")) {
            api.register(new LuckPermsPermissionsProvider());
        }
        if (meta.isModLoaded("Vault")) {
            api.register(new VaultPermissionsProvider());
        }
        // TODO: Disabled on Bukkit and BungeeCord due to classloader issues, needs further
        // investigation
        if (meta.isModLoaded("PermissionsEx")
                && !meta.isPlatformPresent(Platforms.BUKKIT, Platforms.BUNGEECORD)) {
            api.register(new PermissionsExPermissionsProvider());
        }
    }

    /** Register mappings */
    private void register() {
        logger.debug("Initializing CrossPerms mappings");

        // Check if the mappings are Official or Spigot, and return early
        if (MetaAPI.instance().mappings().is(Mappings.OFFICIAL)
                || MetaAPI.instance().mappings().is(Mappings.SPIGOT)) {
            return;
        }

        // Entity
        var entity =
                MappingEntry.builder("Entity")
                        .mojang("net.minecraft.world.entity.Entity")
                        .searge("net.minecraft.world.entity.Entity")
                        .legacySearge("net.minecraft.entity.Entity")
                        .mcp("net.minecraft.entity.Entity")
                        .yarnIntermediary("net.minecraft.class_1297")
                        .legacyIntermediary("net.minecraft.class_864");

        // Player
        var player =
                MappingEntry.builder("Player")
                        .mojang("net.minecraft.world.entity.player.Player")
                        .searge("net.minecraft.src.C_1141_")
                        .legacySearge("net.minecraft.entity.player.PlayerEntity")
                        .mcp("net.minecraft.entity.player.PlayerEntity")
                        .yarnIntermediary("net.minecraft.class_1657")
                        .legacyIntermediary("net.minecraft.class_988");

        // ServerPlayer
        var serverPlayer =
                MappingEntry.builder("ServerPlayer")
                        .mojang("net.minecraft.server.level.ServerPlayer")
                        .searge("net.minecraft.server.level.ServerPlayer")
                        .legacySearge("net.minecraft.entity.player.ServerPlayerEntity")
                        .mcp("net.minecraft.entity.player.ServerPlayerEntity")
                        .legacySearge(
                                "net.minecraft.entity.player.EntityPlayerMP",
                                MinecraftVersions.V7,
                                MinecraftVersions.V13_2)
                        .mcp(
                                "net.minecraft.entity.player.EntityPlayerMP",
                                MinecraftVersions.V7,
                                MinecraftVersions.V13_2)
                        .yarnIntermediary("net.minecraft.class_3222")
                        .legacyIntermediary("net.minecraft.class_798");

        // Entity#hasPermissions(int) -> boolean
        var entity_hasPermissions =
                MappingEntry.builder("hasPermissions")
                        .versionRange(MinecraftVersions.V13, MinecraftVersions.UNKNOWN)
                        .parentEntry(serverPlayer)
                        .mojang("hasPermissions")
                        .searge("m_20310_")
                        .searge("m_352356_", MinecraftVersions.V21_2, MinecraftVersions.UNKNOWN)
                        .legacySearge("func_211513_k")
                        .mcp("hasPermissionLevel")
                        .yarnIntermediary("method_5687")
                        .yarnIntermediary(
                                "method_64475", MinecraftVersions.V21_2, MinecraftVersions.UNKNOWN)
                        .legacyIntermediary("method_15592");

        // Player#getGameProfile() -> GameProfile
        var player_getGameProfile =
                MappingEntry.builder("getGameProfile")
                        .parentEntry(serverPlayer)
                        .mojang("getGameProfile")
                        .searge("m_36316_")
                        .legacySearge("func_146103_bH")
                        .mcp("getGameProfile")
                        .yarnIntermediary("method_7334")
                        .legacyIntermediary("method_8429");

        store.registerClass(entity)
                .registerClass(serverPlayer)
                .registerMethod(player_getGameProfile) // Inherited from Player
                .registerMethod(
                        entity_hasPermissions,
                        int.class); // Inherited from Entity (Only until 1.21.1)
        logger.debug("Registered Entity");
        logger.debug("Registered ServerPlayer");
        logger.debug("|-> getGameProfile");
        logger.debug("|-> hasPermissions");

        // SharedSuggestionProvider
        var commandSource =
                MappingEntry.builder("CommandSource")
                        .versionRange(MinecraftVersions.V13, MinecraftVersions.UNKNOWN)
                        .mojang("net.minecraft.commands.SharedSuggestionProvider")
                        .searge("net.minecraft.src.C_3063_")
                        .legacySearge("net.minecraft.command.ISuggestionProvider")
                        .mcp("net.minecraft.command.ISuggestionProvider")
                        .yarnIntermediary("net.minecraft.class_2172")
                        .legacyIntermediary("net.minecraft.class_3965")
                        .yarn("net.minecraft.command.CommandSource");

        // CommandSourceStack
        var commandSender =
                MappingEntry.builder("CommandSender")
                        .mojang(
                                "net.minecraft.commands.CommandSourceStack",
                                MinecraftVersions.V13,
                                MinecraftVersions.UNKNOWN)
                        .searge(
                                "net.minecraft.commands.CommandSourceStack",
                                MinecraftVersions.V13,
                                MinecraftVersions.UNKNOWN)
                        .legacySearge(
                                "net.minecraft.command.CommandSource",
                                MinecraftVersions.V13,
                                MinecraftVersions.UNKNOWN)
                        .mcp(
                                "net.minecraft.command.CommandSource",
                                MinecraftVersions.V13,
                                MinecraftVersions.UNKNOWN)
                        .yarnIntermediary(
                                "net.minecraft.class_2168",
                                MinecraftVersions.V13,
                                MinecraftVersions.UNKNOWN)
                        .legacyIntermediary(
                                "net.minecraft.class_3915",
                                MinecraftVersions.V13,
                                MinecraftVersions.UNKNOWN)
                        // Old CommandSender implementations
                        .legacySearge(
                                "net.minecraft.command.ICommandSender",
                                MinecraftVersions.V7,
                                MinecraftVersions.V12_2)
                        .mcp(
                                "net.minecraft.command.ICommandSender",
                                MinecraftVersions.V7,
                                MinecraftVersions.V12_2)
                        .yarnIntermediary(
                                "net.minecraft.class_2172",
                                MinecraftVersions.V7,
                                MinecraftVersions.V12_2);

        // SharedSuggestionProvider#hasPermissions(int) -> boolean
        var commandSource_hasPermissions =
                MappingEntry.builder("hasPermissions")
                        .versionRange(MinecraftVersions.V13, MinecraftVersions.UNKNOWN)
                        .parentEntry(commandSender)
                        .mojang("hasPermission")
                        .searge("m_6761_")
                        .legacySearge("func_197034_c")
                        .mcp("hasPermissionLevel")
                        .yarnIntermediary("method_9259")
                        .legacyIntermediary("method_17575");

        // CommandSender#getCommandSenderEntity() -> Entity
        var commandSender_getEntity =
                MappingEntry.builder("getEntity")
                        .parentEntry(commandSender)
                        .mojang("getEntity")
                        .searge("m_81373_")
                        .legacySearge("func_197022_f")
                        .mcp("getEntity")
                        .yarnIntermediary("method_9228")
                        .legacySearge(
                                "func_174793_f", MinecraftVersions.V7, MinecraftVersions.V12_2)
                        .legacyIntermediary(
                                "method_10788", MinecraftVersions.V7, MinecraftVersions.V12_2);

        store.registerClass(commandSender)
                .registerMethod(commandSource_hasPermissions, int.class)
                .registerMethod(commandSender_getEntity);
        logger.debug("Registered CommandSourceStack");
        logger.debug("|-> hasPermissions"); // Inherited from SharedSuggestionProvider
        logger.debug("|-> getCommandSenderEntity");
    }
}
