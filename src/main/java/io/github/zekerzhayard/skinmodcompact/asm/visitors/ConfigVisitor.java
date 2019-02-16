package io.github.zekerzhayard.skinmodcompact.asm.visitors;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import customskinloader.CustomSkinLoader;
import io.github.zekerzhayard.skinmodcompact.SkinModCompact;

public class ConfigVisitor extends ClassVisitor {
    public static void cleanDirectory(final File file) {
        new Thread() {
            @Override()
            public void run() {
                try {
                    FileUtils.cleanDirectory(file);
                } catch (Exception e) {
                    CustomSkinLoader.logger.warning("Exception occurs while cleaning cache: " + e.toString());
                }
            }
        }.start();
    }
    
    private Logger logger = LogManager.getLogger(SkinModCompact.NAME + "-" + this.getClass().getSimpleName());
    
    public ConfigVisitor(int api, ClassVisitor cv, String className) {
        super(api, cv);
    }
    
    @Override()
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("loadConfig0") && desc.equals("()Lcustomskinloader/config/Config;")) {
            this.logger.debug("Found the method: " + name + desc);
            return new MethodVisitor(this.api, mv) {
                @Override()
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    if (opcode == Opcodes.INVOKESTATIC && owner.equals("org/apache/commons/io/FileUtils") && name.equals("deleteDirectory") && desc.equals("(Ljava/io/File;)V")) {
                        ConfigVisitor.this.logger.debug("Found the node: {} {}.{} {}", opcode, owner, name, desc);
                        this.mv.visitMethodInsn(opcode, "io/github/zekerzhayard/skinmodcompact/asm/visitors/ConfigVisitor", "cleanDirectory", desc, itf);
                    } else {
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                    }
                }
            };
        }
        return mv;
    }
}
