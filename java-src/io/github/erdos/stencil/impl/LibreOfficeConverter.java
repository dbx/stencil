package io.github.erdos.stencil.impl;

import io.github.erdos.stencil.*;
import org.jodconverter.LocalConverter;
import org.jodconverter.document.DefaultDocumentFormatRegistry;
import org.jodconverter.document.DocumentFormat;
import org.jodconverter.office.LocalOfficeManager;
import org.jodconverter.office.OfficeException;
import org.jodconverter.office.OfficeManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Default implementation that uses JODConverter.
 */
@SuppressWarnings("unused")
public class LibreOfficeConverter implements Converter {
    private final OfficeManager officeManager;
    private final AtomicBoolean started = new AtomicBoolean(false);

    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Constructs a new converter instance for a given LibreOffice installation path
     *
     * @param officeHome location of installed LibreOffice
     * @throws IllegalArgumentException when officeHome is missing, does not exist or invalid
     */
    public LibreOfficeConverter(final File officeHome) {
        if (officeHome == null)
            throw new IllegalArgumentException("Az Office Home parameter hianyzik!");
        if (!officeHome.exists())
            throw new IllegalArgumentException("Office Home mappa nem letezik: " + officeHome.toString());
        if (!new File(officeHome, "program/soffice.bin").exists())
            throw new IllegalArgumentException("Office home nem korrekt: hianyzik a program/soffice.bin fajlt!");

        this.officeManager = LocalOfficeManager.builder().officeHome(officeHome).build();
    }

    /**
     * Constructs a new converter using an existing office manager.
     * Sets current running status to argument's status.
     *
     * @param officeManager custom manager implementation
     * @throws IllegalArgumentException when office manager is missing
     */
    public LibreOfficeConverter(final OfficeManager officeManager) {
        if (officeManager == null)
            throw new IllegalArgumentException("Office Manager is missing!");
        this.officeManager = officeManager;
        started.set(officeManager.isRunning());
    }


    @Override
    public void start() {
        if (started.compareAndSet(false, true)) {
            try {
                officeManager.start();
            } catch (OfficeException e) {
                throw new IllegalStateException(e);
            }
        } else
            throw new IllegalStateException("LocalOfficeManager has already been started!");
    }


    @Override
    public void stop() {
        if (started.compareAndSet(true, false)) {
            try {
                officeManager.stop();
            } catch (OfficeException e) {
                throw new IllegalStateException(e);
            }
        } else
            throw new IllegalStateException("Office manager has not been started yet!");
    }

    /**
     * Converts from an input stream (and closes it).
     *
     * @param inputStream  stream of input file contents
     * @param inputFormat  input file format
     * @param outputFormat output stream is expected in this file format
     * @return a new input stream with the conversion result
     * @throws IllegalStateException when converter has not yet been started
     * @throws IOException           on file system IO error
     */
    public InputStream convert(InputStream inputStream, InputDocumentFormats inputFormat, OutputDocumentFormats outputFormat) throws IllegalStateException, IOException {
        if (inputFormat.name().equals(outputFormat.name()))
            return inputStream;

        final DocumentFormat inputF = DefaultDocumentFormatRegistry.getFormatByExtension(inputFormat.getExtension());
        final DocumentFormat outputF = DefaultDocumentFormatRegistry.getFormatByExtension(outputFormat.getExtension());

        File temporartOutputFile = File.createTempFile("stencil", "temfile");

        try {
            LocalConverter
                    .make(officeManager)
                    .convert(inputStream)
                    .as(inputF)
                    .to(temporartOutputFile)
                    .as(outputF)
                    .execute();
        } catch (OfficeException e) {
            throw new IOException(e);
        }

        return new FileInputStream(temporartOutputFile);
    }

    @Override
    public ConversionResult<InputStream> convert(EvaluatedDocument document, OutputDocumentFormats outputFormat) throws IllegalStateException, IOException {
        if (!started.get())
            throw new IllegalStateException("Service has not yet been started!");

        final InputDocumentFormats inputFormat = InputDocumentFormats.valueOf(document.getFormat().name());
        final InputStream inputStream = convert(document.getInputStream(), inputFormat, outputFormat);
        return new ConversionResult<>(outputFormat, inputStream, null);
    }
}