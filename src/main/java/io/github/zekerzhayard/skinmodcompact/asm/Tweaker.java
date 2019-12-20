package io.github.zekerzhayard.skinmodcompact.asm;

import java.io.File;
import java.util.List;

import customskinloader.Logger;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

public class Tweaker implements ITweaker {
    @Override
    public void acceptOptions(List<String> list, File file, File file1, String s) {

    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader launchClassLoader) {
        try {
            FieldUtils.writeDeclaredField(Logger.Level.DEBUG, "display", true, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.skinmodcompact.json");
    }

    @Override
    public String getLaunchTarget() {
        return "";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
