/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta.forge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.logger.impl.ApacheLogger;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.ModContainer;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;
import dev.neuralnexus.taterapi.meta.Side;
import dev.neuralnexus.taterapi.meta.impl.WMinecraft;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.ModContainerImpl;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.ModInfoImpl;
import dev.neuralnexus.taterapi.meta.impl.platform.meta.ModResourceImpl;

import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/** Stores data about the CPW Loader platform */
final class CPWLoaderMeta implements Platform.Meta {
    @Override
    public @NonNull Object server() {
        if (this.side().isServer()) {
            return this.minecraft();
        }
        return this.client();
    }

    @Override
    public @NonNull Object client() {
        return WMinecraft.getInstance();
    }

    @Override
    public @NonNull Object minecraft() {
        if (this.side().isClient() && WMinecraft.hasServer()) {
            return WMinecraft.getServer();
        }
        return FMLCommonHandler.instance().getMinecraftServerInstance();
    }

    @Override
    public @NonNull Side side() {
        return WMinecraft.determineSide(this.isClient());
    }

    @Override
    public boolean isClient() {
        return FMLCommonHandler.instance().getSide().isClient();
    }

    @Override
    public @NonNull MinecraftVersion minecraftVersion() {
        String version = "Unknown";
        try {
            // Reflect to get cpw.mods.fml.common.Loader.MC_VERSION
            version = (String) Loader.class.getField("MC_VERSION").get(null);
        } catch (ReflectiveOperationException ignored) {
        }
        return MinecraftVersion.of(version);
    }

    @Override
    public @NonNull String loaderVersion() {
        return ForgeVersion_7_12.forgeVersion();
    }

    @Override
    public @NonNull String apiVersion() {
        return ForgeVersion_7_12.forgeVersion();
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T> Collection<ModContainer<T>> mods() {
        return Loader.instance().getModList().stream()
                .map(mc -> (ModContainer<T>) this.toContainer(mc))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull <T> Optional<ModContainer<T>> mod(final @NonNull String modId) {
        if (Loader.isModLoaded(modId)) {
            return Loader.instance().getModList().stream()
                    .filter(
                            m ->
                                    m.getModId().equals(modId)
                                            || m.getModId().equals(modId.toLowerCase(Locale.ROOT)))
                    .findFirst()
                    .map(mc -> (ModContainer<T>) this.toContainer(mc));
        }
        return Optional.empty();
    }

    @Override
    public @NonNull Logger logger(final @NonNull String modId) {
        return new ApacheLogger(modId);
    }

    @Override
    public boolean isModLoaded(final @NonNull String... modId) {
        for (final String id : modId) {
            if (Loader.isModLoaded(id) || Loader.isModLoaded(id.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean areModsLoaded(final @NonNull String... modId) {
        for (final String id : modId) {
            if (!Loader.isModLoaded(id) && !Loader.isModLoaded(id.toLowerCase(Locale.ROOT))) {
                return false;
            }
        }
        return true;
    }

    private ModContainer<cpw.mods.fml.common.ModContainer> toContainer(
            final cpw.mods.fml.common.@NonNull ModContainer container) {
        return new ModContainerImpl<>(
                container,
                new ModInfoImpl(
                        container.getModId(),
                        container.getName(),
                        container.getVersion(),
                        Platforms.FORGE),
                new ModResourceImpl(() -> container.getSource().toPath()));
    }
}
