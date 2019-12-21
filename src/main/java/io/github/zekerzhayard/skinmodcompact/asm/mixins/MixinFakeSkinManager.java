package io.github.zekerzhayard.skinmodcompact.asm.mixins;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import customskinloader.Logger;
import customskinloader.fake.FakeSkinManager;
import io.github.zekerzhayard.skinmodcompact.asm.mixins.misc.IMixinGameProfile;
import net.minecraft.client.resources.SkinManager;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
        try {
            FieldUtils.writeDeclaredField(Logger.Level.DEBUG, "display", true, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Inject(
        method = "Lcustomskinloader/fake/FakeSkinManager;loadProfileTextures(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;Z)V",
        at = @At("HEAD"),
        require = 1
    )
    private void inject$loadProfileTextures$0(GameProfile profile, SkinManager.SkinAvailableCallback skinAvailableCallback, boolean requireSecure, CallbackInfo ci) {
        ((IMixinGameProfile) profile).setSkinAvailableCallback(skinAvailableCallback);
    }

    @Inject(
        method = "Lcustomskinloader/fake/FakeSkinManager;loadSkinFromCache(Lcom/mojang/authlib/GameProfile;)Ljava/util/Map;",
        at = @At("HEAD"),
        require = 1
    )
    private void inject$loadSkinFromCache$0(GameProfile profile, CallbackInfoReturnable<Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> ci) {
        ((IMixinGameProfile) profile).setSkull(true);
    }
}
