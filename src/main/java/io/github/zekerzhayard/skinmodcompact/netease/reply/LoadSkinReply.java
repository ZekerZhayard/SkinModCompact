package io.github.zekerzhayard.skinmodcompact.netease.reply;

import com.netease.mc.mod.network.message.reply.Reply;

import customskinloader.CustomSkinLoader;
import io.github.zekerzhayard.skinmodcompact.netease.NeteaseAPILoader;

public class LoadSkinReply extends Reply {
    public static final int SMID = 2049;

    public void handler(String name, String skinPath, String capePath) {
        if (!NeteaseAPILoader.lockObjectMap.containsKey(name)) {
            CustomSkinLoader.logger.info("[Skin]: name:" + name + " do not exist!");
            return;
        }
        synchronized (NeteaseAPILoader.lockObjectMap.get(name)) {
            try {
                NeteaseAPILoader.nameSkinMap.put(name, skinPath);
                NeteaseAPILoader.nameCapeMap.put(name, capePath);
                NeteaseAPILoader.lockObjectMap.get(name).notify();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
