package io.github.zekerzhayard.skinmodcompact.asm;

import java.util.Map;

import io.github.zekerzhayard.skinmodcompact.SkinModCompact;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name(value = SkinModCompact.NAME)
@IFMLLoadingPlugin.SortingIndex(value = 500)
public class FMLLoadingPlugin implements IFMLLoadingPlugin {
    @Override()
    public String[] getASMTransformerClass() {
        return new String[] {ClassTransformer.class.getName()};
    }

    @Override()
    public String getModContainerClass() {
        return SkinModCompact.class.getName();
    }

    @Override()
    public String getSetupClass() {
        return null;
    }

    @Override()
    public void injectData(Map<String, Object> data) {

    }

    @Override()
    public String getAccessTransformerClass() {
        return null;
    }
}
