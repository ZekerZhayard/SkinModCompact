package io.github.zekerzhayard.skinmodcompact.asm.misc;

import net.minecraft.client.resources.SkinManager;

public interface IMixinGameProfile {
    void setSkull(boolean isSkull);

    void setSkinAvailableCallback(SkinManager.SkinAvailableCallback skinAvailableCallback);

    boolean isSkull();

    SkinManager.SkinAvailableCallback getSkinAvailableCallback();
}
