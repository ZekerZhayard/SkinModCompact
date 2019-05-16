package io.github.zekerzhayard.skinmodcompact.glitchless;

import customskinloader.loader.JsonAPILoader;
import io.github.zekerzhayard.skinmodcompact.glitchless.jsonapi.GlitchlessAPI;
import io.github.zekerzhayard.skinmodcompact.utils.ReflectionUtils;
import sun.reflect.ConstructorAccessor;

@SuppressWarnings(value = {"restriction"})
public class GlitchlessAPILoader extends JsonAPILoader {
    public GlitchlessAPILoader() throws Exception {
        super((JsonAPILoader.Type) ((ConstructorAccessor) ReflectionUtils.invokeDeclaredMethod(ReflectionUtils.getConstructor(JsonAPILoader.Type.class, String.class, int.class, JsonAPILoader.IJsonAPI.class), "acquireConstructorAccessor")).newInstance(new Object[] {"GlitchlessAPI", JsonAPILoader.Type.values().length, new GlitchlessAPI()}));
    }
}
