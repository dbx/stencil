package io.github.erdos.stencil.functions;

/**
 * Common numeric functions.
 */
@SuppressWarnings("unused")
public enum NumberFunctions implements Function {

    /**
     * Rounds a number to the closest integer.
     * <p>
     * Expects 1 argument and returns null on null argument.
     */
    ROUND() {
        @Override
        public Object call(Object... arguments) {
            if (arguments.length != 1)
                throw new IllegalArgumentException("A round() fuggveny pontosan 1 parametert var!");
            else if (arguments[0] == null)
                return null;
            else if (!(arguments[0] instanceof Number))
                throw new IllegalArgumentException("A round() fuggveny parametere nem szam!");
            else
                return Math.round(((Number) arguments[0]).doubleValue());
        }
    };

    @Override
    public String getName() {
        return name().toLowerCase();
    }
}
