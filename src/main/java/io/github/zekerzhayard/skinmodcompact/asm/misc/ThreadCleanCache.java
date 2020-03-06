package io.github.zekerzhayard.skinmodcompact.asm.misc;

import customskinloader.CustomSkinLoader;
import customskinloader.utils.HttpRequestUtil;
import customskinloader.utils.HttpTextureUtil;
import org.apache.commons.io.FileUtils;

public class ThreadCleanCache extends Thread {
    public ThreadCleanCache() {
        super("CleanCSLCache");
    }

    @Override
    public void run() {
        try {
            FileUtils.deleteDirectory(HttpRequestUtil.CACHE_DIR);
            FileUtils.deleteDirectory(HttpTextureUtil.getCacheDir());
            CustomSkinLoader.logger.info("Successfully cleaned cache.");
        } catch (Exception e) {
            CustomSkinLoader.logger.warning("Exception occurs while cleaning cache: " + e.toString());
        }
    }
}
