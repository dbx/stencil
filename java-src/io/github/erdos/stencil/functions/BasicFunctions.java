package io.github.erdos.stencil.functions;

import io.github.erdos.stencil.Function;

import java.util.Collection;

/**
 * Common general purpose functions.
 */
@SuppressWarnings("unused")
public enum BasicFunctions implements Function {

    /**
     * Returns the first non-null an non-empty value.
     * <p>
     * Accepts any arguments. Skips null values, empty strings and empty collections.
     */
    COALESCE() {
        @Override
        public Object call(Object... arguments) {
            for (Object arg : arguments)
                if (arg != null && !"".equals(arg) && (!(arg instanceof Collection) || ((Collection) arg).isEmpty()))
                    return arg;
            return null;
        }
    };

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}
