package io.github.zekerzhayard.skinmodcompact.asm.transformers.impl;

import io.github.zekerzhayard.skinmodcompact.asm.transformers.AbstractClassTransformer;
import io.github.zekerzhayard.skinmodcompact.asm.transformers.AbstractInsnTransformer;
import io.github.zekerzhayard.skinmodcompact.asm.transformers.AbstractMethodTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class SkinManagerTransformer extends AbstractClassTransformer {
    @Override
    public boolean isTargetClassName(String className) {
        return className.equals("net.minecraft.client.resources.SkinManager");
    }

    @Override
    public AbstractMethodTransformer[] getMethodTransformers() {
        return new AbstractMethodTransformer[] {
                new AbstractMethodTransformer() {
                    @Override
                    public boolean isTargetMethod(String methodName, String methodDesc) {
                        return methodName.equals("func_152788_a") && methodDesc.equals("(Lcom/mojang/authlib/GameProfile;)Ljava/util/Map;");
                    }

                    @Override
                    public AbstractInsnTransformer[] getInsnTransformers() {
                        return new AbstractInsnTransformer[0];
                    }
                    
                    @Override
                    public void transform(MethodNode mn) {
                        mn.instructions.clear();
                        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                        mn.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/resources/SkinManager", "fakeManager", "Lcustomskinloader/fake/FakeSkinManager;"));
                        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "customskinloader/fake/FakeSkinManager", "loadSkinFromCache", "(Lcom/mojang/authlib/GameProfile;)Ljava/util/Map;", false));
                        mn.instructions.add(new InsnNode(Opcodes.ARETURN));
                    }
                }, new AbstractMethodTransformer() {
                    @Override
                    public boolean isTargetMethod(String methodName, String methodDesc) {
                        return methodName.equals("func_152790_a") && methodDesc.equals("(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;Z)V");
                    }

                    @Override
                    public AbstractInsnTransformer[] getInsnTransformers() {
                        return new AbstractInsnTransformer[0];
                    }
                    
                    @Override
                    public void transform(MethodNode mn) {
                        mn.instructions.clear();
                        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                        mn.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
                        mn.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "SkinModCompactByteCodeHook", "loadProfileTextures", "(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;)V", false));
                        mn.instructions.add(new InsnNode(Opcodes.RETURN));
                    }
                }
        };
    }

}
