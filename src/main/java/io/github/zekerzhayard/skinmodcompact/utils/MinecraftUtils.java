package io.github.zekerzhayard.skinmodcompact.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.player.EntityPlayer;

public class MinecraftUtils {
    public static void addScheduledTask(Runnable runnable) {
        Minecraft.getMinecraft().addScheduledTask(runnable);
    }
    
    public static void loadSkin(MinecraftProfileTexture profileTexture, MinecraftProfileTexture.Type type, SkinManager.SkinAvailableCallback skinAvailableCallback) {
        Minecraft.getMinecraft().getSkinManager().loadSkin(profileTexture, type, skinAvailableCallback);
    }
    
    public static String getPlayerUUID(GameProfile profile) {
        return EntityPlayer.getUUID(profile).toString();
    }
}
