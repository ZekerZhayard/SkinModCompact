package io.github.zekerzhayard.skinmodcompact.asm.transformers.impl;

import io.github.zekerzhayard.skinmodcompact.asm.transformers.AbstractClassTransformer;
import io.github.zekerzhayard.skinmodcompact.asm.transformers.AbstractInsnTransformer;
import io.github.zekerzhayard.skinmodcompact.asm.transformers.AbstractMethodTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class HttpRequestUtilTransformer extends AbstractClassTransformer {
    @Override
    public boolean isTargetClassName(String className) {
        return className.equals("customskinloader.utils.HttpRequestUtil");
    }

    @Override
    public AbstractMethodTransformer[] getMethodTransformers() {
        return new AbstractMethodTransformer[] {
                new AbstractMethodTransformer() {
                    @Override
                    public boolean isTargetMethod(String methodName, String methodDesc) {
                        return methodName.equals("makeHttpRequest") && methodDesc.equals("(Lcustomskinloader/utils/HttpRequestUtil$HttpRequest;I)Lcustomskinloader/utils/HttpRequestUtil$HttpResponce;");
                    }
                    
                    @Override
                    public AbstractInsnTransformer[] getInsnTransformers() {
                        return new AbstractInsnTransformer[] {
                                new AbstractInsnTransformer() {
                                    @Override
                                    public boolean isTargetInsn(AbstractInsnNode ain) {
                                        if (ain instanceof MethodInsnNode) {
                                            MethodInsnNode min = (MethodInsnNode) ain;
                                            return min.getOpcode() == Opcodes.INVOKESPECIAL && min.owner.equals("java/net/URI") && min.name.equals("<init>") && min.desc.equals("(Ljava/lang/String;)V");
                                        }
                                        return false;
                                    }
                                    
                                    @Override
                                    public void transform(MethodNode mn, AbstractInsnNode ain) {
                                        mn.instructions.insertBefore(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "SkinModCompactByteCodeHook", "encodeURL", "(Ljava/lang/String;)Ljava/lang/String;", false));
                                    }
                                }
                        };
                    }
                }
        };
    }
}
