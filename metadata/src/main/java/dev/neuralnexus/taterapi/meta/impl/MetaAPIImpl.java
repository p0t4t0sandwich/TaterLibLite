/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl;

import static dev.neuralnexus.taterapi.util.ReflectionUtil.checkForClass;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.logger.impl.SystemLogger;
import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.meta.ModContainer;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.Side;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.BungeeCordMeta;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.FabricMeta;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.IgniteMeta;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.NeoForgeMeta;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.VanillaMeta;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.VelocityMeta;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.bukkit.BukkitMeta;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.forge.ForgeData;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.sponge.SpongeData;
import dev.neuralnexus.taterapi.meta.impl.version.provider.BungeeCordMCVProvider;
import dev.neuralnexus.taterapi.meta.impl.version.provider.FabricMCVProvider;
import dev.neuralnexus.taterapi.meta.impl.version.provider.ForgeCPWMCVProvider;
import dev.neuralnexus.taterapi.meta.impl.version.provider.ForgeFMLMCVProvider;
import dev.neuralnexus.taterapi.meta.impl.version.provider.ForgeMCFMCVProvider;
import dev.neuralnexus.taterapi.meta.impl.version.provider.NeoForgeMCVProvider;
import dev.neuralnexus.taterapi.meta.impl.version.provider.PaperMCVProvider;
import dev.neuralnexus.taterapi.meta.impl.version.provider.SpongeLegacyMCVProvider;
import dev.neuralnexus.taterapi.meta.impl.version.provider.SpongeModernMCVProvider;
import dev.neuralnexus.taterapi.meta.impl.version.provider.VelocityMCVProvider;

import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/** Class implementing the metadata cache and other useful shortcuts. */
public final class MetaAPIImpl implements MetaAPI {
    private static MetaAPIImpl INSTANCE;

