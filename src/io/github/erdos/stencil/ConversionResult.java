package io.github.erdos.stencil;

import java.util.Optional;


/**
 * Result holder of a document type conversion call.
 *
 * @param <TO> conversion result type. Usually InputStream or File.
 */
public final class ConversionResult<TO> {

    private final OutputDocumentFormats format;
    private final TO output;
    private final Integer pageCount;

    public ConversionResult(OutputDocumentFormats format, TO output, Integer pageCount) {
        if (format == null || output == null)
            throw new IllegalArgumentException("Null args are forbidden!");

        this.format = format;
        this.output = output;
        this.pageCount = pageCount;
    }

    TO getOutput() {
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
