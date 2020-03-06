package io.github.zekerzhayard.skinmodcompact.netease.reply;

import com.netease.mc.mod.network.message.reply.Reply;
import io.github.zekerzhayard.skinmodcompact.netease.NeteaseAPILoader;
import org.apache.commons.lang3.reflect.MethodUtils;

public class SocketHandler extends Reply {
    public static final int SMID = 1;
    private Object oldReply;

    public SocketHandler(Object oldReply) {
        super();
        this.oldReply = oldReply;
    }

    public void handler() {
        NeteaseAPILoader.socketClosed = true;
        try {
            MethodUtils.invokeMethod(this.oldReply, "handler");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
