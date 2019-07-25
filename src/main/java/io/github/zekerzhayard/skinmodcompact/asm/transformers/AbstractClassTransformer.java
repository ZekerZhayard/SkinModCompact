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
                String mappedMethodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(cn.name, mn.name, mn.desc);
                String mappedMethodDesc = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(mn.desc);
                if (amt.isTargetMethod(mappedMethodName, mappedMethodDesc)) {
                    System.out.println("Found the method: " + mn.name + mn.desc + " -> " + mappedMethodName + mappedMethodDesc);
                    amt.transform(mn);
                }
            }
        }
    }
}
