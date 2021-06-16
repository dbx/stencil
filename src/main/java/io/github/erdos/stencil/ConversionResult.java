package io.github.erdos.stencil;

import java.util.Optional;


/**
 * Result holder of a document type conversion call.
 *
 * @param <T> conversion result type. Usually InputStream or File.
 */
public final class ConversionResult<T> {

    private final OutputDocumentFormats format;
    private final T output;
    private final Integer pageCount;

    public ConversionResult(OutputDocumentFormats format, T output, Integer pageCount) {
        if (format == null || output == null)
            throw new IllegalArgumentException("Null args are forbidden!");

        this.format = format;
        this.output = output;
        this.pageCount = pageCount;
    }

    T getOutput() {
        return output;
    }

    /**
     * Number of pages if possible to calculate.
     */
    Optional<Integer> getPageCount() {
        return Optional.ofNullable(pageCount);
    }

    /**
     * Output format type.
     *
     * @return never null.
     */
    OutputDocumentFormats getOutputFormat() {
        return format;
    }

    /**
     * How many milliseconds it took to do the conversion.
     * <p>
     * Not yet implemented!
     */
    @SuppressWarnings("unused")
    long conversionTimeMillis() {
        return 0L;
    }
}
