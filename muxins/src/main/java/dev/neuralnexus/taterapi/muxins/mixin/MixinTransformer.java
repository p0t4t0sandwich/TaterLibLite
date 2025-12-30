/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.muxins.mixin;

import dev.neuralnexus.taterapi.muxins.AnnotationChecker;
import dev.neuralnexus.taterapi.muxins.Muxins;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.Iterator;
import java.util.List;

/**
 * This class has been adapted from <a
 * href="https://github.com/Moulberry/MixinConstraints/blob/master/LICENSE">MixinConstraints</a> by
 * Moulberry.
 */
public final class MixinTransformer {
    public static void transform(IMixinInfo info, ClassNode classNode, boolean verbose) {
        if (verbose) {
            Muxins.logger.info("Checking inner mixin constraints for " + info.getClassName());
        }

        if (classNode.visibleAnnotations != null) {
            classNode.visibleAnnotations.removeIf(AnnotationChecker::isConstraintAnnotationNode);
        }

        classNode.fields.removeIf(
                field ->
                        shouldRemoveTarget(
                                "field " + field.name, field.visibleAnnotations, verbose));
        classNode.methods.removeIf(
                method ->
                        shouldRemoveTarget(
                                "method " + method.name, method.visibleAnnotations, verbose));
    }

    private static boolean shouldRemoveTarget(
            String targetName, List<AnnotationNode> annotations, boolean verbose) {
        if (annotations == null) {
            return false;
        }

        boolean remove = false;

        Iterator<AnnotationNode> annotationIterator = annotations.iterator();
        while (annotationIterator.hasNext()) {
            AnnotationNode annotationNode = annotationIterator.next();

            if (!remove && !AnnotationChecker.checkAnnotation(annotationNode, verbose)) {
                if (verbose) {
                    Muxins.logger.warn(
                            "Preventing application of mixin "
                                    + targetName
                                    + " due to failing constraint");
                }

                remove = true;
                annotationIterator.remove();
            } else if (AnnotationChecker.isConstraintAnnotationNode(annotationNode)) {
                annotationIterator.remove();
            }
        }

        return remove;
    }
}
