package io.github.zekerzhayard.skinmodcompact.asm.visitors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import customskinloader.CustomSkinLoader;
import customskinloader.utils.HttpRequestUtil;
import customskinloader.utils.HttpTextureUtil;
import io.github.zekerzhayard.skinmodcompact.SkinModCompact;

public class ConfigVisitor extends ClassVisitor {
    public static boolean cleanDirectory(boolean b) {
        new Thread() {
            @Override()
            public void run() {
                if (ConfigVisitor.cleanDirectory(HttpRequestUtil.CACHE_DIR) & ConfigVisitor.cleanDirectory(HttpTextureUtil.getCacheDir())) {
                    CustomSkinLoader.logger.info("Successfully cleaned cache.");
                }
            }
        }.start();
        return true;
    }
    
    public static boolean cleanDirectory(final File directory) {
        boolean isSuccessful = true;
        for (File filteredFile : FileUtils.listFiles(directory, new AbstractFileFilter() {
            private long ctm = System.currentTimeMillis();

            @Override()
            public boolean accept(File file) {
                try {
                    return Files.readAttributes(file.toPath(), BasicFileAttributes.class).creationTime().to(TimeUnit.MILLISECONDS) < this.ctm;
                } catch (IOException e) {
                    return false;
                }
            }
        }, TrueFileFilter.INSTANCE)) {
            try {
                FileUtils.forceDelete(filteredFile);
                if (filteredFile.getParentFile().list().length == 0) {
                    FileUtils.forceDelete(filteredFile.getParentFile());
                }
            } catch (Exception e) {
                isSuccessful = false;
                CustomSkinLoader.logger.warning("Exception occurs while cleaning cache: " + e.toString());
            }
        }
        return isSuccessful;
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
                private boolean ready = true;

                @Override()
                public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                    super.visitFieldInsn(opcode, owner, name, desc);
                    if (opcode == Opcodes.GETFIELD && owner.equals("customskinloader/config/Config") && name.equals("enableLocalProfileCache") && desc.equals("Z") && this.ready) {
                        this.ready = false;
                        ConfigVisitor.this.logger.debug("Found the node: {} {}.{} {}", opcode, owner, name, desc);
                        this.mv.visitMethodInsn(Opcodes.INVOKESTATIC, "io/github/zekerzhayard/skinmodcompact/asm/visitors/ConfigVisitor", "cleanDirectory", "(Z)Z", false);
                    }
                }
            };
        }
        return mv;
    }
}
