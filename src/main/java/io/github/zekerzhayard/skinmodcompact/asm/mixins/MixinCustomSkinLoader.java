package io.github.zekerzhayard.skinmodcompact.asm.mixins;

import java.util.Map;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import customskinloader.CustomSkinLoader;
import customskinloader.Logger;
import customskinloader.profile.ModelManager0;
import customskinloader.profile.UserProfile;
import io.github.zekerzhayard.skinmodcompact.asm.misc.IMixinGameProfile;
import io.github.zekerzhayard.skinmodcompact.asm.misc.RunnableLoadSkin;
import io.github.zekerzhayard.skinmodcompact.utils.MinecraftUtils;
import io.github.zekerzhayard.skinmodcompact.utils.UserProfileUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
    value = CustomSkinLoader.class,
    remap = false
)
public abstract class MixinCustomSkinLoader {
    @Final
    @Shadow
    public static Logger logger;

    @Redirect(
        method = "Lcustomskinloader/CustomSkinLoader;loadProfile0(Lcom/mojang/authlib/GameProfile;)Lcustomskinloader/profile/UserProfile;",
        at = @At(
            value = "INVOKE",
            target = "Lcustomskinloader/profile/UserProfile;mix(Lcustomskinloader/profile/UserProfile;)V"
        ),
        require = 1
    )
    private static void redirect$loadProfile0$0(UserProfile profile1, UserProfile profile2, GameProfile gameProfile) {
        if (UserProfileUtils.mix(profile1, profile2) && !((IMixinGameProfile) gameProfile).isSkull()) {
            logger.info("[SkinModCompact] %s's profile loaded.", gameProfile.getName());
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = ModelManager0.fromUserProfile(profile1);
            if (!CustomSkinLoader.config.enableCape) {
                map.remove(MinecraftProfileTexture.Type.CAPE);
            }
            MinecraftUtils.addScheduledTask(new RunnableLoadSkin(map, ((IMixinGameProfile) gameProfile).getSkinAvailableCallback()));
        }
    }

    @Redirect(
        method = "Lcustomskinloader/CustomSkinLoader;loadProfile0(Lcom/mojang/authlib/GameProfile;)Lcustomskinloader/profile/UserProfile;",
        at = @At(
            value = "INVOKE",
            target = "Lcustomskinloader/profile/UserProfile;isFull()Z"
        ),
        require = 1
    )
    private static boolean redirect$loadProfile0$1(UserProfile profile, GameProfile gameProfile) {
        return profile.isFull() || (((IMixinGameProfile) gameProfile).isSkull() && profile.hasSkinUrl());
    }
}
