package io.github.zekerzhayard.skinmodcompact.netease;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import customskinloader.CustomSkinLoader;
import customskinloader.config.SkinSiteProfile;
import customskinloader.loader.ProfileLoader;
import customskinloader.profile.ModelManager0;
import customskinloader.profile.UserProfile;
import customskinloader.utils.HttpTextureUtil;
import io.github.zekerzhayard.skinmodcompact.netease.reflector.ConstructorReflector;
import io.github.zekerzhayard.skinmodcompact.netease.reflector.FieldReflector;
import io.github.zekerzhayard.skinmodcompact.netease.reflector.MethodReflector;
import io.github.zekerzhayard.skinmodcompact.netease.reply.LoadSkinReplyV2;
import io.github.zekerzhayard.skinmodcompact.netease.reply.SocketHandler;
import org.apache.commons.lang3.StringUtils;

public class NeteaseAPILoader implements ProfileLoader.IProfileLoader {
    public static boolean socketClosed = false;
    public static ConcurrentHashMap<String, Object> lockObjectMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, String> nameSkinMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, String> nameCapeMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, ModelManager0.Model> nameSkinMode = new ConcurrentHashMap<>();

    static {
        if (FieldReflector.NetworkHandler_replyHashMap.exist() && FieldReflector.NetworkHandler_replyAsyncHashMap.exist()) {
            FieldReflector.NetworkHandler_replyHashMap.getStaticValue().put(SocketHandler.SMID, new SocketHandler(FieldReflector.NetworkHandler_replyHashMap.getStaticValue().get(SocketHandler.SMID)));
            FieldReflector.NetworkHandler_replyAsyncHashMap.getStaticValue().put(LoadSkinReplyV2.SMID, new LoadSkinReplyV2());
        } else {
            NeteaseAPILoader.socketClosed = true;
        }
    }

    @Override()
    public UserProfile loadProfile(SkinSiteProfile ssp, GameProfile gameProfile) throws Exception {
        if (NeteaseAPILoader.socketClosed) {
            return null;
        }
        String name = gameProfile.getName();
        CustomSkinLoader.logger.info("Player %s start loading skin, ThreadID %s.", name, Thread.currentThread().getName());
        NeteaseAPILoader.lockObjectMap.put(name, new Object());
        Object messageRequest = ConstructorReflector.MessageRequest.newInstance();
        CustomSkinLoader.logger.info("Skin: send message to launcher.");
        MethodReflector.MessageRequest_send.invoke(messageRequest, LoadSkinReplyV2.SMID, new Object[] {
            FieldReflector.GameState_gameid.getStaticValue(), name, gameProfile.getId() != null ? gameProfile.getId().toString() : UUID.nameUUIDFromBytes(("OfflinePlayer:" + gameProfile.getName()).getBytes(Charsets.UTF_8)).toString()
        });
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
        UserProfile userProfile = new UserProfile();
        if (StringUtils.isNotEmpty(localSkinUrl) && !StringUtils.endsWithIgnoreCase(localSkinUrl, "cache\\skin\\skin_10000.png") && !StringUtils.endsWithIgnoreCase(localSkinUrl, "cache\\skin\\skin_10001.png")) {
            File skinFile = new File(localSkinUrl);
            if (skinFile.isFile()) {
                CustomSkinLoader.logger.info("Player %s start loading skin, Url: %s", name, localSkinUrl);
                userProfile.put(NeteaseAPILoader.nameSkinMode.get(name), HttpTextureUtil.getLocalFakeUrl("..\\..\\..\\cache\\skin\\" + skinFile.getName()));
            }
        }
        if (StringUtils.isNotEmpty(localCapeUrl)) {
            File capeFile = new File(localCapeUrl);
            if (capeFile.exists()) {
                CustomSkinLoader.logger.info("Player %s start loading cape, Url: %s", name, localCapeUrl);
                userProfile.capeUrl = HttpTextureUtil.getLocalFakeUrl("..\\..\\..\\cache\\skin\\" + capeFile.getName());
            }
        }
        return userProfile;
    }

    @Override()
    public boolean compare(SkinSiteProfile ssp0, SkinSiteProfile ssp1) {
        return ssp0.type != null && ssp0.type.equals(ssp1.type);
    }

    @Override()
    public String getName() {
        return "NeteaseAPI";
    }

    @Override()
    public void init(SkinSiteProfile ssp) {

    }
}