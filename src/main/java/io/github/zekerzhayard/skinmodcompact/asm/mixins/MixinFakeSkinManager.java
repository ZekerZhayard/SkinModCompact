package io.github.zekerzhayard.skinmodcompact.asm.mixins;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import customskinloader.fake.FakeSkinManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    value = FakeSkinManager.class,
    remap = false
)
public abstract class MixinFakeSkinManager {
    @Mutable
    @Final
    @Shadow
    private static ExecutorService THREAD_POOL;

    @Inject(
        method = "Lcustomskinloader/fake/FakeSkinManager;<clinit>",
        at = @At("TAIL"),
        require = 1
    )
    private static void inject$_clinit_$0(CallbackInfo ci) {
        THREAD_POOL = Executors.newCachedThreadPool();
    }
}
