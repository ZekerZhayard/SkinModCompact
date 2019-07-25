package io.github.zekerzhayard.skinmodcompact.asm.transformers.impl;

import io.github.zekerzhayard.skinmodcompact.asm.transformers.AbstractClassTransformer;
import io.github.zekerzhayard.skinmodcompact.asm.transformers.AbstractInsnTransformer;
import io.github.zekerzhayard.skinmodcompact.asm.transformers.AbstractMethodTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ThreadDownloadImageDataTransformer extends AbstractClassTransformer {
    @Override
    public boolean isTargetClassName(String className) {
        return className.startsWith("net.minecraft.client.renderer.ThreadDownloadImageData");
    }

    @Override
    public AbstractMethodTransformer[] getMethodTransformers() {
        return new AbstractMethodTransformer[] {
            new AbstractMethodTransformer() {
                @Override
                public boolean isTargetMethod(String methodName, String methodDesc) {
                    return (methodName.equals("loadPipelined") || methodName.equals("run")) && methodDesc.equals("()V");
                }

                @Override
                public AbstractInsnTransformer[] getInsnTransformers() {
                    return new AbstractInsnTransformer[] {
                        new AbstractInsnTransformer() {
                            @Override
                            public boolean isTargetInsn(AbstractInsnNode ain) {
                                if (ain.getOpcode() == Opcodes.INVOKEINTERFACE) {
                                    MethodInsnNode min = (MethodInsnNode) ain;
                                    return min.owner.equals("org/apache/logging/log4j/Logger") && min.name.equals("error") && (min.desc.equals("(Ljava/lang/String;)V") || min.desc.equals("(Ljava/lang/String;Ljava/lang/Throwable;)V"));
                                }
                                return false;
                            }

                            @Override
                            public void transform(MethodNode mn, AbstractInsnNode ain) {
                                if (((MethodInsnNode) ain).desc.equals("(Ljava/lang/String;)V")) {
                                    mn.instructions.insertBefore(ain, new InsnNode(Opcodes.DUP_X1));
                                    mn.instructions.insertBefore(ain, new InsnNode(Opcodes.POP));
                                    mn.instructions.insertBefore(ain, new InsnNode(Opcodes.DUP_X1));
                                    mn.instructions.insertBefore(ain, new InsnNode(Opcodes.DUP_X1));
                                } else {
                                    mn.instructions.insertBefore(ain, new InsnNode(Opcodes.DUP2_X1));
                                    mn.instructions.insertBefore(ain, new InsnNode(Opcodes.POP2));
                                    mn.instructions.insertBefore(ain, new InsnNode(Opcodes.DUP_X2));
                                    mn.instructions.insertBefore(ain, new InsnNode(Opcodes.DUP_X2));
                                }
                                mn.instructions.insertBefore(ain, new InsnNode(Opcodes.POP));
                                InsnList insnList = new InsnList();
                                insnList.add(new LdcInsnNode("[SkinModCompact] Retry to download image."));
                                insnList.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "org/apache/logging/log4j/Logger", "info", "(Ljava/lang/String;)V", true));
                                insnList.add(new LdcInsnNode(500L));
                                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false));
                                insnList.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                if (mn.name.equals("loadPipelined")) {
                                    insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/renderer/ThreadDownloadImageData", mn.name, mn.desc, false));
                                } else {
                                    insnList.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Thread", mn.name, mn.desc, false));
                                }
                                mn.instructions.insert(ain, insnList);
                            }
                        }
                    };
                }
            }
        };
    }

}
