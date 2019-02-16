package io.github.zekerzhayard.skinmodcompact.asm.visitors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import customskinloader.loader.ProfileLoader;
import io.github.zekerzhayard.skinmodcompact.SkinModCompact;
import io.github.zekerzhayard.skinmodcompact.netease.NeteaseAPILoader;

public class ProfileLoaderVisitor extends ClassVisitor {
    public static ProfileLoader.IProfileLoader[] addNeteaseAPI(ProfileLoader.IProfileLoader[] plArray) {
        return ArrayUtils.add(plArray, new NeteaseAPILoader());
    }
    
    private Logger logger = LogManager.getLogger(SkinModCompact.NAME + "-" + this.getClass().getSimpleName());
    
    public ProfileLoaderVisitor(int api, ClassVisitor cv, String className) {
        super(api, cv);
    }
    
    @Override()
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("<clinit>") && desc.equals("()V")) {
            this.logger.debug("Found the method: " + name + desc);
            return new MethodVisitor(this.api, mv) {
                @Override()
                public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                    if (opcode == Opcodes.PUTSTATIC && owner.equals("customskinloader/loader/ProfileLoader") && name.equals("DEFAULT_LOADERS")) {
                        ProfileLoaderVisitor.this.logger.debug("Found the node: {} {}.{} {}", opcode, owner, name, desc);
                        this.mv.visitMethodInsn(Opcodes.INVOKESTATIC, "io/github/zekerzhayard/skinmodcompact/asm/visitors/ProfileLoaderVisitor", "addNeteaseAPI", "([Lcustomskinloader/loader/ProfileLoader$IProfileLoader;)[Lcustomskinloader/loader/ProfileLoader$IProfileLoader;", false);
                    }
                    super.visitFieldInsn(opcode, owner, name, desc);
                }
            };
        }
        return mv;
    }
}
