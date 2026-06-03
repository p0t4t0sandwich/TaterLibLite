package dev.neuralnexus.taterapi.crossperms;

import org.jspecify.annotations.NonNull;

import java.lang.reflect.ParameterizedType;

@FunctionalInterface
public interface HasPermission<P, S> {
    @SuppressWarnings("unchecked")
    default Class<P> permissionType() {
        return (Class<P>)
                ((ParameterizedType) getClass().getGenericInterfaces()[0])
                        .getActualTypeArguments()[0];
    }

    @SuppressWarnings("unchecked")
    default Class<S> subjectType() {
        return (Class<S>)
                ((ParameterizedType) getClass().getGenericInterfaces()[0])
                        .getActualTypeArguments()[1];
    }

    /**
     * Check if a source has a permission, which can be anything depending on the provider, but
     * usually a string or int
     *
     * @param subject The source to check
     * @param permission The permission to check
     * @return If the source has the permission
     */
    @NonNull TriState hasPermission(final @NonNull S subject, final @NonNull P permission);
}
