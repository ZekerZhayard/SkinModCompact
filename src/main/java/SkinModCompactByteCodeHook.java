import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import customskinloader.CustomSkinLoader;
import customskinloader.loader.ProfileLoader;
import customskinloader.profile.ModelManager0;
import customskinloader.profile.UserProfile;
import customskinloader.utils.HttpRequestUtil;
import customskinloader.utils.HttpTextureUtil;
import io.github.zekerzhayard.skinmodcompact.netease.NeteaseAPILoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SkinManager;

public class SkinModCompactByteCodeHook {
	private static long ctm = System.currentTimeMillis();
	private static ConcurrentHashMap<GameProfile, SkinManager.SkinAvailableCallback> callBackMap = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<String, Object> nameMap = new ConcurrentHashMap<>();
	
	public static void loadProfileTextures(final GameProfile gameProfile, SkinManager.SkinAvailableCallback skinAvailableCallback) {
		SkinModCompactByteCodeHook.callBackMap.put(gameProfile, skinAvailableCallback);
		final Object object = new Object();
		SkinModCompactByteCodeHook.nameMap.put(gameProfile.getName(), object);
		new Thread() {
			@Override()
			public void run() {
				Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = new HashMap<>();
				map.putAll(CustomSkinLoader.loadProfile(gameProfile));
				if (object.equals(SkinModCompactByteCodeHook.nameMap.get(gameProfile.getName()))) {
					Minecraft.getMinecraft().addScheduledTask(new SkinModCompactByteCodeHook.LoadSkinRunnable(gameProfile, map));
				}
			}
		}.start();
	}

    public static boolean cleanDirectory(boolean b) {
        new Thread() {
            @Override()
            public void run() {
            	SkinModCompactByteCodeHook.cleanDirectory(HttpRequestUtil.CACHE_DIR);
            	SkinModCompactByteCodeHook.cleanDirectory(HttpTextureUtil.getCacheDir());
            	CustomSkinLoader.logger.info("Cleaning cache complete.");
            }
        }.start();
        return true;
    }
    
    public static void cleanDirectory(File directory) {
        FileUtils.listFiles(directory, new AbstractFileFilter() {
            @Override()
            public boolean accept(File file) {
            	try {
            		if (Files.readAttributes(file.toPath(), BasicFileAttributes.class).creationTime().to(TimeUnit.MILLISECONDS) < SkinModCompactByteCodeHook.ctm) {
                        FileUtils.forceDelete(file);
                        if (file.getParentFile().list().length == 0) {
                            FileUtils.forceDelete(file.getParentFile());
                        }
            		}
            	} catch (Exception e) {
            		CustomSkinLoader.logger.warning("Exception occurs while cleaning cache: " + e.toString());
            	}
            	return false;
            }
        }, TrueFileFilter.INSTANCE);
    }
	
    public static HashMap<String, ProfileLoader.IProfileLoader> addNeteaseAPI(HashMap<String, ProfileLoader.IProfileLoader> loaders) {
    	NeteaseAPILoader nal = new NeteaseAPILoader();
    	loaders.put(nal.getName().toLowerCase(), nal);
        return loaders;
    }
    
    public static boolean profileCacheCheck(boolean isReady) {
		//try {
		//	FieldUtils.writeDeclaredField(ReflectionHelper.findMethod(ProfileCache.class, null, new String[] {"getCachedProfile"}, String.class).invoke(FieldUtils.readDeclaredStaticField(CustomSkinLoader.class, "profileCache", true), MinecraftUtil.getCredential(gameProfile)), "expiryTime", 0L, true);
		//} catch (Exception e) {
		//	e.printStackTrace();
		//}
    	return false;
    }
	
	public static UserProfile loadSkin(UserProfile userProfile, final GameProfile gameProfile) {
		Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = ModelManager0.fromUserProfile(userProfile);
        Minecraft.getMinecraft().addScheduledTask(new SkinModCompactByteCodeHook.LoadSkinRunnable(gameProfile, map));
        if (Thread.currentThread().getName().endsWith("'s skull") && userProfile.hasSkinUrl()) {
        	return new UserProfile() {
        		{
        			this.skinUrl = "http://example.com/";
        			this.capeUrl = "http://example.com/";
        		}
        	};
        }
        return userProfile;
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
