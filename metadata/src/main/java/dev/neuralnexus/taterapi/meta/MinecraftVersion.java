/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta;

import dev.neuralnexus.taterapi.meta.impl.version.meta.MetaStore;
import dev.neuralnexus.taterapi.meta.impl.version.meta.MinecraftVersionMetaImpl;
import dev.neuralnexus.taterapi.meta.version.VersionComparable;
import dev.neuralnexus.taterapi.util.VersionUtil;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public interface MinecraftVersion extends VersionComparable<MinecraftVersion> {
    /**
     * Create a MinecraftVersion from a string.
     *
     * @param version The version to create
     * @return The MinecraftVersion
     */
    static MinecraftVersion of(String version) {
        return MinecraftVersions.of(version);
    }

    /**
     * Get the metadata for the version.
     *
     * @return The metadata
     */
    // Not quite finished yet
    @ApiStatus.Experimental
    default Meta meta() {
        return MetaStore.getMeta(this);
    }

    @Override
    default boolean parseRange(@NonNull String rangeString) {
        Objects.requireNonNull(rangeString, "Range string cannot be null");
        VersionUtil.@Nullable Range range = VersionUtil.Range.parse(rangeString);
        if (range == null) {
            return this.is(rangeString);
        }
        MinecraftVersion start =
                range.start() == null
                        ? MinecraftVersions.UNKNOWN
                        : MinecraftVersions.of(range.start());
        MinecraftVersion end =
                range.end() == null ? MinecraftVersions.UNKNOWN : MinecraftVersions.of(range.end());
        return this.isInRange(range.startInclusive(), start, range.endInclusive(), end);
    }

    /**
     * Represents a provider for Minecraft versions, returns MinecraftVersions.UNKNOWN instead of
     * null if no version can be found
     */
    @FunctionalInterface
    interface Provider extends Supplier<MinecraftVersion> {
        default boolean shouldProvide() {
            return true;
        }

        @Override
        @NonNull MinecraftVersion get();
    }

    /** Represents the metadata for a Minecraft version */
    @ApiStatus.Experimental
    interface Meta {
        Meta UNKNOWN =
                new MinecraftVersionMetaImpl(new Integer[] {0x0, 0b00, 0b000, 0x0, 0x0, 0x0});

        /**
         * Get the protocol version of the Minecraft server. 0 if unknown
         *
         * @return The protocol version
         */
        int protocol();

        /**
         * Get the protocol type of the Minecraft server. UNKNOWN if unknown
         *
         * @return The protocol type
         */
        ProtocolType protocolType();

        /**
         * Get the release type of the Minecraft server. UNKNOWN if unknown
         *
         * @return The release type
         */
        Type type();

        /**
         * Get the data version of the Minecraft server. 0 if unknown
         *
         * @return The data version
         */
        int dataVersion();

        /**
         * Get the resource pack format of the Minecraft server. 0 if unknown
         *
         * @return The resource pack format
         */
        int resourcePackFormat();

        /**
         * Get the data pack format of the Minecraft server. 0 if unknown
         *
         * @return The data version
         */
        int dataPackFormat();
    }

    /** Represents the release type */
    enum Type {
        UNKNOWN,
        SNAPSHOT,
        EXP_SNAPSHOT,
        PRE_RELEASE,
        RELEASE_CANDIDATE,
        RELEASE;

        public static Type fromInt(int i) {
            return switch (i) {
                case 1 -> SNAPSHOT;
                case 2 -> EXP_SNAPSHOT;
                case 3 -> PRE_RELEASE;
                case 4 -> RELEASE_CANDIDATE;
                case 5 -> RELEASE;
                default -> UNKNOWN;
            };
        }
    }
}