    public static MetaAPIImpl getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MetaAPIImpl();
        }
        return INSTANCE;
    }

    private static Mappings mappings;

    private MetaAPIImpl() {}

    // ----------------------------- Platform -----------------------------

    @Override
    public @NonNull Platform platform() throws NoPlatformException {
        return Platforms.get().stream().findFirst().orElseThrow(NoPlatformException::new);
    }

    @Override
    public boolean isPlatformPresent(@NonNull Platform platform) throws NullPointerException {
        Objects.requireNonNull(platform, "Platform cannot be null");
        return Platforms.get().contains(platform);
    }

    @Override
    public Platform.@NonNull Meta meta() throws NoPlatformException, NoPlatformMetaException {
        return lookup(this.platform())
                .orElseThrow(() -> new NoPlatformMetaException(this.platform()));
    }

    @Override
    public Optional<Platform.Meta> meta(@NonNull Platform platform) throws NullPointerException {
        Objects.requireNonNull(platform, "Platform cannot be null");
        return lookup(platform);
    }

    // ----------------------------- Platform.Meta Getters -----------------------------

    @Override
    public @NonNull Object server() {
        return lookupAll().stream()
                .map(Platform.Meta::server)
                .findFirst()
                .orElseThrow(NullPointerException::new);
    }

    @Override
    public @NonNull Object client() {
        return lookupAll().stream()
                .map(Platform.Meta::client)
                .findFirst()
                .orElseThrow(NullPointerException::new);
    }

    @Override
    public @NonNull Object minecraft() {
        return lookupAll().stream()
                .map(Platform.Meta::minecraft)
                .findFirst()
                .orElseThrow(NullPointerException::new);
    }

    @Override
    public @NonNull Side side() {
        return lookupAll().stream()
                .map(Platform.Meta::side)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    @Override
    public boolean isClient() {
        return lookupAll().stream().anyMatch(Platform.Meta::isClient);
    }

    private MinecraftVersion cachedVersion = MinecraftVersions.UNKNOWN;

    @Override
    public @NonNull MinecraftVersion version() {
        if (cachedVersion != MinecraftVersions.UNKNOWN) {
            return cachedVersion;
        }
        Collection<MinecraftVersion.Provider> providers =
                Platforms.get().stream()
                        .map(MetaAPIImpl::lookupMCV)
                        .flatMap(Collection::stream)
                        .filter(MinecraftVersion.Provider::shouldProvide)
                        .collect(Collectors.toSet());

        // Lazily iterate through providers and exit when the first non-UNKNOWN version is found
        for (final MinecraftVersion.Provider provider : providers) {
            try {
                MinecraftVersion version = provider.get();
                if (version != MinecraftVersions.UNKNOWN) {
                    cachedVersion = version;
                    break;
                }
            } catch (final Throwable e) {
                Logger.create("MetaAPI")
                        .warn(
                                "Failed to get Minecraft version from provider: "
                                        + provider.getClass().getName(),
                                e);
            }
        }
        return cachedVersion;
    }

    @Override
    public boolean isModLoaded(final @NonNull String... nameOrId) throws NullPointerException {
        Objects.requireNonNull(nameOrId, "Name or ID cannot be null");
        return lookupAll().stream().anyMatch(meta -> meta.isModLoaded(nameOrId));
    }

    @Override
    public boolean isModLoaded(final @NonNull Platform platform, final @NonNull String... nameOrId)
            throws NullPointerException {
        Objects.requireNonNull(platform, "Platform cannot be null");
        Objects.requireNonNull(nameOrId, "Name or ID cannot be null");
        return lookup(platform).map(meta -> meta.isModLoaded(nameOrId)).orElse(false);
    }

    @Override
    public boolean areModsLoaded(final @NonNull String... nameOrId) throws NullPointerException {
        Objects.requireNonNull(nameOrId, "Name or ID cannot be null");
        return lookupAll().stream().allMatch(meta -> meta.isModLoaded(nameOrId));
    }

    @Override
    public boolean areModsLoaded(
            final @NonNull Platform platform, final @NonNull String... nameOrId)
            throws NullPointerException {
        Objects.requireNonNull(platform, "Platform cannot be null");
        Objects.requireNonNull(nameOrId, "Name or ID cannot be null");
        return lookup(platform).map(meta -> meta.isModLoaded(nameOrId)).orElse(false);
    }

    // TODO: At some point, it would be nice to have a guaranteed set of version-specific mappings
    // Would allow for more accurate mappings detection, rather than assumptions inflexible to
    // future changes
    @Override
    public @NonNull Mappings mappings() {
        if (mappings == null) {
            MetaAPI api = MetaAPI.instance();
            // Check for proxy
            if (api.isProxy()) {
                mappings = Mappings.NONE;
                // Check for FFAPI, Connector, and Kilt
            } else if (api.version().greaterThan(MinecraftVersions.V21_11)) {
                mappings = Mappings.MOJANG;
            } else if (api.isMixedForgeFabric()) {
                if (api.isModLoaded(Platforms.FABRIC, "kilt")) {
                    mappings = Mappings.YARN_INTERMEDIARY;
                } else if (api.isModLoaded(Platforms.FORGE, "fabric_api", "connector")) {
                    mappings = Mappings.SEARGE;
                }
            } else if (api.isMixedNeoForgeFabric()
                    && api.isModLoaded(Platforms.NEOFORGE, "fabric_api", "connector")) {
                mappings = Mappings.MOJANG;
                // Check NeoForge
            } else if (api.isPlatformPresent(Platforms.NEOFORGE)) {
                if (this.version().is(MinecraftVersions.V20_1)) {
                    mappings = Mappings.SEARGE;
                } else {
                    mappings = Mappings.MOJANG;
                }
                // Check Forge
            } else if (api.isPlatformPresent(Platforms.FORGE)) {
                if (this.version().noGreaterThan(MinecraftVersions.V20_5)) {
                    mappings = Mappings.SEARGE;
                } else {
                    mappings = Mappings.MOJANG;
                }
                // Check Fabric
            } else if (api.isPlatformPresent(Platforms.FABRIC)) {
                // TODO: Add Babric and CursedFabric checks
                if (this.version().lessThan(MinecraftVersions.V14)) {
                    mappings = Mappings.CALAMUS;
                } else if (this.version().noLessThan(MinecraftVersions.V14)) {
                    mappings = Mappings.YARN_INTERMEDIARY;
                }
                // Check SpongeVanilla
            } else if (api.isPlatformPresent(Platforms.SPONGE)) {
                if (this.version().lessThan(MinecraftVersions.V14)) {
                    mappings = Mappings.SEARGE;
                } else {
                    mappings = Mappings.MOJANG;
                }
                // Check Paper
            } else if (api.isPlatformPresent(Platforms.PAPER)
                    && this.version().noLessThan(MinecraftVersions.V20_5)) {
                mappings = Mappings.MOJANG;
                // Check Spigot
            } else if (api.isPlatformPresent(Platforms.SPIGOT)) {
                mappings = Mappings.SPIGOT;
                // Check Bukkit
            } else if (api.isPlatformPresent(Platforms.BUKKIT)) {
                mappings = Mappings.OFFICIAL;
            }
        }
        return Objects.requireNonNull(mappings, "Mappings are null after initialization");
    }

    @Override
    public @NonNull Collection<ModContainer<Object>> mods(final @NonNull Platform platform) {
        Objects.requireNonNull(platform, "Platform cannot be null");
        return lookup(platform).map(Platform.Meta::mods).orElse(Collections.emptyList());
    }

    @Override
    public @NonNull <T> Optional<ModContainer<T>> mod(final @NonNull String modId) {
        Objects.requireNonNull(modId, "Mod ID cannot be null");
        return lookupAll().stream()
                .map(meta -> meta.<T>mod(modId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    @Override
    public @NonNull <T> Optional<ModContainer<T>> mod(
            final @NonNull Platform platform, final @NonNull String modId) {
        Objects.requireNonNull(platform, "Platform cannot be null");
        Objects.requireNonNull(modId, "Mod ID cannot be null");
        return lookup(platform).flatMap(meta -> meta.mod(modId));
    }

    @Override
    public @NonNull Logger logger(final @NonNull String modId) throws NullPointerException {
        Objects.requireNonNull(modId, "Mod ID cannot be null");
        return lookupAll().stream()
                .map(meta -> meta.logger(modId))
                .findFirst()
                .orElse(new SystemLogger(modId));
    }

    /**
     * Get the metadata for the specified platform
     *
     * @param platform The Platform
     * @return The Platform's metadata
     */
    public static Optional<Platform.Meta> lookup(final Platform platform) {
        if (MetaAPI.isNeoForgeBased(platform)) {
            return Optional.of(new NeoForgeMeta());
        } else if (MetaAPI.isForgeBased(platform)) {
            return Optional.ofNullable(ForgeData.create());
        } else if (MetaAPI.isFabricBased(platform)) {
            return Optional.of(new FabricMeta());
        } else if (platform == Platforms.SPONGE) {
            return Optional.ofNullable(SpongeData.create());
        } else if (MetaAPI.isBukkitBased(platform)
                && !platform.equals(Platforms.ARCLIGHT)
                && !platform.equals(Platforms.LUMINARA)) {
            // TODO: Implement lifecycle hook for Arclight late-Bukkit registration
            return Optional.of(new BukkitMeta());
        } else if (MetaAPI.isBungeeCordBased(platform)) {
            return Optional.of(new BungeeCordMeta());
        } else if (platform == Platforms.VELOCITY) {
            return Optional.of(new VelocityMeta());
        } else if (platform == Platforms.IGNITE) {
            return Optional.of(new IgniteMeta());
        } else if (checkForClass("org.spongepowered.asm.service.MixinService")) {
            return Optional.of(new VanillaMeta());
        }
        return Optional.empty();
    }

    /**
     * Get the metadata for the primary platform
     *
     * @return The Platform's metadata
     */
    public static List<Platform.Meta> lookupAll() {
        Set<Platform> platforms = Platforms.get();

        // TODO: Implement lifecycle hook for Arclight late-Bukkit registration
        if (platforms.contains(Platforms.ARCLIGHT) || platforms.contains(Platforms.LUMINARA)) {
            platforms.remove(Platforms.BUKKIT);
            platforms.remove(Platforms.SPIGOT);
            platforms.remove(Platforms.PAPER);
        }
        return platforms.stream()
                .map(MetaAPIImpl::lookup)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public static Collection<MinecraftVersion.Provider> lookupMCV(final Platform platform) {
        if (MetaAPI.isNeoForgeBased(platform)) {
            return Collections.singleton(new NeoForgeMCVProvider());
        } else if (MetaAPI.isForgeBased(platform)) {
            return List.of(
                    new ForgeFMLMCVProvider(),
                    new ForgeMCFMCVProvider(),
                    new ForgeCPWMCVProvider());
        } else if (MetaAPI.isFabricBased(platform)) {
            return Collections.singleton(new FabricMCVProvider());
        } else if (platform == Platforms.SPONGE) {
            return List.of(new SpongeLegacyMCVProvider(), new SpongeModernMCVProvider());
        } else if (MetaAPI.isBukkitBased(platform)) {
            return Collections.singleton(new PaperMCVProvider());
        } else if (MetaAPI.isBungeeCordBased(platform)) {
            return Collections.singleton(new BungeeCordMCVProvider());
        } else if (platform == Platforms.VELOCITY) {
            return Collections.singleton(new VelocityMCVProvider());
        }
        // TODO: Implement lifecycle hook to add providers later
        // Collections.singleton(new VanillaMCVProvider());
        return Collections.emptySet();
    }
}
