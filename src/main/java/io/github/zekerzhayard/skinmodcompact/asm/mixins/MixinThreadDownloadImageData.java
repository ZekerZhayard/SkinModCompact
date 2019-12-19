package io.github.zekerzhayard.skinmodcompact.asm.mixins;

import io.github.zekerzhayard.skinmodcompact.config.ModConfig;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThreadDownloadImageData.class)
public abstract class MixinThreadDownloadImageData {
    @Shadow(
        remap = false,
        aliases = "loadPipelined"
    )
    protected abstract void loadPipelined();

    @Shadow
    public static Logger logger;

    @Dynamic("Method ThreadDownloadImageData.loadPipelined is added at runtime by OptiFine.")
    @Inject(
        method = "Lnet/minecraft/client/renderer/ThreadDownloadImageData;loadPipelined()V",
        at = @At(
            value = "INVOKE",
            shift = At.Shift.AFTER,
            target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;)V"
        ),
        remap = false,
        require = 1
    )
    private void inject$loadPipelined$0(CallbackInfo ci) throws InterruptedException {
        logger.info("[SkinModCompact] Retry to download image.");
        Thread.sleep(ModConfig.retryTime);
        this.loadPipelined();
    }
}
