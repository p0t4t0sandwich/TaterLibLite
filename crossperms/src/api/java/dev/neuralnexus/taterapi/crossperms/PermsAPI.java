package dev.neuralnexus.taterapi.crossperms;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.Wrapped;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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
        return subject -> instance().hasPermission(subject, permission);
    }

    /**
     * Check if a subject has a permission, with a fallback if the permission is not defined
     * @param permission The permission object
     * @param defaultValue The fallback to check if the permission is not defined
     * @return A PermissionCheck that checks the permission and falls back to the fallback if the permission is not defined
     * @param <P> The type of the permission
     * @param <S> The type of the subject
     */
    @SuppressWarnings("unchecked")
    static <P, S> @NonNull PermissionCheck<S> require(final @NonNull P permission, final @NonNull PermissionCheck<S> defaultValue) {
        Objects.requireNonNull(permission, "Permission cannot be null");
        Objects.requireNonNull(defaultValue, "Fallback cannot be null");
        return ((PermissionCheck<S>) require(permission)).orElse(defaultValue);
    }

    /**
     * Check if a subject has a permission, with a fallback if the permission is false or not defined
     * @param permission The permission object
     * @param defaultValue The fallback to check if the permission is not defined
     * @return A PermissionCheck that checks the permission and falls back to the fallback if the permission is false or not defined
     * @param <P> The type of the permission
     * @param <S> The type of the subject
     */
    @SuppressWarnings("unchecked")
    static <P, S> @NonNull PermissionCheck<S> require(final @NonNull P permission, final @NonNull Predicate<S> defaultValue) {
        Objects.requireNonNull(permission, "Permission cannot be null");
        Objects.requireNonNull(defaultValue, "Fallback cannot be null");
        return ((PermissionCheck<S>) require(permission)).orElse(defaultValue);
    }

    /**
     * Check if a subject has a permission, with a fallback if the permission is not defined
     * @param permission The permission object
     * @param defaultValue The fallback to check if the permission is not defined
     * @return A PermissionCheck that checks the permission and falls back to the fallback if the permission is not defined
     * @param <P> The type of the permission
     * @param <S> The type of the subject
     */
    static <P, S> @NonNull PermissionCheck<S> require(final @NonNull P permission, final @NonNull TriState defaultValue) {
        Objects.requireNonNull(permission, "Permission cannot be null");
        Objects.requireNonNull(defaultValue, "Fallback cannot be null");
        return require(permission, s -> defaultValue);
    }

    /**
     * Check if a subject has a permission, with a default value if the permission is not defined
     * @param permission The permission object
     * @param defaultValue The default value to return if the permission is not defined
     * @return A PermissionCheck that checks the permission and returns the default value if the permission is not defined
     * @param <P> The type of the permission
     * @param <S> The type of the subject
     */
    static <P, S> @NonNull PermissionCheck<S> require(final @NonNull P permission, final @Nullable Boolean defaultValue) {
        Objects.requireNonNull(permission, "Permission cannot be null");
        return require(permission, s -> TriState.of(defaultValue));
    }

    /**
     * Check if a subject has a permission, with a default value if the permission is not defined
     * @param permission The permission object
     * @param defaultValue The default value to return if the permission is not defined
     * @return A PermissionCheck that checks the permission and returns the default value if the permission is not defined
     * @param <P> The type of the permission
     * @param <S> The type of the subject
     */
    static <P, S> @NonNull PermissionCheck<S> require(final @NonNull P permission, final boolean defaultValue) {
        Objects.requireNonNull(permission, "Permission cannot be null");
        return require(permission, s -> TriState.of(defaultValue));
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
    <P, S> Collection<HasPermission<P, S>> providers(final @NonNull Class<P> providerType, final @NonNull Class<S> subjectType);

    /**
     * Register a provider
     *
     * @param provider The provider to register
     */
    void registerProvider(PermissionsProvider provider);

    default <P, S> CompletableFuture<TriState> hasPermissionAsync(@NonNull S subject, @NonNull P permission) {
        Objects.requireNonNull(subject, "Subject cannot be null");
        Objects.requireNonNull(permission, "Permission cannot be null");

        if (subject instanceof Wrapped<?> wrapped) {
            return this.hasPermissionAsync(wrapped.unwrap(), permission);
        }

        Collection<HasPermission<P, S>> checks =
                this.providers((Class<P>) permission.getClass(), (Class<S>) subject.getClass());
        for (final HasPermission<P, S> check : checks) {
            if (check.hasPermissionNotAsync(subject, permission).get()) {
                return TriState.TRUE;
            }
        }
        // Check Object.class to see if there is a generic provider
        Collection<HasPermission<P, Object>> objChecks =
                this.providers((Class<P>) permission.getClass(), Object.class);
        for (final HasPermission<P, Object> check : objChecks) {
            if (check.hasPermissionNotAsync(subject, permission).get()) {
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
            if (check.hasPermissionNotAsync(subject, permission).get()) {
                return TriState.TRUE;
            }
        }
        // Check Object.class to see if there is a generic provider
        Collection<HasPermission<P, Object>> objChecks =
                this.providers((Class<P>) permission.getClass(), Object.class);
        for (final HasPermission<P, Object> check : objChecks) {
            if (check.hasPermissionNotAsync(subject, permission).get()) {
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
