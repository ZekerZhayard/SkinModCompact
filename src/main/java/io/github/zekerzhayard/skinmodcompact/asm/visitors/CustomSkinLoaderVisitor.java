package io.github.zekerzhayard.skinmodcompact.asm.visitors;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import io.github.zekerzhayard.skinmodcompact.asm.ClassTransformer;

public class CustomSkinLoaderVisitor extends ClassVisitor {
    public CustomSkinLoaderVisitor(int api, ClassVisitor cv, String className) {
        super(api, cv);
    }

    @Override()
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("loadProfile0") && desc.equals("(Lcom/mojang/authlib/GameProfile;)Lcustomskinloader/profile/UserProfile;")) {
            ClassTransformer.logger.debug("Found the method: " + name + desc);
            return new MethodVisitor(this.api, mv) {
                @Override()
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                    if (opcode == Opcodes.INVOKESPECIAL && owner.equals("customskinloader/profile/UserProfile") && name.equals("<init>") && desc.equals("()V")) {
                        ClassTransformer.logger.debug("Found the node: {} {}.{} {}", opcode, owner, name, desc);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "SkinModCompactByteCodeHook", "loadProfile", "(Lcustomskinloader/profile/UserProfile;Lcom/mojang/authlib/GameProfile;)Lcustomskinloader/profile/UserProfile;", false);
                    }
                }
                
                @Override()
                public void visitJumpInsn(int opcode, Label label) {
                    if (opcode == Opcodes.IF_ICMPGE) {
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "SkinModCompactByteCodeHook", "jump", "(I)I", false);
                    }
                    super.visitJumpInsn(opcode, label);
                }
            };
        }
        return mv;
    }
}
