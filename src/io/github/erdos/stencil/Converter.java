package io.github.erdos.stencil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Conversion of evaluated template to a desired file format.
 * <p>
 * This service has a lifecycle. It needs to be started before the first conversion call.
 */
@SuppressWarnings("unused")
public interface Converter {

    /**
     * Starts the converter service. Thread safe.
     *
     * @throws IllegalStateException if the service has already been started.
     */
    void start() throws IllegalStateException;

    /**
     * Stops the converter service. Thread safe.
     *
     * @throws IllegalStateException if the service has not yet been started.
     */
    void stop() throws IllegalStateException;

    /**
     * Converts an evaluated template t oa desired file format.
     *
     * @param document     not null document to convert
     * @param outputFormat not null target file format
     * @return wrapped input stream of file in a desired format. Not null.
     * @throws IllegalStateException if service has not yet been started.
     * @throws NullPointerException  when any argument is null
     * @throws IOException           of file system errors or converter service errors
     */
    ConversionResult<InputStream> convert(EvaluatedDocument document, OutputDocumentFormats outputFormat) throws IllegalStateException, IOException;

    /**
     * Just like convert() but result is a temporary file.
     * <p>
     * Result file must be copied/moved to an other location as it might be deleted after the program quits.
     *
     * @param document     not null document to convert
     * @param outputFormat not null target file format
     * @return wrapped file in a desired format. Not null.
     * @throws IllegalStateException when service has not yet been started
     * @throws NullPointerException  when any argument is null
     * @throws IOException           on file system error on converter service error
     */
    default ConversionResult<File> convertToFile(EvaluatedDocument document, OutputDocumentFormats outputFormat)
            throws IllegalStateException, IOException {
        final Path out = Files.createTempFile("stencil-out-", "." + outputFormat.getExtension());
        final File outFile = out.toFile();

        ConversionResult<InputStream> result = convert(document, outputFormat);
        try (FileOutputStream outputStream = new FileOutputStream(outFile);
             InputStream input = result.getOutput()) {
            int read;
            byte[] bytes = new byte[1024];

            while ((read = input.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        }

        return new ConversionResult<>(result.getOutputFormat(), outFile, result.getPageCount().orElse(null));
    }
}

