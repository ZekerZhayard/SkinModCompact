package io.github.zekerzhayard.skinmodcompact.asm.visitors;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import customskinloader.config.SkinSiteProfile;
import customskinloader.fake.FakeSkinManager;
import customskinloader.profile.ModelManager0;
import customskinloader.profile.UserProfile;
import io.github.zekerzhayard.skinmodcompact.SkinModCompact;
import net.minecraft.client.Minecraft;

public class CustomSkinLoaderVisitor extends ClassVisitor {
    public static SkinSiteProfile[] addNeteaseAPI(SkinSiteProfile[] sspArray) {
        return ArrayUtils.add(sspArray, new SkinSiteProfile() {
            {
                this.name = "Netease";
                this.type = "NeteaseAPI";
            }
        });
    }
    
    public static UserProfile loadSkin(final UserProfile up, final GameProfile gp) {
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override()
            public void run() {
                Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = ModelManager0.fromUserProfile(up);
                for (Map.Entry<MinecraftProfileTexture.Type, MinecraftProfileTexture> entry : map.entrySet()) {
                    try {
                        ((FakeSkinManager) FieldUtils.readDeclaredField(Minecraft.getMinecraft().getSkinManager(), "fakeManager", true)).loadSkin(entry.getValue(), entry.getKey(), FakeSkinManagerVisitor.callBackMap.get(gp));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        if (Thread.currentThread().getName().endsWith("'s skull") && up.hasSkinUrl()) {
        	return new UserProfile() {
        		{
        			this.skinUrl = "http://example.com/";
        			this.capeUrl = "http://example.com/";
        		}
        	};
        }
        return up;
    }
    
    private Logger logger = LogManager.getLogger(SkinModCompact.NAME + "-" + this.getClass().getSimpleName());
    
    public CustomSkinLoaderVisitor(int api, ClassVisitor cv, String className) {
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
                    if (opcode == Opcodes.PUTSTATIC && owner.equals("customskinloader/CustomSkinLoader") && name.equals("DEFAULT_LOAD_LIST")) {
                        CustomSkinLoaderVisitor.this.logger.debug("Found the node: {} {}.{} {}", opcode, owner, name, desc);
                        this.mv.visitMethodInsn(Opcodes.INVOKESTATIC, "io/github/zekerzhayard/skinmodcompact/asm/visitors/CustomSkinLoaderVisitor", "addNeteaseAPI", "([Lcustomskinloader/config/SkinSiteProfile;)[Lcustomskinloader/config/SkinSiteProfile;", false);
                    }
                    super.visitFieldInsn(opcode, owner, name, desc);
                }
            };
        } else if (name.equals("loadProfile0") && desc.equals("(Lcom/mojang/authlib/GameProfile;)Lcustomskinloader/profile/UserProfile;")) {
        	this.logger.debug("Found the method: " + name + desc);
        	return new MethodVisitor(this.api, mv) {
                @Override()
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    if (opcode == Opcodes.INVOKEVIRTUAL && owner.equals("customskinloader/profile/UserProfile") && name.equals("isFull") && desc.equals("()Z")) {
                        CustomSkinLoaderVisitor.this.logger.debug("Found the node: {} {}.{} {}", opcode, owner, name, desc);
                        this.mv.visitVarInsn(Opcodes.ALOAD, 0);
                        this.mv.visitMethodInsn(Opcodes.INVOKESTATIC, "io/github/zekerzhayard/skinmodcompact/asm/visitors/CustomSkinLoaderVisitor", "loadSkin", "(Lcustomskinloader/profile/UserProfile;Lcom/mojang/authlib/GameProfile;)Lcustomskinloader/profile/UserProfile;", false);
                    }
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                }
        	};
        }
        return mv;
    }
}
