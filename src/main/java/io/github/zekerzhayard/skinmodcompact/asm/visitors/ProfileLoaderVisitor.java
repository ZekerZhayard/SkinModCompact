package io.github.zekerzhayard.skinmodcompact.asm.visitors;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import io.github.zekerzhayard.skinmodcompact.asm.ClassTransformer;

public class ProfileLoaderVisitor extends ClassVisitor {
    public ProfileLoaderVisitor(int api, ClassVisitor cv, String className) {
        super(api, cv);
    }
    
    @Override()
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("initLoaders") && desc.equals("()Ljava/util/HashMap;")) {
            ClassTransformer.logger.debug("Found the method: " + name + desc);
            return new MethodVisitor(this.api, mv) {
            	@Override()
            	public void visitInsn(int opcode) {
            		if (opcode == Opcodes.ARETURN) {
            			ClassTransformer.logger.debug("Found the node: {}", opcode);
            			super.visitMethodInsn(Opcodes.INVOKESTATIC, "SkinModCompactByteCodeHook", "addNeteaseAPI", "(Ljava/util/HashMap;)Ljava/util/HashMap;", false);
            		}
            		super.visitInsn(opcode);
            	}
            };
        }
        return mv;
    }
}
