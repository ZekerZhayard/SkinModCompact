package io.github.zekerzhayard.skinmodcompact.asm.transformers.impl;

import io.github.zekerzhayard.skinmodcompact.asm.transformers.AbstractClassTransformer;
import io.github.zekerzhayard.skinmodcompact.asm.transformers.AbstractInsnTransformer;
import io.github.zekerzhayard.skinmodcompact.asm.transformers.AbstractMethodTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ThreadDownloadImageData_1Transformer extends AbstractClassTransformer {
    @Override
    public boolean isTargetClassName(String className) {
        return className.equals("net.minecraft.client.renderer.ThreadDownloadImageData$1");
    }

    @Override
    public AbstractMethodTransformer[] getMethodTransformers() {
        return new AbstractMethodTransformer[] {
                new AbstractMethodTransformer() {
                    @Override
                    public boolean isTargetMethod(String methodName, String methodDesc) {
                        return methodName.equals("run") && methodDesc.equals("()V");
                    }

                    @Override
                    public AbstractInsnTransformer[] getInsnTransformers() {
                        return new AbstractInsnTransformer[] {
                                new AbstractInsnTransformer() {
                                    @Override
                                    public boolean isTargetInsn(AbstractInsnNode ain) {
                                        if (ain instanceof MethodInsnNode) {
                                            MethodInsnNode min = (MethodInsnNode) ain;
                                            return min.getOpcode() == Opcodes.INVOKEINTERFACE && min.owner.equals("org/apache/logging/log4j/Logger") && min.name.equals("error") && min.desc.equals("(Ljava/lang/String;)V");
                                        }
                                        return false;
                                    }

                                    @Override
                                    public void transform(MethodNode mn, AbstractInsnNode ain) {
                                        mn.instructions.insertBefore(ain, new VarInsnNode(Opcodes.ALOAD, 0));
                                        mn.instructions.set(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "SkinModCompactByteCodeHook", "retryToDownload", "(Lorg/apache/logging/log4j/Logger;Ljava/lang/String;Ljava/lang/Thread;)V", false));
                                    }
                                }
                        };
                    }
                }
        };
    }

}
