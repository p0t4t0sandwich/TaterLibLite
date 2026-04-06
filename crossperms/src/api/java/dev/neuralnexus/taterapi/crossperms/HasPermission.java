package dev.neuralnexus.taterapi.crossperms;

import org.jspecify.annotations.NonNull;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

public abstract class HasPermission<P, S> {
    @SuppressWarnings("unchecked")
    public Class<P> permissionType() {
        return (Class<P>) Arrays.stream(getClass().getGenericInterfaces())
                .filter(t -> t instanceof ParameterizedType)
                .map(ParameterizedType.class::cast)
                .filter(t -> t.getRawType().equals(HasPermission.class))
                .findFirst()
                .map(t -> t.getActualTypeArguments()[0])
                .orElseThrow();
    }

    @SuppressWarnings("unchecked")
    public Class<S> subjectType() {
        return (Class<S>) Arrays.stream(getClass().getGenericInterfaces())
                .filter(t -> t instanceof ParameterizedType)
                .map(ParameterizedType.class::cast)
                .filter(t -> t.getRawType().equals(HasPermission.class))
                .findFirst()
                .map(t -> t.getActualTypeArguments()[1])
                .orElseThrow();
    }

    /**
     * Check if a source has a permission, which can be anything depending on the provider, but
     * usually a string or int
     *
     * @param subject The source to check
     * @param permission The permission to check
     * @return If the source has the permission
     */
    public abstract @NonNull TriState hasPermission(final @NonNull S subject, final @NonNull P permission);
}
