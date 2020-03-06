package io.github.zekerzhayard.skinmodcompact.netease.reflector;

import java.lang.reflect.Field;
import java.util.HashMap;

public class FieldReflector<T> extends AbstractReflector {
    public static FieldReflector<Short> GameState_gameid = new FieldReflector<>(ClassReflector.GameState, "gameid");
    public static FieldReflector<HashMap<Integer, ? super Object>> NetworkHandler_replyHashMap = new FieldReflector<>(ClassReflector.NetworkHandler, "replyHashMap");
    public static FieldReflector<HashMap<Integer, ? super Object>> NetworkHandler_replyAsyncHashMap = new FieldReflector<>(ClassReflector.NetworkHandler, "replyAsyncHashMap");

    private Field field;

    private FieldReflector(ClassReflector<?> classReflector, String fieldName) {
        if (classReflector.exist()) {
            try {
                this.field = classReflector.getTargetClass().getField(fieldName);
                this.exist = true;
            } catch (Exception e) {
                e.printStackTrace();
                this.exist = false;
            }
        }
    }

    public T getStaticValue() {
        return this.getValue(null);
    }

    @SuppressWarnings("unchecked")
    public T getValue(Object instance) {
        try {
            return (T) this.field.get(instance);
        } catch (Exception e) {
            e.printStackTrace();
            this.exist = false;
        }
        return null;
    }
}
