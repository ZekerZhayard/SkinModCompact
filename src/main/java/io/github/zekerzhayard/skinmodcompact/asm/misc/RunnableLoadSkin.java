package io.github.zekerzhayard.skinmodcompact.asm.misc;

import java.util.Map;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import io.github.zekerzhayard.skinmodcompact.utils.MinecraftUtils;
import net.minecraft.client.resources.SkinManager;

public class RunnableLoadSkin implements Runnable {
    private Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map;
    private SkinManager.SkinAvailableCallback skinAvailableCallback;

    public RunnableLoadSkin(Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map, SkinManager.SkinAvailableCallback skinAvailableCallback) {
        this.map = map;
        this.skinAvailableCallback = skinAvailableCallback;
    }

    @Override
    public void run() {
        for (MinecraftProfileTexture.Type type : MinecraftProfileTexture.Type.values()) {
            if (map.containsKey(type)) {
                MinecraftUtils.loadSkin(this.map.get(type), type, this.skinAvailableCallback);
            }
        }
    }
}
