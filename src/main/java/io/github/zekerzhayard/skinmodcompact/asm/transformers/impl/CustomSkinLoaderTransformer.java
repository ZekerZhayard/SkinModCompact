package io.github.zekerzhayard.skinmodcompact.asm.transformers.impl;

import io.github.zekerzhayard.skinmodcompact.asm.transformers.AbstractClassTransformer;
import io.github.zekerzhayard.skinmodcompact.asm.transformers.AbstractInsnTransformer;
import io.github.zekerzhayard.skinmodcompact.asm.transformers.AbstractMethodTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class CustomSkinLoaderTransformer extends AbstractClassTransformer {
    @Override
    public boolean isTargetClassName(String className) {
        return className.equals("customskinloader.CustomSkinLoader");
    }

    @Override
    public AbstractMethodTransformer[] getMethodTransformers() {
        return new AbstractMethodTransformer[] {
            new AbstractMethodTransformer() {
                @Override
                public boolean isTargetMethod(String methodName, String methodDesc) {
                    return methodName.equals("loadProfile0") && methodDesc.equals("(Lcom/mojang/authlib/GameProfile;)Lcustomskinloader/profile/UserProfile;");
                }

                @Override
                public AbstractInsnTransformer[] getInsnTransformers() {
                    return new AbstractInsnTransformer[] {
                        new AbstractInsnTransformer() {
                            @Override
                            public boolean isTargetInsn(AbstractInsnNode ain) {
                                if (ain.getOpcode() == Opcodes.INVOKESPECIAL) {
                                    MethodInsnNode min = (MethodInsnNode) ain;
                                    return min.owner.equals("customskinloader/profile/UserProfile") && min.name.equals("<init>") && min.desc.equals("()V");
                                }
                                return false;
                            }

                            @Override
                            public void transform(MethodNode mn, AbstractInsnNode ain) {
                                mn.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "SkinModCompactByteCodeHook", "loadProfile", "(Lcustomskinloader/profile/UserProfile;Lcom/mojang/authlib/GameProfile;)Lcustomskinloader/profile/UserProfile;", false));
                                mn.instructions.insert(ain, new VarInsnNode(Opcodes.ALOAD, 0));
                            }
                        }, new AbstractInsnTransformer() {
                            @Override
                            public boolean isTargetInsn(AbstractInsnNode ain) {
                                return ain.getOpcode() == Opcodes.IF_ICMPGE;
                            }

                            @Override
                            public void transform(MethodNode mn, AbstractInsnNode ain) {
                                mn.instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "SkinModCompactByteCodeHook", "jump", "(I)I", false));
                            }
                        }, new AbstractInsnTransformer() {
                            @Override
                            public boolean isTargetInsn(AbstractInsnNode ain) {
                                if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                                    MethodInsnNode min = (MethodInsnNode) ain;
                                    return min.owner.equals("customskinloader/profile/UserProfile") && min.name.equals("mix") && min.desc.equals("(Lcustomskinloader/profile/UserProfile;)V");
                                }
                                return false;
                            }

                            @Override
                            public void transform(MethodNode mn, AbstractInsnNode ain) {
                                mn.instructions.insertBefore(ain, new VarInsnNode(Opcodes.ALOAD, 0));
                                mn.instructions.set(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "SkinModCompactByteCodeHook", "mix", "(Lcustomskinloader/profile/UserProfile;Lcustomskinloader/profile/UserProfile;Lcom/mojang/authlib/GameProfile;)V", false));
                            }
                        }, new AbstractInsnTransformer() {
                            @Override
                            public boolean isTargetInsn(AbstractInsnNode ain) {
                                if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                                    MethodInsnNode min = (MethodInsnNode) ain;
                                    return min.owner.equals("customskinloader/profile/UserProfile") && min.name.equals("isFull") && min.desc.equals("()Z");
                                }
                                return false;
                            }

                            @Override
                            public void transform(MethodNode mn, AbstractInsnNode ain) {
                                mn.instructions.insertBefore(ain, new VarInsnNode(Opcodes.ALOAD, 0));
                                mn.instructions.set(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "SkinModCompactByteCodeHook", "isFull", "(Lcustomskinloader/profile/UserProfile;Lcom/mojang/authlib/GameProfile;)Z", false));
                            }
                        }
                    };
                }
            }, new AbstractMethodTransformer() {
                @Override
                public boolean isTargetMethod(String methodName, String methodDesc) {
                    return methodName.equals("loadProfileFromCache") && methodDesc.equals("(Lcom/mojang/authlib/GameProfile;)Ljava/util/Map;");
                }

                @Override
                public AbstractInsnTransformer[] getInsnTransformers() {
                    return new AbstractInsnTransformer[] {
                        new AbstractInsnTransformer() {
                            @Override
                            public boolean isTargetInsn(AbstractInsnNode ain) {
                                if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                                    MethodInsnNode min = (MethodInsnNode) ain;
                                    return min.owner.equals("java/lang/Thread") && min.name.equals("start") && min.desc.equals("()V");
                                }
                                return false;
                            }

                            @Override
                            public void transform(MethodNode mn, AbstractInsnNode ain) {
                                mn.instructions.insertBefore(ain, new VarInsnNode(Opcodes.ALOAD, 0));
                                mn.instructions.set(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "SkinModCompactByteCodeHook", "loadSkull", "(Ljava/lang/Thread;Lcom/mojang/authlib/GameProfile;)V", false));
                            }
                        }
                    };
                }
            }
        };
    }

}
