package org.pitest.mutationtest.engine.gregor.mutators.additional;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.engine.gregor.MutationContext;

import java.util.Random;

public enum StringMutator implements MethodMutatorFactory {

    STRING_MUTATOR;

    @Override
    public MethodVisitor create(
            MutationContext context,
            MethodInfo methodInfo,
            MethodVisitor methodVisitor
    ) {
        return new StringMethodVisitor(this, context, methodVisitor);
    }

    @Override
    public String getGloballyUniqueId() {
        return this.getClass().getName();
    }

    @Override
    public String getName() {
        return name();
    }

    private static final class StringMethodVisitor extends MethodVisitor {

        private final int lengthSpread = 30;
        private final Random random = new Random();
        private final MutationContext context;
        private final MethodMutatorFactory factory;

        public StringMethodVisitor(
                MethodMutatorFactory factory,
                MutationContext context,
                MethodVisitor methodVisitor
        ) {
            super(Opcodes.ASM6, methodVisitor);
            this.factory = factory;
            this.context = context;
        }

        @Override
        public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
            super.visitMultiANewArrayInsn(descriptor, numDimensions);
        }

        @Override
        public void visitLdcInsn(Object value) {
            if (value instanceof String) {
                final String randomizedString = randomizeString((String) value);
                if (shouldMutate((String) value, randomizedString)) {
                    super.visitLdcInsn(randomizeString(randomizedString));
                } else {
                    super.visitLdcInsn(value);
                }
            } else {
                super.visitLdcInsn(value);
            }
        }

        private boolean shouldMutate(final String from, final String to) {
            MutationIdentifier id = context.registerMutation(factory, "Mutate string from: " + from + ", to: " + to);
            return context.shouldMutate(id);
        }

        private String randomizeString(final String input) {
            int length = input.length();
            int minLength = Math.max(0, length - lengthSpread);
            int maxLength = length + lengthSpread;
            int newLength = minLength + random.nextInt(maxLength - minLength);

            char[] chars = new char[newLength];
            for (int i = 0; i < chars.length; i++) {
                chars[i] = (char) random.nextInt(Character.MAX_VALUE);
            }

            return new String(chars);
        }

    }

}
