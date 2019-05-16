package io.github.zekerzhayard.skinmodcompact.glitchless.jsonapi;

import java.util.Map;

import com.google.gson.Gson;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import customskinloader.config.SkinSiteProfile;
import customskinloader.loader.JsonAPILoader;
import customskinloader.profile.ModelManager0;
import customskinloader.profile.UserProfile;;

public class GlitchlessAPI implements JsonAPILoader.IJsonAPI {
    @Override()
    public String toJsonUrl(String root, String username) {
        return root + username;
    }

    @Override()
    public UserProfile toUserProfile(String root, String json, boolean local) {
        GlitchlessAPI.GlitchlessApiResponse result = new Gson().fromJson(json, GlitchlessAPI.GlitchlessApiResponse.class);
        if (!result.textures.containsKey(MinecraftProfileTexture.Type.SKIN)) {
            return null;
        }
        return ModelManager0.toUserProfile(result.textures);
    }

    @Override()
    public String getPayload(SkinSiteProfile ssp) {
        return null;
    }

    @Override()
    public String getName() {
        return "GlitchlessAPI";
    }

    public static class GlitchlessApiResponse {
        protected Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures;
    }
}
