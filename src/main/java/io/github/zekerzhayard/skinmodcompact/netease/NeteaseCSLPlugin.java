package io.github.zekerzhayard.skinmodcompact.netease;

import customskinloader.loader.ProfileLoader.IProfileLoader;
import customskinloader.plugin.ICustomSkinLoaderPlugin;

public class NeteaseCSLPlugin implements ICustomSkinLoaderPlugin {
    @Override
    public IProfileLoader getProfileLoader() {
        return new NeteaseAPILoader();
    }
}
