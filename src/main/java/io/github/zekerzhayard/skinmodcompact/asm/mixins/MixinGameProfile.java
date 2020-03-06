package io.github.zekerzhayard.skinmodcompact.asm.mixins;

import com.mojang.authlib.GameProfile;
import io.github.zekerzhayard.skinmodcompact.asm.misc.IMixinGameProfile;
import net.minecraft.client.resources.SkinManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(
    value = GameProfile.class,
    remap = false
)
public abstract class MixinGameProfile implements IMixinGameProfile {
    private boolean isSkull;
    private SkinManager.SkinAvailableCallback skinAvailableCallback;

    @Override
    public void setSkull(boolean isSkull) {
        this.isSkull = isSkull;
    }

    @Override
    public void setSkinAvailableCallback(SkinManager.SkinAvailableCallback skinAvailableCallback) {
        this.skinAvailableCallback = skinAvailableCallback;
    }

    @Override
    public boolean isSkull() {
        return this.isSkull;
    }

    @Override
    public SkinManager.SkinAvailableCallback getSkinAvailableCallback() {
        return this.skinAvailableCallback;
    }
}
