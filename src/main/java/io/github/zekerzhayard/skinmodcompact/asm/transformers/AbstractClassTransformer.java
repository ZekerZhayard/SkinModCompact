package io.github.zekerzhayard.skinmodcompact.asm.transformers;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class AbstractClassTransformer {
    private AbstractMethodTransformer[] methodTransformers = this.getMethodTransformers();
    
    public abstract boolean isTargetClassName(String className);
    
    public abstract AbstractMethodTransformer[] getMethodTransformers();
    
    public void transform(ClassNode cn) {
        for (MethodNode mn : cn.methods) {
            for (AbstractMethodTransformer amt : this.methodTransformers) {
                if (amt.isTargetMethod(FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(cn.name, mn.name, mn.desc), FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(mn.desc))) {
                    System.out.println("Found the method: " + mn.name + mn.desc);
                    amt.transform(mn);
                }
            }
        }
    }
}
