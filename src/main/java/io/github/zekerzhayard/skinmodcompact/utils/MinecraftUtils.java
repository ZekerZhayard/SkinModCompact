package io.github.zekerzhayard.skinmodcompact.utils;

import java.net.Proxy;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import customskinloader.fake.itf.IFakeMinecraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SkinManager;

public class MinecraftUtils {
    public static void addScheduledTask(Runnable runnable) {
        ((IFakeMinecraft) Minecraft.getMinecraft()).execute(runnable);
    }

    public static void loadSkin(MinecraftProfileTexture profileTexture, MinecraftProfileTexture.Type type, SkinManager.SkinAvailableCallback skinAvailableCallback) {
        Minecraft.getMinecraft().getSkinManager().loadSkin(profileTexture, type, skinAvailableCallback);
    }

    public static Proxy getProxy() {
        return Minecraft.getMinecraft().getProxy();
    }
}
