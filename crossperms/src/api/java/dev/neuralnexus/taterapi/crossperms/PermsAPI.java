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
     * Get the handlers for a class and a specified permission type
     *
     * @param permissionType The provider type
     * @param subjectType The subject type
     * @return A collection of handlers that match the provider type and subject type
     * @param <P> The type of the permission
     * @param <S> The type of the subject
     */
    <P, S> Collection<HasPermission<P, S>> providers(final @NonNull Class<P> permissionType, final @NonNull Class<S> subjectType);

    /**
     * Register a provider
     *
     * @param provider The provider to register
     */
    void register(PermissionsProvider provider);

    /**
     * Get if a subject has a permission <br>
     * Can be a CommandSender, CommandSourceStack, Entity, GameProfile, String (name), UUID, Player,
     * or any platform implementation of those objects
     *
     * @param subject The subject to check
     * @param permission The permission to check
     * @return TriState of if the subject has the permission, or DEFAULT if the permission is not defined
     * @param <P> The type of the permission
     * @param <S> The type of the subject
     */
    default <P, S> CompletableFuture<TriState> hasPermissionAsync(final @NonNull S subject, final @NonNull P permission) {
        Objects.requireNonNull(subject, "Subject cannot be null");
        Objects.requireNonNull(permission, "Permission cannot be null");

        if (subject instanceof Wrapped<?> wrapped) {
            return this.hasPermissionAsync(wrapped.unwrap(), permission);
        }

        TriState result = TriState.DEFAULT;
        Collection<HasPermission<P, S>> checks =
                this.providers((Class<P>) permission.getClass(), (Class<S>) subject.getClass());
        for (final HasPermission<P, S> check : checks) {
            TriState checkResult = check.hasPermission(subject, permission);
            if (checkResult != TriState.DEFAULT) {
                result = checkResult;
                break;
            }
        }
        // Check Object.class to see if there is a generic provider
        Collection<HasPermission<P, Object>> objChecks =
                this.providers((Class<P>) permission.getClass(), Object.class);
        for (final HasPermission<P, Object> check : objChecks) {
            TriState checkResult = check.hasPermission(subject, permission);
            if (checkResult != TriState.DEFAULT) {
                result = checkResult;
                break;
            }
        }
        return CompletableFuture.completedFuture(result);
    }

    /**
     * Get if a subject has a permission <br>
     * Can be a CommandSender, CommandSourceStack, Entity, GameProfile, String (name), UUID, Player,
     * or any platform implementation of those objects
     *
     * @param subject The subject to check
     * @param permission The permission to check
     * @return TriState of if the subject has the permission, or DEFAULT if the permission is not defined
     * @param <P> The type of the permission
     * @param <S> The type of the subject
     */
    @SuppressWarnings("unchecked")
    default <P, S> @NonNull TriState hasPermission(final @NonNull S subject, final @NonNull P permission) {
        Objects.requireNonNull(subject, "Subject cannot be null");
        Objects.requireNonNull(permission, "Permission cannot be null");

        if (subject instanceof Wrapped<?> wrapped) {
            return this.hasPermission(wrapped.unwrap(), permission);
        }

        TriState result = TriState.DEFAULT;
        Collection<HasPermission<P, S>> checks =
                this.providers((Class<P>) permission.getClass(), (Class<S>) subject.getClass());
        for (final HasPermission<P, S> check : checks) {
            TriState checkResult = check.hasPermission(subject, permission);
            if (checkResult != TriState.DEFAULT) {
                result = checkResult;
                break;
            }
        }
        // Check Object.class to see if there is a generic provider
        Collection<HasPermission<P, Object>> objChecks =
                this.providers((Class<P>) permission.getClass(), Object.class);
        for (final HasPermission<P, Object> check : objChecks) {
            TriState checkResult = check.hasPermission(subject, permission);
            if (checkResult != TriState.DEFAULT) {
                result = checkResult;
                break;
            }
        }
        return result;
    }

    /**
     * Get if a subject has a permission <br>
     * Can be a CommandSender, CommandSourceStack, Entity, GameProfile, String (name), UUID, Player,
     * or any platform implementation of those objects
     *
     * @param subject The subject to check
     * @param permission The permission to check
     * @param defaultPermissionLevel The default permission level
     * @return TriState of if the subject has the permission, falling back to the default permission level
     */
    default @NonNull TriState hasPermission(
            final @NonNull Object subject, final @NonNull String permission, final int defaultPermissionLevel) {
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
