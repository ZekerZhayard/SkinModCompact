package io.github.zekerzhayard.skinmodcompact.asm.mixins;

import io.github.zekerzhayard.skinmodcompact.asm.mixins.misc.IMixinThreadDownloadImageData;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ThreadDownloadImageData.class)
public abstract class MixinThreadDownloadImageData implements IMixinThreadDownloadImageData {
    @Final
    @Shadow
    private static Logger logger;

    @Override
    public Logger getLogger() {
        return logger;
    }
}
