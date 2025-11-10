/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.version;

import static dev.neuralnexus.taterapi.util.FlexVerComparator.compare;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Interface for comparing versions
 *
 * @param <T> The type of version object to compare
 */
public interface VersionComparable<T extends VersionComparable<?>> extends Comparable<T> {
    /**
     * Get the version as a string
     *
     * @return The version as a string
     */
    String version();

    @Override
    default int compareTo(final @NonNull T o) {
        Objects.requireNonNull(o, "version cannot be null");
        return compare(this.version(), o.version());
    }

    /**
     * Check if the version is equal to another version
     *
     * @param version The version to compare
     * @return True if the versions are equal
     */
    default boolean is(final @NonNull String version) {
        Objects.requireNonNull(version, "version cannot be null");
        return compare(this.version(), version) == 0;
    }

    /**
     * Check if the version is equal to another version
     *
     * @param version The version to compare
     * @return True if the versions are equal
     */
    default boolean is(final @NonNull T version) {
        Objects.requireNonNull(version, "version cannot be null");
        return this.compareTo(version) == 0;
    }

    /**
     * Check if the version is in the range of two other versions
     *
     * @param startInclusive Whether the start version is inclusive
     * @param start The start version
     * @param endInclusive Whether the end version is inclusive
     * @param end The end version
     * @return True if the version is in the range
     */
    default boolean isInRange(
            final boolean startInclusive,
            final @NonNull String start,
            final boolean endInclusive,
            final @NonNull String end) {
        Objects.requireNonNull(start, "start cannot be null");
        Objects.requireNonNull(end, "end cannot be null");

        // If start or end is unknown, treat as an unbounded range
        int compareStart = compare(this.version(), start);
        int compareEnd = compare(this.version(), end);
        if (start.equals("unknown")) {
            compareStart = 1;
        }
        if (end.equals("unknown")) {
            compareEnd = -1;
        }
        return (startInclusive ? compareStart >= 0 : compareStart > 0)
                && (endInclusive ? compareEnd <= 0 : compareEnd < 0);
    }

    /**
     * Check if the version is in the range of two other versions
     *
     * @param startInclusive Whether the start version is inclusive
     * @param start The start version
     * @param endInclusive Whether the end version is inclusive
     * @param end The end version
     * @return True if the version is in the range
     */
    default boolean isInRange(
            final boolean startInclusive,
            final @NonNull T start,
            final boolean endInclusive,
            final @NonNull T end) {
        Objects.requireNonNull(start, "start cannot be null");
        Objects.requireNonNull(end, "end cannot be null");

        // If start or end is unknown, treat as an unbounded range
        int compareStart = compare(this.version(), start.version());
        int compareEnd = compare(this.version(), end.version());
        if (start.version().equals("unknown")) {
            compareStart = 1;
        }
        if (end.version().equals("unknown")) {
            compareEnd = -1;
        }
        return (startInclusive ? compareStart >= 0 : compareStart > 0)
                && (endInclusive ? compareEnd <= 0 : compareEnd < 0);
    }

    /**
     * Check if the version is in the range of two other versions
     *
     * @param start The start version
     * @param end The end version
     * @return True if the version is in the range
     */
    default boolean isInRange(final @NonNull String start, final @NonNull String end) {
        Objects.requireNonNull(start, "start cannot be null");
        Objects.requireNonNull(end, "end cannot be null");

        // If start or end is unknown, treat as an unbounded range
        int compareStart = compare(this.version(), start);
        int compareEnd = compare(this.version(), end);
        if (start.equals("unknown")) {
            compareStart = 1;
        }
        if (end.equals("unknown")) {
            compareEnd = -1;
        }
        return compareStart >= 0 && compareEnd <= 0;
    }

    /**
     * Check if the version is in the range of two other versions
     *
     * @param start The start version
     * @param end The end version
     * @return True if the version is in the range
     */
    default boolean isInRange(final @NonNull T start, final @NonNull T end) {
        Objects.requireNonNull(start, "start cannot be null");
        Objects.requireNonNull(end, "end cannot be null");
        // If start or end is unknown, treat as an unbounded range
        int compareStart = compare(this.version(), start.version());
        int compareEnd = compare(this.version(), end.version());
        if (start.version().equals("unknown")) {
            compareStart = 1;
        }
        if (end.version().equals("unknown")) {
            compareEnd = -1;
        }
        return compareStart >= 0 && compareEnd <= 0;
    }

    /**
     * Get if The version of Minecraft the server is running is within the defined range. <br>
     * Strings are read in the format of: <b>(1.17,1.20]</b> or <b>[1.17,)</b> or <b>(,1.20]</b>
     *
     * @param rangeString The range to check
     * @return If The version of Minecraft the server is running is within the defined range
     */
    boolean parseRange(final @NonNull String rangeString);

    /**
     * Check if the version is newer than another version
     *
     * @param version The version to compare
     * @return True if the version is newer
     */
    default boolean isNewerThan(final @NonNull String version) {
        Objects.requireNonNull(version, "version cannot be null");
        return compare(this.version(), version) > 0;
    }

    /**
     * Check if the version is newer than another version
     *
     * @param version The version to compare
     * @return True if the version is newer
     */
    default boolean isNewerThan(final @NonNull T version) {
        Objects.requireNonNull(version, "version cannot be null");
        return this.compareTo(version) > 0;
    }

    /**
     * Check if the version is at least another version
     *
     * @param version The version to compare
     * @return True if the version is at least
     */
    default boolean isAtLeast(final @NonNull String version) {
        Objects.requireNonNull(version, "version cannot be null");
        return compare(this.version(), version) >= 0;
    }

    /**
     * Check if the version is at least another version
     *
     * @param version The version to compare
     * @return True if the version is at least
     */
    default boolean isAtLeast(final @NonNull T version) {
        Objects.requireNonNull(version, "version cannot be null");
        return this.compareTo(version) >= 0;
    }

    /**
     * Check if the version is older than another version
     *
     * @param version The version to compare
     * @return True if the version is older
     */
    default boolean isOlderThan(final @NonNull String version) {
        Objects.requireNonNull(version, "version cannot be null");
        return compare(this.version(), version) < 0;
    }

    /**
     * Check if the version is older than another version
     *
     * @param version The version to compare
     * @return True if the version is older
     */
    default boolean isOlderThan(final @NonNull T version) {
        Objects.requireNonNull(version, "version cannot be null");
        return this.compareTo(version) < 0;
    }

    /**
     * Check if the version is at most another version
     *
     * @param version The version to compare
     * @return True if the version is at most
     */
    default boolean isAtMost(final @NonNull String version) {
        Objects.requireNonNull(version, "version cannot be null");
        return compare(this.version(), version) <= 0;
    }

    /**
     * Check if the version is at most another version
     *
     * @param version The version to compare
     * @return True if the version is at most
     */
    default boolean isAtMost(final @NonNull T version) {
        Objects.requireNonNull(version, "version cannot be null");
        return this.compareTo(version) <= 0;
    }
}
