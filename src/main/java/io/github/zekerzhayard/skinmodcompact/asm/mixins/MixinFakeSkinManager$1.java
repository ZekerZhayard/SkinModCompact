package io.github.zekerzhayard.skinmodcompact.asm.mixins;

import com.mojang.authlib.GameProfile;
import io.github.zekerzhayard.skinmodcompact.asm.mixins.misc.IMixinGameProfile;
import net.minecraft.client.resources.SkinManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(
    targets = "customskinloader.fake.FakeSkinManager$1",
    remap = false
)
public abstract class MixinFakeSkinManager$1 {
    @Final
    @Shadow(aliases = "val$skinAvailableCallback")
    private SkinManager.SkinAvailableCallback skinAvailableCallback;

    @ModifyArg(
        method = "Lcustomskinloader/fake/FakeSkinManager$1;run()V",
        at = @At(
            value = "INVOKE",
            target = "Lcustomskinloader/CustomSkinLoader;loadProfile(Lcom/mojang/authlib/GameProfile;)Ljava/util/Map;"
        ),
        require = 1
    )
    private GameProfile modifyArg$run$0(GameProfile profile) {
        ((IMixinGameProfile) profile).setSkinAvailableCallback(this.skinAvailableCallback);
        return profile;
    }
}
