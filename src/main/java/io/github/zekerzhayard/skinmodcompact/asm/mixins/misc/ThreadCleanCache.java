package io.github.zekerzhayard.skinmodcompact.asm.mixins.misc;

import customskinloader.CustomSkinLoader;
import customskinloader.utils.HttpRequestUtil;
import customskinloader.utils.HttpTextureUtil;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.commons.io.FileUtils;

public class ThreadCleanCache extends Thread {
    public ThreadCleanCache() {
        super("CleanCSLCache");
    }

    @Override
    public void run() {
        if (!(this.getClass().getClassLoader() instanceof LaunchClassLoader)) {
            try {
                Class<?> cl = Launch.classLoader.loadClass(this.getClass().getName());
                cl.getMethod("run").invoke(cl.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            FileUtils.deleteDirectory(HttpRequestUtil.CACHE_DIR);
            FileUtils.deleteDirectory(HttpTextureUtil.getCacheDir());
            CustomSkinLoader.logger.info("Successfully cleaned cache.");
        } catch (Exception e) {
            CustomSkinLoader.logger.warning("Exception occurs while cleaning cache: " + e.toString());
        }
    }
}
