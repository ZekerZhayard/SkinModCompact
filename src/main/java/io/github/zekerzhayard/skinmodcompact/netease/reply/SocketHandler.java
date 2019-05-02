package io.github.zekerzhayard.skinmodcompact.netease.reply;

import org.apache.commons.lang3.reflect.MethodUtils;

import com.netease.mc.mod.network.message.reply.MessageReply;
import com.netease.mc.mod.network.message.reply.Reply;

import io.github.zekerzhayard.skinmodcompact.netease.NeteaseAPILoader;

public class SocketHandler extends Reply {
    public static final int SMID = 1;
    private MessageReply oldReply;

    public SocketHandler(MessageReply oldReply) {
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
