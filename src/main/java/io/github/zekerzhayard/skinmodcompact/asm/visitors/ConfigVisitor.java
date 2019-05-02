package io.github.zekerzhayard.skinmodcompact.asm.visitors;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import io.github.zekerzhayard.skinmodcompact.asm.ClassTransformer;

public class ConfigVisitor extends ClassVisitor {
    public ConfigVisitor(int api, ClassVisitor cv, String className) {
        super(api, cv);
    }

    @Override()
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("loadConfig0") && desc.equals("()Lcustomskinloader/config/Config;")) {
            ClassTransformer.logger.debug("Found the method: " + name + desc);
            return new MethodVisitor(this.api, mv) {
                private boolean ready = true;

                @Override()
                public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                    super.visitFieldInsn(opcode, owner, name, desc);
                    if (opcode == Opcodes.GETFIELD && owner.equals("customskinloader/config/Config") && name.equals("enableLocalProfileCache") && desc.equals("Z") && this.ready) {
                        this.ready = false;
                        ClassTransformer.logger.debug("Found the node: {} {}.{} {}", opcode, owner, name, desc);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "SkinModCompactByteCodeHook", "cleanDirectory", "(Z)Z", false);
                    }
                }
            };
        }
        return mv;
    }
}
