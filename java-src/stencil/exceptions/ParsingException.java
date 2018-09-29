package stencil.exceptions;

/**
 * This class indicates an error while reading and parsing a stencil expression.
 */
public final class ParsingException extends RuntimeException {

    private final String expression;

    public static ParsingException fromMessage(String expression, String message) {
        return new ParsingException(expression, message);
    }

    private ParsingException(String expression, String message) {
        super(message);
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }
}
