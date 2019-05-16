package io.github.zekerzhayard.skinmodcompact;

import com.google.common.eventbus.EventBus;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

public class SkinModCompact extends DummyModContainer {
    public final static String MODID = "skinmodcompact";
    public final static String NAME = "SkinModCompact";
    public final static String VERSION = "@VERSION@";

    public SkinModCompact() {
        super(new ModMetadata());
        ModMetadata md = this.getMetadata();
        md.modId = SkinModCompact.MODID;
        md.name = SkinModCompact.NAME;
        md.version = SkinModCompact.VERSION;
    }

    @Override()
    public boolean registerBus(EventBus bus, LoadController controller) {
        return true;
    }
}
