package io.github.zekerzhayard.skinmodcompact.asm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import com.google.common.collect.ImmutableMap;

import io.github.zekerzhayard.skinmodcompact.SkinModCompact;
import io.github.zekerzhayard.skinmodcompact.asm.visitors.ConfigVisitor;
import io.github.zekerzhayard.skinmodcompact.asm.visitors.CustomSkinLoaderVisitor;
import io.github.zekerzhayard.skinmodcompact.asm.visitors.ProfileLoaderVisitor;
import io.github.zekerzhayard.skinmodcompact.asm.visitors.SkinManagerVisitor;
import io.github.zekerzhayard.skinmodcompact.asm.visitors.ThreadDownloadImageData_1Visitor;
import net.minecraft.launchwrapper.IClassTransformer;

public class ClassTransformer implements IClassTransformer {
    public static Logger logger = LogManager.getLogger(SkinModCompact.NAME);

    private ImmutableMap<String, Class<? extends ClassVisitor>> visitors = ImmutableMap.of(
            "customskinloader.config.Config", ConfigVisitor.class,
            "customskinloader.CustomSkinLoader", CustomSkinLoaderVisitor.class,
            "customskinloader.loader.ProfileLoader", ProfileLoaderVisitor.class,
            "net.minecraft.client.renderer.ThreadDownloadImageData$1", ThreadDownloadImageData_1Visitor.class,
            "net.minecraft.client.resources.SkinManager", SkinManagerVisitor.class
    );

    @Override()
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (this.visitors.containsKey(transformedName)) {
            ClassTransformer.logger.debug("Found the class: " + transformedName);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            try {
                new ClassReader(basicClass).accept(this.visitors.get(transformedName).getConstructor(int.class, ClassVisitor.class, String.class).newInstance(Opcodes.ASM5, cw, name), ClassReader.EXPAND_FRAMES);
                return cw.toByteArray();
            } catch (Exception e) {
                ClassTransformer.logger.warn("", e);
            }
        }
        return basicClass;
    }
}
