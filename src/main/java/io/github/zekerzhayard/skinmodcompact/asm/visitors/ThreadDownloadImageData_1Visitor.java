package io.github.zekerzhayard.skinmodcompact.asm.visitors;

import io.github.zekerzhayard.skinmodcompact.asm.ClassTransformer;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ThreadDownloadImageData_1Visitor extends ClassVisitor {
    public ThreadDownloadImageData_1Visitor(int api, ClassVisitor cv, String className) {
        super(api, cv);
    }
    
    @Override()
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("run") && desc.equals("()V")) {
            ClassTransformer.logger.debug("Found the method: " + name + desc);
            return new MethodVisitor(this.api, mv) {
                @Override()
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    if (opcode == Opcodes.INVOKEINTERFACE && owner.equals("org/apache/logging/log4j/Logger") && name.equals("error") && desc.equals("(Ljava/lang/String;)V")) {
                        ClassTransformer.logger.debug("Found the node: {} {}.{} {}", opcode, owner, name, desc);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "SkinModCompactByteCodeHook", "retryToDownload", "(Ljava/lang/String;Ljava/lang/Thread;)Ljava/lang/String;", false);
                    }
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                }
            };
        }
        return mv;
    }
}
