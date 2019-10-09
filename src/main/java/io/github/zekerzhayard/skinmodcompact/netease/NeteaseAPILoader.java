package io.github.zekerzhayard.skinmodcompact.netease;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.netease.mc.mod.network.common.GameState;
import com.netease.mc.mod.network.message.request.MessageRequest;
import com.netease.mc.mod.network.socket.NetworkHandler;
import customskinloader.CustomSkinLoader;
import customskinloader.config.SkinSiteProfile;
import customskinloader.loader.ProfileLoader;
import customskinloader.profile.ModelManager0;
import customskinloader.profile.UserProfile;
import customskinloader.utils.HttpTextureUtil;
import io.github.zekerzhayard.skinmodcompact.netease.reply.LoadSkinReply;
import io.github.zekerzhayard.skinmodcompact.netease.reply.LoadSkinReplyV2;
import io.github.zekerzhayard.skinmodcompact.netease.reply.SocketHandler;
import io.github.zekerzhayard.skinmodcompact.utils.MinecraftUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class NeteaseAPILoader implements ProfileLoader.IProfileLoader {
    public static boolean socketClosed = false;
    public static ConcurrentHashMap<String, Object> lockObjectMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, String> nameSkinMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, String> nameCapeMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Boolean> nameSkinMode = new ConcurrentHashMap<>();
    
    static {
        NetworkHandler.replyHashMap.put(SocketHandler.SMID, new SocketHandler(NetworkHandler.replyHashMap.get(SocketHandler.SMID)));
        NetworkHandler.replyAsyncHashMap.put(LoadSkinReply.SMID, new LoadSkinReply());
        NetworkHandler.replyAsyncHashMap.put(LoadSkinReplyV2.SMID, new LoadSkinReplyV2());
    }

    @Override()
    public UserProfile loadProfile(SkinSiteProfile ssp, GameProfile gameProfile) throws Exception {
        if (NeteaseAPILoader.socketClosed) {
            return null;
        }
        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = new HashMap<>();
        String name = gameProfile.getName();
        CustomSkinLoader.logger.info("Player %s start loading skin, ThreadID %s.", name, Thread.currentThread().getName());
        NeteaseAPILoader.lockObjectMap.put(name, new Object());
        MessageRequest mrq = new MessageRequest();
        CustomSkinLoader.logger.info("Skin: send message to launcher.");
        mrq.send(LoadSkinReply.SMID, GameState.gameid, name);
        mrq.send(LoadSkinReplyV2.SMID, GameState.gameid, name, MinecraftUtils.getPlayerUUID(gameProfile));
        synchronized (NeteaseAPILoader.lockObjectMap.get(name)) {
            try {
                NeteaseAPILoader.lockObjectMap.get(name).wait(10000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        NeteaseAPILoader.lockObjectMap.remove(name);
        String localSkinUrl = NeteaseAPILoader.nameSkinMap.get(name);
        String localCapeUrl = NeteaseAPILoader.nameCapeMap.get(name);
        if (StringUtils.isNotEmpty(localSkinUrl) && !StringUtils.endsWithIgnoreCase(localSkinUrl, "cache\\skin\\skin_10000.png") && !StringUtils.endsWithIgnoreCase(localSkinUrl, "cache\\skin\\skin_10001.png")) {
            CustomSkinLoader.logger.info("Player %s start loading skin, Url: %s", name, localSkinUrl);
            String url = NeteaseAPILoader.copySkinToAsset(new File(localSkinUrl));
            boolean isSlim = NeteaseAPILoader.nameSkinMode.containsKey(name) && NeteaseAPILoader.nameSkinMode.get(name);
            HashMap<String, String> modelMap = new HashMap<>();
            if (isSlim) {
                modelMap.put("model", "slim");
            }
            map.put(MinecraftProfileTexture.Type.SKIN, new MinecraftProfileTexture(url, modelMap));
        }
        if (StringUtils.isNotEmpty(localCapeUrl)) {
            CustomSkinLoader.logger.info("Player %s start loading cape, Url: %s", name, localCapeUrl);
            String url = NeteaseAPILoader.copySkinToAsset(new File(localCapeUrl));
            map.put(MinecraftProfileTexture.Type.CAPE, new MinecraftProfileTexture(url, null));
        }
        return ModelManager0.toUserProfile(map);
    }

    @Override()
    public boolean compare(SkinSiteProfile ssp0, SkinSiteProfile ssp1) {
        return true;
    }

    @Override()
    public String getName() {
        return "NeteaseAPI";
    }

    @Override()
    public void init(SkinSiteProfile ssp) {

    }

    public static String copySkinToAsset(File file) {
        if (file == null) {
            return null;
        }
        try {
            String sha = DigestUtils.sha256Hex(new FileInputStream(file)).toLowerCase();
            File subDir = new File(HttpTextureUtil.getCacheDir(), sha.substring(0, 2));
            subDir.mkdirs();
            FileUtils.copyFile(file, new File(subDir, sha));
            return "http://127.0.0.1/" + sha;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}