package io.github.zekerzhayard.skinmodcompact.netease.reflector;

import java.lang.reflect.Constructor;

public class ConstructorReflector<T> extends AbstractReflector {
    public static ConstructorReflector<Object> MessageRequest = new ConstructorReflector<>(ClassReflector.MessageRequest);

    private Constructor<? extends T> constructor;

    private ConstructorReflector(ClassReflector<T> classReflector, Class<?>... paramsType) {
        if (classReflector.exist()) {
            try {
                this.constructor = classReflector.getTargetClass().getConstructor(paramsType);
                this.exist = true;
            } catch (Exception e) {
                e.printStackTrace();
                this.exist = false;
            }
        }
    }

    public T newInstance(Object... params) {
        try {
            return this.constructor.newInstance(params);
        } catch (Exception e) {
            e.printStackTrace();
            this.exist = false;
        }
        return null;
    }
}
