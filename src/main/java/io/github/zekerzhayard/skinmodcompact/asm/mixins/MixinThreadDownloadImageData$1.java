package io.github.zekerzhayard.skinmodcompact.asm.mixins;

import io.github.zekerzhayard.skinmodcompact.config.ModConfig;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.renderer.ThreadDownloadImageData$1")
@Pseudo
public abstract class MixinThreadDownloadImageData$1 {
    @Final
    @Shadow(aliases = "this$0")
    private ThreadDownloadImageData field_110932_a;

    @Shadow
    public abstract void run();

    @Dynamic
    @Inject(
        method = "Lnet/minecraft/client/renderer/ThreadDownloadImageData$1;run()V",
        at = {
            @At(
                value = "INVOKE",
                shift = At.Shift.AFTER,
                target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;)V",
                remap = false
            ),
            @At(
                value = "INVOKE",
                shift = At.Shift.AFTER,
                target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;Ljava/lang/Throwable;)V",
                remap = false
            )
        },
        require = 1
    )
    private void inject$run$0(CallbackInfo ci) throws InterruptedException {
        ThreadDownloadImageData.logger.info("[SkinModCompact] Retry to download image. (" + this.field_110932_a.imageUrl + ")");
        Thread.sleep(ModConfig.retryTime);
        this.run();
    }
}
