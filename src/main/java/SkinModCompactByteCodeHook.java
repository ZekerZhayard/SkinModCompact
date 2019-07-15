import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import customskinloader.CustomSkinLoader;
import customskinloader.config.SkinSiteProfile;
import customskinloader.loader.ProfileLoader;
import customskinloader.profile.ModelManager0;
import customskinloader.profile.UserProfile;
import customskinloader.utils.HttpRequestUtil;
import customskinloader.utils.HttpTextureUtil;
import io.github.zekerzhayard.skinmodcompact.config.ModConfig;
import io.github.zekerzhayard.skinmodcompact.utils.FilesUtils;
import io.github.zekerzhayard.skinmodcompact.utils.MinecraftUtils;
import io.github.zekerzhayard.skinmodcompact.utils.UserProfileUtils;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.SkinManager.SkinAvailableCallback;
import org.apache.logging.log4j.Logger;

public class SkinModCompactByteCodeHook {
    private static ConcurrentHashMap<String, SkinManager.SkinAvailableCallback> callBackMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<GameProfile, String> profileMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, ThreadPoolExecutor> loaderThreadPools = new ConcurrentHashMap<>();
//    private static ConcurrentHashMap<Integer, ConcurrentLinkedDeque<Runnable>> loadProfileRunnables = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();
    private static ExecutorService threadPool = Executors.newCachedThreadPool();
    
    public static void loadProfileTextures(final GameProfile gameProfile, SkinManager.SkinAvailableCallback skinAvailableCallback) {
        String hashCode = String.valueOf(new Object().hashCode());
        gameProfile.getProperties().put("skinAvailableCallback", new Property("hashCode", hashCode));
        SkinModCompactByteCodeHook.callBackMap.put(hashCode, skinAvailableCallback);
        SkinModCompactByteCodeHook.profileMap.put(gameProfile, hashCode);
        if (!SkinModCompactByteCodeHook.lockMap.containsKey(gameProfile.getName())) {
            SkinModCompactByteCodeHook.lockMap.put(gameProfile.getName(), new ReentrantLock(true));
        }
        SkinModCompactByteCodeHook.threadPool.execute(new Runnable() {
            @Override()
            public void run() {
                try {
                    SkinModCompactByteCodeHook.lockMap.get(gameProfile.getName()).lock();
                    Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = new HashMap<>();
                    map.putAll(CustomSkinLoader.loadProfile(gameProfile));
                    MinecraftUtils.addScheduledTask(new SkinModCompactByteCodeHook.LoadSkinRunnable(gameProfile, map));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    SkinModCompactByteCodeHook.lockMap.get(gameProfile.getName()).unlock();
                }
            }
        });
    }
    
    public static void retryToDownload(Logger logger, String msg, Thread thread) throws InterruptedException {
        logger.error(msg);
        if (msg.contains("java.net.UnknownHostException")) {
            Thread.sleep(1000L);
        }
        logger.info("[SkinModCompact] Retry to download image.");
        thread.run();
    }

    public static boolean cleanDirectory(boolean enableLocalProfileCache) {
        if (!enableLocalProfileCache) {
            new Thread() {
                @Override()
                public void run() {
                    long timeStamp = System.currentTimeMillis();
                    FilesUtils.cleanDirectory(HttpRequestUtil.CACHE_DIR, timeStamp);
                    FilesUtils.cleanDirectory(HttpTextureUtil.getCacheDir(), timeStamp);
                    CustomSkinLoader.logger.info("Cleaning cache complete.");
                }
            }.start();
        }
        return true;
    }
    
