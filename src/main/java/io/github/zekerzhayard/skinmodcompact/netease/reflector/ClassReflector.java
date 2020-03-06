package io.github.zekerzhayard.skinmodcompact.netease.reflector;

public class ClassReflector<T> extends AbstractReflector {
    public static ClassReflector<Object> GameState = new ClassReflector<>("com.netease.mc.mod.network.common.GameState");
    public static ClassReflector<Object> MessageRequest = new ClassReflector<>("com.netease.mc.mod.network.message.request.MessageRequest");
    public static ClassReflector<Object> NetworkHandler = new ClassReflector<>("com.netease.mc.mod.network.socket.NetworkHandler");

    private Class<? extends T> targetClass;

    @SuppressWarnings("unchecked")
    private ClassReflector(String className) {
        try {
            this.targetClass = (Class<? extends T>) Class.forName(className, false, this.getClass().getClassLoader());
            this.exist = true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            this.exist = false;
        }
    }

    public Class<? extends T> getTargetClass() {
        return this.targetClass;
    }
}
