package io.github.zekerzhayard.skinmodcompact.asm;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class MixinConnector implements IMixinConnector {
    @Override
    public void connect() {
        MixinBootstrap.init();
        Mixins.addConfigurations("mixins.skinmodcompact.json");
    }
}
