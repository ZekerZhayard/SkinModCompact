package io.github.zekerzhayard.skinmodcompact.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.apache.commons.lang3.ClassUtils;

public class ReflectionUtils {
    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameterTypes) throws Exception {
        Constructor<T> constructor = clazz.getDeclaredConstructor(parameterTypes);
        constructor.setAccessible(true);
        return constructor;
    }
    
    public static Object invokeDeclaredMethod(Object instance, String name, Object... args) throws Exception {
        Class<?>[] parameterTypes = ClassUtils.toClass(args);
        Method method = instance.getClass().getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return method.invoke(instance, args);
    }
}
