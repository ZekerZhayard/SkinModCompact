package io.github.zekerzhayard.skinmodcompact.asm.mixins;

import java.util.Map;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import io.github.zekerzhayard.skinmodcompact.asm.mixins.misc.IMixinGameProfile;
import net.minecraft.client.resources.SkinManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SkinManager.class)
public abstract class MixinSkinManager {
    @Inject(
        method = "Lnet/minecraft/client/resources/SkinManager;loadSkinFromCache(Lcom/mojang/authlib/GameProfile;)Ljava/util/Map;",
        at = @At("HEAD"),
        require = 1
    )
    private void inject$loadSkinFromCache$0(GameProfile profile, CallbackInfoReturnable<Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> cir) {
        ((IMixinGameProfile) profile).setSkull(true);
    }
}
