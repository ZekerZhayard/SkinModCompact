package io.github.zekerzhayard.skinmodcompact;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.netease.mc.mod.network.socket.NetworkHandler;

import io.github.zekerzhayard.skinmodcompact.netease.reply.LoadSkinReply;
import io.github.zekerzhayard.skinmodcompact.netease.reply.LoadSkinReplyV2;
import io.github.zekerzhayard.skinmodcompact.netease.reply.SocketHandler;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

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
        bus.register(this);
        return true;
    }
    
    @Subscribe()
    public void postInit(FMLPostInitializationEvent event) {
        NetworkHandler.replyHashMap.put(SocketHandler.SMID, new SocketHandler(NetworkHandler.replyHashMap.get(SocketHandler.SMID)));
        NetworkHandler.replyAsyncHashMap.put(LoadSkinReply.SMID, new LoadSkinReply());
        NetworkHandler.replyAsyncHashMap.put(LoadSkinReplyV2.SMID, new LoadSkinReplyV2());
    }
}
