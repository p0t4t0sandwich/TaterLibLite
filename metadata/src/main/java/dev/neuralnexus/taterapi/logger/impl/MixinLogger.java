package dev.neuralnexus.taterapi.logger.impl;

import dev.neuralnexus.taterapi.logger.Logger;

import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.service.MixinService;

// TODO: Use in Mixin PlatformMeta
@SuppressWarnings("CallToPrintStackTrace")
public final class MixinLogger implements Logger {
    private final ILogger logger;

    public MixinLogger(String modId) {
        this.logger = MixinService.getService().getLogger(modId);
    }

    @Override
    public Object getLogger() {
        return this.logger;
    }

    @Override
    public void info(String message) {
        this.logger.info(message);
    }

    @Override
    public void warn(String message) {
        this.logger.warn(message);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        this.logger.warn(message);
        throwable.printStackTrace();
    }

    @Override
    public void error(String message) {
        this.logger.error(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        this.logger.error(message);
        throwable.printStackTrace();
    }

    @Override
    public void debug(String message) {
        this.logger.debug(message);
    }

    @Override
    public void trace(String message) {
        this.logger.trace(message);
    }

    @Override
    public void fatal(String message) {
        this.logger.error(message);
    }
}
