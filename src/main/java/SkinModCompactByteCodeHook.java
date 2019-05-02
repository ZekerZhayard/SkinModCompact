import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.base.Strings;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import customskinloader.CustomSkinLoader;
import customskinloader.config.SkinSiteProfile;
import customskinloader.loader.ProfileLoader;
import customskinloader.profile.ModelManager0;
import customskinloader.profile.UserProfile;
import customskinloader.utils.HttpRequestUtil;
import customskinloader.utils.HttpTextureUtil;
import io.github.zekerzhayard.skinmodcompact.netease.NeteaseAPILoader;
import io.github.zekerzhayard.skinmodcompact.utils.FilesUtils;
import io.github.zekerzhayard.skinmodcompact.utils.UserProfileUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SkinManager;

public class SkinModCompactByteCodeHook {
    private static ConcurrentHashMap<GameProfile, SkinManager.SkinAvailableCallback> callBackMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();
    
    public static void loadProfileTextures(final GameProfile gameProfile, SkinManager.SkinAvailableCallback skinAvailableCallback) {
        SkinModCompactByteCodeHook.callBackMap.put(gameProfile, skinAvailableCallback);
        if (!SkinModCompactByteCodeHook.lockMap.containsKey(gameProfile.getName())) {
            SkinModCompactByteCodeHook.lockMap.put(gameProfile.getName(), new ReentrantLock(true));
        }
        new Thread() {
            @Override()
            public void run() {
                try {
                    SkinModCompactByteCodeHook.lockMap.get(gameProfile.getName()).lock();
                    Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = new HashMap<>();
                    map.putAll(CustomSkinLoader.loadProfile(gameProfile));
                    Minecraft.getMinecraft().addScheduledTask(new SkinModCompactByteCodeHook.LoadSkinRunnable(gameProfile, map));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    SkinModCompactByteCodeHook.lockMap.get(gameProfile.getName()).unlock();
                }
            }
        }.start();
    }

    public static boolean cleanDirectory(boolean b) {
        new Thread() {
            @Override()
            public void run() {
                long timeStamp = System.currentTimeMillis();
                FilesUtils.cleanDirectory(HttpRequestUtil.CACHE_DIR, timeStamp);
                FilesUtils.cleanDirectory(HttpTextureUtil.getCacheDir(), timeStamp);
                CustomSkinLoader.logger.info("Cleaning cache complete.");
            }
        }.start();
        return true;
    }
    
    public static HashMap<String, ProfileLoader.IProfileLoader> addNeteaseAPI(HashMap<String, ProfileLoader.IProfileLoader> loaders) {
        NeteaseAPILoader nal = new NeteaseAPILoader();
        loaders.put(nal.getName().toLowerCase(), nal);
        return loaders;
    }
    
    public static UserProfile loadProfile(final UserProfile userProfile, final GameProfile gameProfile) {
        for (int j = 0; j <= CustomSkinLoader.config.loadlist.size(); j += 64) {
            final int k = Math.min(CustomSkinLoader.config.loadlist.size() - j, 64);
            final Object lock = new Object();
            // [0]=be loaded; [1]=effective skin; [2]=effective cape; [3]=effective elytra
            final long[] indicators = { 0L, -1L>>>64-k, -1L>>>64-k, -1L>>>64-k };
            
            for (int i = 0; i < k; i++) {
                final SkinSiteProfile ssp = CustomSkinLoader.config.loadlist.get(i + j);
                CustomSkinLoader.logger.info((i + j + 1) + "/" + CustomSkinLoader.config.loadlist.size() + " Try to load profile from '" + ssp.name + "'.");
                final ProfileLoader.IProfileLoader loader = ProfileLoader.LOADERS.get(ssp.type.toLowerCase());
                final int loaderPriority = k - i;
                Thread loadThread = new Thread() {
                    @Override()
                    public void run() {
                        UserProfile profile = null;
                        if (loader != null) {
                            try {
                                profile = loader.loadProfile(ssp, gameProfile);
                            } catch (Exception e) {
                                CustomSkinLoader.logger.warning("Exception occurs while loading.");
                                CustomSkinLoader.logger.warning(e);
                                if (e.getCause() != null) {
                                    CustomSkinLoader.logger.warning("Caused By:");
                                    CustomSkinLoader.logger.warning(e.getCause());
                                }
                            }
                        } else {
                            CustomSkinLoader.logger.info("Type '" + ssp.type + "' is not defined.");
                        }
                        synchronized (userProfile) {
                            String oldUrl = Strings.nullToEmpty(userProfile.skinUrl);
                            UserProfileUtils.mix(userProfile, profile, indicators, loaderPriority);
                            indicators[0] = indicators[0] | 1L << loaderPriority - 1;
                            boolean isSkull = Thread.currentThread().getName().endsWith("'s skull");
                            if (!isSkull && userProfile.hasSkinUrl() && !oldUrl.equals(userProfile.skinUrl)) {
                                Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = ModelManager0.fromUserProfile(profile);
                                if (!CustomSkinLoader.config.enableCape) {
                                    map.remove(MinecraftProfileTexture.Type.CAPE);
                                }
                                Minecraft.getMinecraft().addScheduledTask(new SkinModCompactByteCodeHook.LoadSkinRunnable(gameProfile, map));
                            }
                            if (isSkull && UserProfileUtils.isEffective(false, indicators) || indicators[0] == -1L >>> 64 - k || UserProfileUtils.isEffective(CustomSkinLoader.config.forceLoadAllTextures, indicators)) {
                                synchronized (lock) {
                                    lock.notifyAll();
                                }
                            }
                        }
                    }
                };
                loadThread.setName(ssp.name + "-" + Thread.currentThread().getName());
                loadThread.start();
            }
            synchronized (lock) {
                try {
                    lock.wait(1000 * 11);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (indicators[0] != -1L) {
                break;
            }
        }
        return userProfile;
    }
    
    public static int jump(int size) {
        return -1;
    }
    
    public static class LoadSkinRunnable implements Runnable {
        private GameProfile gameProfile;
        private Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map;

        public LoadSkinRunnable(GameProfile gameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map) {
            this.gameProfile = gameProfile;
            this.map = map;
        }
        
        @Override()
        public void run() {
            for (Map.Entry<MinecraftProfileTexture.Type, MinecraftProfileTexture> entry : this.map.entrySet()) {
                Minecraft.getMinecraft().getSkinManager().loadSkin(entry.getValue(), entry.getKey(), SkinModCompactByteCodeHook.callBackMap.get(this.gameProfile));
            }
        }
    }
}
