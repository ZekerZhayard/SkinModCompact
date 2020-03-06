package io.github.zekerzhayard.skinmodcompact.asm.mixins;

import customskinloader.config.Config;
import io.github.zekerzhayard.skinmodcompact.asm.misc.ThreadCleanCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
    value = Config.class,
    remap = false
)
public abstract class MixinConfig {
    @Redirect(
        method = "Lcustomskinloader/config/Config;loadConfig0()Lcustomskinloader/config/Config;",
        at = @At(
            value = "FIELD",
            target = "Lcustomskinloader/config/Config;enableLocalProfileCache:Z"
        ),
        require = 1
    )
    private static boolean redirect$loadConfig0$0(Config config) {
        if (!config.enableLocalProfileCache) {
            Runtime.getRuntime().addShutdownHook(new ThreadCleanCache());
        }
        return true;
    }
}
