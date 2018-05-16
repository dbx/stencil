package io.github.erdos.stencil.functions;

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
    },

    /**
     * Returns true iff input is null, empty string or empty collection.
     * <p>
     * Expects exaclty 1 argument.
     */
    EMPTY() {
        @Override
        public Object call(Object... arguments) throws IllegalArgumentException {
            if (arguments.length != 1)
                throw new IllegalArgumentException("empty() function expects exactly 1 argument, " + arguments.length + " given.");
            Object x = arguments[0];
            return (x == null || "".equals(x))
                    || ((x instanceof Collection) && ((Collection) x).size() == 0)
                    || ((x instanceof Iterable) && !((Iterable) x).iterator().hasNext());
        }
    };

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}