    public static UserProfile loadProfile(final UserProfile userProfile, final GameProfile gameProfile) {
        if (ModConfig.isSTVersion) {
            return userProfile;
        }
        for (int j = 0; j <= CustomSkinLoader.config.loadlist.size(); j += 64) {
            final int k = Math.min(CustomSkinLoader.config.loadlist.size() - j, 64);
            final Object lock = new Object();
            // [0]=be loaded; [1]=effective skin; [2]=effective cape; [3]=effective elytra
            final long[] indicators = {0L, -1L >>> 64 - k, -1L >>> 64 - k, -1L >>> 64 - k};
            final String threadName = Thread.currentThread().getName();
            for (int i = 0; i < k; i++) {
                final SkinSiteProfile ssp = CustomSkinLoader.config.loadlist.get(i + j);
                CustomSkinLoader.logger.info((i + j + 1) + "/" + CustomSkinLoader.config.loadlist.size() + " Try to load profile from '" + ssp.name + "'.");
                final ProfileLoader.IProfileLoader loader = ProfileLoader.LOADERS.get(ssp.type.toLowerCase());
                final int loaderPriority = k - i;
                
                if (SkinModCompactByteCodeHook.loaderThreadPools.get(i + j) == null) {
                    synchronized (SkinModCompactByteCodeHook.loaderThreadPools) {
                        if (SkinModCompactByteCodeHook.loaderThreadPools.get(i + j) == null) {
//                            SkinModCompactByteCodeHook.loadProfileRunnables.put(i + j, new ConcurrentLinkedDeque<Runnable>());
                            SkinModCompactByteCodeHook.loaderThreadPools.put(i + j, new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()));
                        }
                    }
                }
                
                final ThreadPoolExecutor loaderThreadPool = SkinModCompactByteCodeHook.loaderThreadPools.get(i + j);
//                final ConcurrentLinkedDeque<Runnable> loadProfileRunnables = SkinModCompactByteCodeHook.loadProfileRunnables.get(i + j);
                Runnable loadProfileRunnable = new Runnable() {
                    @Override()
                    public void run() {
                        Thread.currentThread().setName(threadName);
                        boolean isSkull = threadName.equals(gameProfile.getName() + "'s skull");
                        
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
                            UserProfile oldProfile = UserProfileUtils.clone(userProfile);
                            UserProfileUtils.mix(userProfile, profile, indicators, loaderPriority);
                            indicators[0] = indicators[0] | 1L << loaderPriority - 1;
                            
                            if (!isSkull && !UserProfileUtils.isEquals(oldProfile, userProfile)) {
                                Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = ModelManager0.fromUserProfile(userProfile);
                                if (!CustomSkinLoader.config.enableCape) {
                                    map.remove(MinecraftProfileTexture.Type.CAPE);
                                }
                                MinecraftUtils.addScheduledTask(new SkinModCompactByteCodeHook.LoadSkinRunnable(gameProfile, map));
                            }
                            if (isSkull && UserProfileUtils.isEffective(false, indicators) || indicators[0] == -1L >>> 64 - k || UserProfileUtils.isEffective(CustomSkinLoader.config.forceLoadAllTextures, indicators)) {
                                synchronized (lock) {
                                    lock.notifyAll();
                                }
                            }
                        }
//                        synchronized (loadProfileRunnables) {
//                            if (!loadProfileRunnables.isEmpty()) {
//                                loaderThreadPool.execute(loadProfileRunnables.getFirst());
//                                loadProfileRunnables.removeFirst();
//                            }
//                        }
                    }
                };
//                synchronized (loadProfileRunnables) {
//                    if (loaderThreadPool.getQueue().isEmpty()) {
                        loaderThreadPool.execute(loadProfileRunnable);
//                    } else {
//                        loadProfileRunnables.addFirst(loadProfileRunnable);
//                    }
//                }
            }
            synchronized (lock) {
                try {
                    lock.wait();
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
        return ModConfig.isSTVersion ? size : -1;
    }
    
    public static void mix(UserProfile profile1, UserProfile profile2, GameProfile gameProfile) {
        UserProfile oldProfile = UserProfileUtils.clone(profile1);
        profile1.mix(profile2);
        if (!Thread.currentThread().getName().equals(gameProfile.getName() + "'s skull") && !UserProfileUtils.isEquals(oldProfile, profile1)) {
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = ModelManager0.fromUserProfile(profile1);
            if (!CustomSkinLoader.config.enableCape) {
                map.remove(MinecraftProfileTexture.Type.CAPE);
            }
            MinecraftUtils.addScheduledTask(new SkinModCompactByteCodeHook.LoadSkinRunnable(gameProfile, map));
        }
    }
    
    public static void loadSkull(final Thread thread, final GameProfile gameProfile) {
        if (!SkinModCompactByteCodeHook.lockMap.containsKey(gameProfile.getName())) {
            SkinModCompactByteCodeHook.lockMap.put(gameProfile.getName(), new ReentrantLock(true));
        }
        SkinModCompactByteCodeHook.threadPool.execute(new Runnable() {
            @Override()
            public void run() {
                Thread.currentThread().setName(thread.getName());
                try {
                    SkinModCompactByteCodeHook.lockMap.get(gameProfile.getName()).lock();
                    thread.run();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    SkinModCompactByteCodeHook.lockMap.get(gameProfile.getName()).unlock();
                }
            }
        });
    }
    
    public static String encodeURL(String str) throws MalformedURLException, URISyntaxException {
        URL url = new URL(str);
        return new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null).toString();
    }
    
    private static class LoadSkinRunnable implements Runnable {
        private GameProfile gameProfile;
        private Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map;

        public LoadSkinRunnable(GameProfile gameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map) {
            this.gameProfile = gameProfile;
            this.map = map;
        }
        
        @Override()
        public void run() {
            SkinAvailableCallback skinAvailableCallback = SkinModCompactByteCodeHook.callBackMap.get(this.gameProfile.getProperties().get("skinAvailableCallback").iterator().next().getValue());
            for (Map.Entry<MinecraftProfileTexture.Type, MinecraftProfileTexture> entry : this.map.entrySet()) {
                MinecraftUtils.loadSkin(entry.getValue(), entry.getKey(), skinAvailableCallback);
            }
        }
    }
}
