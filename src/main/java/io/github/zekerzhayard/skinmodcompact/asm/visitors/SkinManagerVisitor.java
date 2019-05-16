package io.github.zekerzhayard.skinmodcompact.asm.visitors;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import io.github.zekerzhayard.skinmodcompact.asm.ClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class SkinManagerVisitor extends ClassVisitor {
    private String className;

    public SkinManagerVisitor(int api, ClassVisitor cv, String className) {
        super(api, cv);
        this.className = className;
    }

    @Override()
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(this.className, name, desc).equals("func_152788_a") && desc.equals("(Lcom/mojang/authlib/GameProfile;)Ljava/util/Map;")) {
            ClassTransformer.logger.debug("Found the method: " + name + desc);
            return new MethodVisitor(this.api, mv) {
                @Override()
                public void visitCode() {
                    super.visitCode();
                    super.visitVarInsn(Opcodes.ALOAD, 0);
                    super.visitFieldInsn(Opcodes.GETFIELD, "net/minecraft/client/resources/SkinManager", "fakeManager", "Lcustomskinloader/fake/FakeSkinManager;");
                    super.visitVarInsn(Opcodes.ALOAD, 1);
                    super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "customskinloader/fake/FakeSkinManager", "loadSkinFromCache", "(Lcom/mojang/authlib/GameProfile;)Ljava/util/Map;", false);
                    super.visitInsn(Opcodes.ARETURN);
                }
            };
        } else if (FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(this.className, name, desc).equals("func_152790_a") && FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(desc).equals("(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;Z)V")) {
            ClassTransformer.logger.debug("Found the method: " + name + desc);
            return new MethodVisitor(this.api, mv) {
                @Override()
                public void visitCode() {
                    super.visitCode();
                    super.visitVarInsn(Opcodes.ALOAD, 1);
                    super.visitVarInsn(Opcodes.ALOAD, 2);
                    super.visitMethodInsn(Opcodes.INVOKESTATIC, "SkinModCompactByteCodeHook", "loadProfileTextures", "(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;)V", false);
                    super.visitInsn(Opcodes.RETURN);
                }
            };
        }
        return mv;
    }
}
