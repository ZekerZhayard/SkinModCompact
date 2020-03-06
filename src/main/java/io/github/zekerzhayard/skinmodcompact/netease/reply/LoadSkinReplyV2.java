package io.github.zekerzhayard.skinmodcompact.netease.reply;

import com.netease.mc.mod.network.message.reply.Reply;

import customskinloader.CustomSkinLoader;
import customskinloader.profile.ModelManager0;
import io.github.zekerzhayard.skinmodcompact.netease.NeteaseAPILoader;

public class LoadSkinReplyV2 extends Reply {
    public static final int SMID = 2050;

    public void handler(String name, String skinPath, String capePath, int skinModel) {
        if (!NeteaseAPILoader.lockObjectMap.containsKey(name)) {
            CustomSkinLoader.logger.info("[Skin]: name:" + name + " do not exist!");
            return;
        }
        synchronized (NeteaseAPILoader.lockObjectMap.get(name)) {
            try {
                NeteaseAPILoader.nameSkinMap.put(name, skinPath);
                NeteaseAPILoader.nameCapeMap.put(name, capePath);
                NeteaseAPILoader.nameSkinMode.put(name, skinModel == 0 ? ModelManager0.Model.SKIN_DEFAULT : ModelManager0.Model.SKIN_SLIM);
                NeteaseAPILoader.lockObjectMap.get(name).notify();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
