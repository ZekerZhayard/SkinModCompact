package io.github.zekerzhayard.skinmodcompact.asm;

import java.util.ServiceLoader;

import io.github.zekerzhayard.skinmodcompact.asm.transformers.AbstractClassTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public class ClassTransformer implements IClassTransformer {
    private ServiceLoader<AbstractClassTransformer> sl = ServiceLoader.load(AbstractClassTransformer.class);

    @Override()
    public byte[] transform(String className, String transformedName, byte[] basicClass) {
        for (AbstractClassTransformer act : this.sl) {
            if (act.isTargetClassName(transformedName)) {
                System.out.println("Found the class: " + className + " -> " + transformedName);
                ClassNode cn = new ClassNode();
                new ClassReader(basicClass).accept(cn, ClassReader.EXPAND_FRAMES);
                act.transform(cn);
                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                cn.accept(cw);
                return cw.toByteArray();
            }
        }
        return basicClass;
    }
}
