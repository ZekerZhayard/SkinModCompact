package io.github.zekerzhayard.skinmodcompact.asm.transformers.impl;

import io.github.zekerzhayard.skinmodcompact.asm.transformers.AbstractClassTransformer;
import io.github.zekerzhayard.skinmodcompact.asm.transformers.AbstractInsnTransformer;
import io.github.zekerzhayard.skinmodcompact.asm.transformers.AbstractMethodTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ConfigTransformer extends AbstractClassTransformer {
    @Override
    public boolean isTargetClassName(String className) {
        return className.equals("customskinloader.config.Config");
    }

    @Override
    public AbstractMethodTransformer[] getMethodTransformers() {
        return new AbstractMethodTransformer[] {
                new AbstractMethodTransformer() {
                    @Override
                    public boolean isTargetMethod(String methodName, String methodDesc) {
                        return methodName.equals("loadConfig0") && methodDesc.equals("()Lcustomskinloader/config/Config;");
                    }

                    @Override
                    public AbstractInsnTransformer[] getInsnTransformers() {
                        return new AbstractInsnTransformer[] {
                                new AbstractInsnTransformer() {
                                    @Override
                                    public boolean isTargetInsn(AbstractInsnNode ain) {
                                        if (ain instanceof FieldInsnNode) {
                                            FieldInsnNode fin = (FieldInsnNode) ain;
                                            return fin.getOpcode() == Opcodes.GETFIELD && fin.owner.equals("customskinloader/config/Config") && fin.name.equals("enableLocalProfileCache") && fin.desc.equals("Z");
                                        }
                                        return false;
                                    }

                                    @Override
                                    public void transform(MethodNode mn, AbstractInsnNode ain) {
                                        mn.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "SkinModCompactByteCodeHook", "cleanDirectory", "(Z)Z", false));
                                    }
                                }
                        };
                    }
                }
        };
    }
}
