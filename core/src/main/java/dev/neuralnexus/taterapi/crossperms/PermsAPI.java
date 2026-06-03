package dev.neuralnexus.taterapi.crossperms;

import com.mojang.authlib.GameProfile;
import dev.neuralnexus.taterapi.Wrapped;

import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/** Permissions API */
public interface PermsAPI {
    PermissionCheck<?> ALL = PermsAPI.require(PermissionLevel.ALL);
    PermissionCheck<?> MODERATORS = PermsAPI.require(PermissionLevel.MODERATORS);
    PermissionCheck<?> GAMEMASTERS = PermsAPI.require(PermissionLevel.GAMEMASTERS);
    PermissionCheck<?> ADMINS = PermsAPI.require(PermissionLevel.ADMINS);
    PermissionCheck<?> OWNERS = PermsAPI.require(PermissionLevel.OWNERS);

    /** Get the instance of the API */
    static @NonNull PermsAPI instance() {
        return PermsAPIImpl.getInstance();
    }

    /**
     * Check if a subject has a permission
     *
     * @param permission The permission object
     * @return A PermissionCheck that checks the permission
     * @param <P> The type of the permission
     * @param <S> The type of the subject
     */
    static <P, S> @NonNull PermissionCheck<S> require(final @NonNull P permission) {
        Objects.requireNonNull(permission, "Permission cannot be null");
        return subject -> PermsAPIImpl.getInstance().hasPermission(subject, permission);
    }

    /**
     * Check if a subject has a permission, with a fallback if the permission is not defined
     * @param permission The permission object
     * @param fallback The fallback to check if the permission is not defined
     * @return A PermissionCheck that checks the permission and falls back to the fallback if the permission is not defined
     * @param <P> The type of the permission
     * @param <S> The type of the subject
     */
    static <P, S> @NonNull PermissionCheck<S> require(final @NonNull P permission, final @NonNull PermissionCheck<S> fallback) {
        Objects.requireNonNull(permission, "Permission cannot be null");
        Objects.requireNonNull(fallback, "Fallback cannot be null");
        return ((PermissionCheck<S>) (subject ->
                PermsAPIImpl.getInstance().hasPermission(subject, permission)))
                .or(fallback);
    }

    /**
     * Check if a subject has a permission, with a fallback if the permission is not defined
     * @param permission The permission object
     * @param fallback The fallback to check if the permission is not defined
     * @return A PermissionCheck that checks the permission and falls back to the fallback if the permission is not defined
     * @param <P> The type of the permission
     * @param <S> The type of the subject
     */
    static <P, S> @NonNull PermissionCheck<S> require(final @NonNull P permission, final @NonNull Predicate<S> fallback) {
        Objects.requireNonNull(permission, "Permission cannot be null");
        Objects.requireNonNull(fallback, "Fallback cannot be null");
        return ((PermissionCheck<S>) (subject ->
                PermsAPIImpl.getInstance().hasPermission(subject, permission)))
                .or(fallback);
    }

    /**
     * Get the providers for a class and a specified permission type
     *
     * @param providerType The provider type
     * @param subjectType The subject type
     * @return A collection of providers that match the provider type and subject type
     * @param <P> The type of the permission
     * @param <S> The type of the subject
     */
    <P, S> Collection<HasPermission<P, S>> providers(Class<P> providerType, Class<S> subjectType);

    /**
     * Register a provider
     *
     * @param provider The provider to register
     */
    void registerProvider(PermissionsProvider provider);

    /**
     * Get if a subject has a permission <br>
     * Can be a CommandSender, CommandSourceStack, Entity, GameProfile, String (name), UUID, Player,
     * or any platform implementation of those objects
     *
     * @param subject The subject to check
     * @param permission The permission to check
     * @return If the subject has the permission
     * @param <P> The type of the permission
     * @param <S> The type of the subject
     */
    @SuppressWarnings("unchecked")
    default <P, S> @NonNull TriState hasPermission(@NonNull S subject, @NonNull P permission) {
        Objects.requireNonNull(subject, "Subject cannot be null");
        Objects.requireNonNull(permission, "Permission cannot be null");

        if (subject instanceof Wrapped<?> wrapped) {
            return this.hasPermission(wrapped.unwrap(), permission);
        }

        Collection<HasPermission<P, S>> checks =
                this.providers((Class<P>) permission.getClass(), (Class<S>) subject.getClass());
        for (final HasPermission<P, S> check : checks) {
            if (check.hasPermission(subject, permission).get()) {
                return TriState.TRUE;
            }
        }
        // Check Object.class to see if there is a generic provider
        Collection<HasPermission<P, Object>> objChecks =
                this.providers((Class<P>) permission.getClass(), Object.class);
        for (final HasPermission<P, Object> check : objChecks) {
            if (check.hasPermission(subject, permission).get()) {
                return TriState.TRUE;
            }
        }
        return TriState.FALSE;
    }

    /**
     * Get if a subject has a permission <br>
     * Can be a CommandSender, CommandSourceStack, Entity, GameProfile, String (name), UUID, Player,
     * or any platform implementation of those objects
     *
     * @param subject The subject to check
     * @param permission The permission to check
     * @param defaultPermissionLevel The default permission level
     * @return If the subject has the permission
     */
    default @NonNull TriState hasPermission(
            @NonNull Object subject, @NonNull String permission, int defaultPermissionLevel) {
        return TriState.of(this.hasPermission(subject, permission).get() ||
                this.hasPermission(subject, defaultPermissionLevel).get());
    }

    /**
     * Get the GameProfile of a subject
     *
     * @param subject The subject to get the GameProfile of
     * @return The GameProfile of the subject
     */
    default Optional<GameProfile> getGameProfile(@NonNull Object subject) {
        Objects.requireNonNull(subject, "Subject cannot be null");
        if (subject instanceof GameProfile profile) {
            return Optional.of(profile);
        }
        // TODO: Fix this
        //return WMinecraftServer.getPlayer(subject).map(WServerPlayer::getGameProfile);
        return Optional.empty();
    }
}
