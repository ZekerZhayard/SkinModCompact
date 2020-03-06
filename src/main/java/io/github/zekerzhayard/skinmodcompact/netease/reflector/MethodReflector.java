package io.github.zekerzhayard.skinmodcompact.netease.reflector;

import java.lang.reflect.Method;

public class MethodReflector<T> extends AbstractReflector {
    public static MethodReflector<Void> MessageRequest_send = new MethodReflector<>(ClassReflector.MessageRequest, "send", int.class, Object[].class);

    private Method method;

    private MethodReflector(ClassReflector<?> classReflector, String methodName, Class<?>... paramsType) {
        if (classReflector.exist()) {
            try {
                this.method = classReflector.getTargetClass().getMethod(methodName, paramsType);
                this.exist = true;
            } catch (Exception e) {
                e.printStackTrace();
                this.exist = false;
            }
        }
    }

    public T invokeStatic(Object... prarams) {
        return this.invoke(null, prarams);
    }

    @SuppressWarnings("unchecked")
    public T invoke(Object instance, Object... params) {
        try {
            return (T) this.method.invoke(instance, params);
        } catch (Exception e) {
            e.printStackTrace();
            this.exist = false;
        }
        return null;
    }
}
