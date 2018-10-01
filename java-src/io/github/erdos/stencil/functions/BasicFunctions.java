package io.github.erdos.stencil.functions;

import java.util.Collection;

/**
 * Common general purpose functions.
 */
@SuppressWarnings("unused")
public enum BasicFunctions implements Function {

    /**
     * Selects value based on first argument.
     * Usage: switch(expression, case-1, value-1, case-2, value-2, ..., default-value)
     */
    SWITCH() {
        @Override
        public Object call(Object... arguments) throws IllegalArgumentException {
            if (arguments.length < 3) {
                throw new IllegalArgumentException("switch() function expects at least 3 args!");
            }
            final Object expr = arguments[0];
            for (int i = 1; i < arguments.length; i += 2) {
                final Object value = arguments[i];
                final Object result = arguments[i + 1];
                if (expr == null && value == null)
                    return result;
                else if (expr != null && expr.equals(value))
                    return result;
            }

            if (arguments.length % 2 == 0) {
                return arguments[arguments.length - 1];
            } else {
                return null;
            }
        }
    },

    /**
     * Returns the first non-null an non-empty value.
     * <p>
     * Accepts any arguments. Skips null values, empty strings and empty collections.
     */
    COALESCE() {
        @Override
        public Object call(Object... arguments) {
            for (Object arg : arguments)
                if (arg != null && !"".equals(arg) && (!(arg instanceof Collection) || !((Collection) arg).isEmpty()))
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
