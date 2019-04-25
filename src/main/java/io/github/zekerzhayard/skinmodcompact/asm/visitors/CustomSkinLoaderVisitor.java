package io.github.zekerzhayard.skinmodcompact.asm.visitors;

import org.objectweb.asm.ClassVisitor;
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
        if (name.equals("loadProfile") && desc.equals("(Lcom/mojang/authlib/GameProfile;)Ljava/util/Map;")) {
        	ClassTransformer.logger.debug("Found the method: " + name + desc);
        	return new MethodVisitor(this.api, mv) {
                @Override()
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                	super.visitMethodInsn(opcode, owner, name, desc, itf);
                    if (opcode == Opcodes.INVOKEVIRTUAL && owner.equals("customskinloader/profile/ProfileCache") && name.equals("isReady") && desc.equals("(Ljava/lang/String;)Z")) {
                        ClassTransformer.logger.debug("Found the node: {} {}.{} {}", opcode, owner, name, desc);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "SkinModCompactByteCodeHook", "profileCacheCheck", "(Z)Z", false);
                    }
                }
        	};
        } else if (name.equals("loadProfile0") && desc.equals("(Lcom/mojang/authlib/GameProfile;)Lcustomskinloader/profile/UserProfile;")) {
        	ClassTransformer.logger.debug("Found the method: " + name + desc);
        	return new MethodVisitor(this.api, mv) {
                @Override()
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    if (opcode == Opcodes.INVOKEVIRTUAL && owner.equals("customskinloader/profile/UserProfile") && name.equals("isFull") && desc.equals("()Z")) {
                        ClassTransformer.logger.debug("Found the node: {} {}.{} {}", opcode, owner, name, desc);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "SkinModCompactByteCodeHook", "loadSkin", "(Lcustomskinloader/profile/UserProfile;Lcom/mojang/authlib/GameProfile;)Lcustomskinloader/profile/UserProfile;", false);
                    }
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                }
        	};
        }
        return mv;
    }
}
