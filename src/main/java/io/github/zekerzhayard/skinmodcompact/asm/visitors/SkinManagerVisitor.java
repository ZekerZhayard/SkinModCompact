package io.github.zekerzhayard.skinmodcompact.asm.visitors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import io.github.zekerzhayard.skinmodcompact.SkinModCompact;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class SkinManagerVisitor extends ClassVisitor {
    private Logger logger = LogManager.getLogger(SkinModCompact.NAME + "-" + this.getClass().getSimpleName());
    private String className;
    
    public SkinManagerVisitor(int api, ClassVisitor cv, String className) {
        super(api, cv);
        this.className = className;
    }
    
    @Override()
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(this.className, name, desc).equals("func_152788_a") && desc.equals("(Lcom/mojang/authlib/GameProfile;)Ljava/util/Map;")) {
            this.logger.debug("Found the method: " + name + desc);
            return new MethodVisitor(this.api, mv) {
                @Override()
                public void visitCode() {
                    super.visitCode();
                    this.mv.visitVarInsn(Opcodes.ALOAD, 0);
                    this.mv.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/resources/SkinManager", "fakeManager", "Lcustomskinloader/fake/FakeSkinManager;");
                    this.mv.visitVarInsn(Opcodes.ALOAD, 1);
                    this.mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "customskinloader/fake/FakeSkinManager", "loadSkinFromCache", "(Lcom/mojang/authlib/GameProfile;)Ljava/util/Map;", false);
                    this.mv.visitInsn(Opcodes.ARETURN);
                }
            };
        }
        return mv;
    }
}
