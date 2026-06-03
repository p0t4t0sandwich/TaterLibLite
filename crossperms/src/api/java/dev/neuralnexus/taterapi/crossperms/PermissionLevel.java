package dev.neuralnexus.taterapi.crossperms;

import org.jspecify.annotations.NonNull;

public enum PermissionLevel {
    ALL("all", 0),
    MODERATORS("moderators", 1),
    GAMEMASTERS("gamemasters", 2),
    ADMINS("admins", 3),
    OWNERS("owners", 4);

    private final String name;
    private final int id;

    PermissionLevel(final @NonNull String name, final int id) {
        this.name = name;
        this.id = id;
    }

    public int id() {
        return this.id;
    }

    /**
     * Get the permission level from its id, clamping to the valid range of 0-4
     * @param id The id of the permission level
     * @return The permission level corresponding to the id
     */
    public static @NonNull PermissionLevel fromId(final int id) {
        // TODO: Cross-check against vanilla to allow for extended levels
        final int clampedId = Math.max(0, Math.min(4, id));
        return values()[clampedId];
    }

    /**
     * Get the permission level from its name, ignoring case
     * @param name The name of the permission level
     * @return The permission level corresponding to the name
     */
    public static @NonNull PermissionLevel fromName(final @NonNull String name) {
        // TODO: Cross-check against vanilla to allow for extended levels
        for (final PermissionLevel level : values()) {
            if (level.name.equalsIgnoreCase(name)) {
                return level;
            }
        }
        throw new IllegalArgumentException("No permission level with name: " + name);
    }
}
