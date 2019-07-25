package io.github.zekerzhayard.skinmodcompact.asm.transformers.impl;

import io.github.zekerzhayard.skinmodcompact.asm.transformers.AbstractClassTransformer;
import io.github.zekerzhayard.skinmodcompact.asm.transformers.AbstractInsnTransformer;
import io.github.zekerzhayard.skinmodcompact.asm.transformers.AbstractMethodTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ForgeModTransformer extends AbstractClassTransformer {
    @Override
    public boolean isTargetClassName(String className) {
        return className.equals("customskinloader.forge.ForgeMod");
    }

    @Override
    public AbstractMethodTransformer[] getMethodTransformers() {
        return new AbstractMethodTransformer[] {
            new AbstractMethodTransformer() {
                @Override
                public boolean isTargetMethod(String methodName, String methodDesc) {
                    return methodName.equals("fingerprintError") && methodDesc.equals("(Lnet/minecraftforge/fml/common/event/FMLFingerprintViolationEvent;)V");
                }

                @Override
                public AbstractInsnTransformer[] getInsnTransformers() {
                    return new AbstractInsnTransformer[0];
                }

                @Override
                public void transform(MethodNode mn) {
                    mn.instructions.clear();
                    mn.instructions.add(new InsnNode(Opcodes.RETURN));
                }
            }
        };
    }
}
