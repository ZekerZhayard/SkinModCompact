package io.github.zekerzhayard.skinmodcompact.asm.mixins;

import customskinloader.forge.ForgeMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    value = ForgeMod.class,
    remap = false
)
public abstract class MixinForgeMod {
    @Inject(
        method = "Lcustomskinloader/forge/ForgeMod;fingerprintError(Lnet/minecraftforge/fml/common/event/FMLFingerprintViolationEvent;)V",
        at = @At("HEAD"),
        cancellable = true,
        require = 1
    )
    private void inject$fingerprintError$0(CallbackInfo ci) {
        ci.cancel();
    }
}


































