package io.github.zekerzhayard.skinmodcompact.asm.visitors;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.mojang.authlib.GameProfile;

import io.github.zekerzhayard.skinmodcompact.SkinModCompact;
import net.minecraft.client.resources.SkinManager;

public class FakeSkinManagerVisitor extends ClassVisitor {
    public static ConcurrentHashMap<GameProfile, SkinManager.SkinAvailableCallback> callBackMap = new ConcurrentHashMap<>();
    
    public static ExecutorService initThreadPool(ThreadPoolExecutor tpe) {
        tpe.setMaximumPoolSize(Integer.MAX_VALUE);
        tpe.setCorePoolSize(Integer.MAX_VALUE);
        tpe.setKeepAliveTime(1L, TimeUnit.MILLISECONDS);
        tpe.allowCoreThreadTimeOut(true);
        return tpe;
    }
    
    public static void putMap(GameProfile gp, SkinManager.SkinAvailableCallback callBack) {
        FakeSkinManagerVisitor.callBackMap.put(gp, callBack);
    }
    
    private Logger logger = LogManager.getLogger(SkinModCompact.NAME + "-" + this.getClass().getSimpleName());

    public FakeSkinManagerVisitor(int api, ClassVisitor cv, String className) {
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
                    if (opcode == Opcodes.PUTSTATIC && owner.equals("customskinloader/fake/FakeSkinManager") && name.equals("THREAD_POOL")) {
                        FakeSkinManagerVisitor.this.logger.debug("Found the node: {} {}.{} {}", opcode, owner, name, desc);
                        this.mv.visitMethodInsn(Opcodes.INVOKESTATIC, "io/github/zekerzhayard/skinmodcompact/asm/visitors/FakeSkinManagerVisitor", "initThreadPool", "(Ljava/util/concurrent/ThreadPoolExecutor;)Ljava/util/concurrent/ExecutorService;", false);
                    }
                    super.visitFieldInsn(opcode, owner, name, desc);
                }
            };
        } else if (name.equals("loadProfileTextures") && desc.equals("(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;Z)V")) {
            this.logger.debug("Found the method: " + name + desc);
            return new MethodVisitor(this.api, mv) {
                @Override()
                public void visitCode() {
                    super.visitCode();
                    this.mv.visitVarInsn(Opcodes.ALOAD, 1);
                    this.mv.visitVarInsn(Opcodes.ALOAD, 2);
                    this.mv.visitMethodInsn(Opcodes.INVOKESTATIC, "io/github/zekerzhayard/skinmodcompact/asm/visitors/FakeSkinManagerVisitor", "putMap", "(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;)V", false);
                }
            };
        }
        return mv;
    }
}
