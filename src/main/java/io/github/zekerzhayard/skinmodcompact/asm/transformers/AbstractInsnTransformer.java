package io.github.zekerzhayard.skinmodcompact.asm.transformers;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class AbstractInsnTransformer {
    public abstract boolean isTargetInsn(AbstractInsnNode ain);
    
    public abstract void transform(MethodNode mn, AbstractInsnNode ain);
}
