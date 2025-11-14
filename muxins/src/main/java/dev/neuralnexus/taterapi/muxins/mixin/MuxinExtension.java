/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.muxins.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ext.IExtension;
import org.spongepowered.asm.mixin.transformer.ext.ITargetClassContext;

/**
 * This class has been adapted from <a
 * href="https://github.com/Moulberry/MixinConstraints/blob/master/LICENSE">MixinConstraints</a> by
 * Moulberry.
 */
public final class MuxinExtension implements IExtension {
    private final String mixinPackage;
    private final boolean verbose;

    public MuxinExtension(String mixinPackage, boolean verbose) {
        if (!mixinPackage.endsWith(".")) {
            mixinPackage += ".";
        }
        this.mixinPackage = mixinPackage;
        this.verbose = verbose;
    }

    @Override
    public boolean checkActive(MixinEnvironment environment) {
        return false;
    }

    @Override
    public void preApply(ITargetClassContext context) {
        for (Pair<IMixinInfo, ClassNode> pair : MixinHacks.getMixinsFor(context)) {
            if (pair.first().getConfig().getMixinPackage().equals(this.mixinPackage)) {
                MixinTransformer.transform(pair.first(), pair.second(), this.verbose);
            }
        }
    }

    @Override
    public void postApply(ITargetClassContext context) {}

    @Override
    public void export(MixinEnvironment env, String name, boolean force, ClassNode classNode) {}
}
