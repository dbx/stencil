package io.github.erdos.stencil;

import io.github.erdos.stencil.functions.Function;
import io.github.erdos.stencil.impl.CachingTemplateFactory;
import io.github.erdos.stencil.impl.LibreOfficeConverter;
import io.github.erdos.stencil.impl.NativeEvaluator;
import io.github.erdos.stencil.impl.NativeTemplateFactory;
import org.jodconverter.office.OfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

/**
 * Used to control the document creation process.
 * <p>
 * The process instance has lifecycle too. Make sure to start/stop it.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class Process implements TemplateFactory {

    private final static Logger LOGGER = LoggerFactory.getLogger(Process.class);

    private final Converter converter;
    private final TemplateFactory templateFactory;
    private final NativeEvaluator evaluator = new NativeEvaluator();

    Process(File libreOfficeHome) {
        converter = new LibreOfficeConverter(libreOfficeHome);
        templateFactory = new CachingTemplateFactory(new NativeTemplateFactory(converter));
    }

    Process(OfficeManager officeManager) {
        converter = new LibreOfficeConverter(officeManager);
        templateFactory = new CachingTemplateFactory(new NativeTemplateFactory(converter));
    }

    /**
     * Starts the embedded converter instance.
     *
     * @throws IllegalArgumentException when already started.
     */
    public void start() {
        converter.start();
    }

    /**
     * Stops the embedded converter instance.
     *
     * @throws IllegalStateException when already stopped or not yet been started.
     */
    public void stop() {
        converter.stop();
    }

    /**
     * Registers functions to his evaluator engine.
     * The registered functions can be called from inside template documents.
     *
     * @param functions vary array of function instances.
     * @throws IllegalArgumentException when any arg is null
     */
    public void registerFunctions(Function... functions) {
        evaluator.getFunctionEvaluator().registerFunctions(functions);
    }

    /**
     * Renders a preprocessed template.
     *
     * @param template     preprocessed template
     * @param templateData data to fill template with
     * @param outputFile   rendered document is written to this file
     * @throws IOException              on file system err
     * @throws IllegalArgumentException when any argument is null
     */
    public void renderTemplate(PreparedTemplate template, TemplateData templateData, File outputFile) throws IOException {
        if (outputFile == null)
            throw new IllegalArgumentException("Output File is null!");
        if (template == null)
            throw new IllegalArgumentException("Template is null!");
        if (templateData == null)
            throw new IllegalArgumentException("Template Data is null!");

        final Optional<OutputDocumentFormats> format = OutputDocumentFormats.ofExtension(outputFile.getName());

        if (!format.isPresent())
            throw new IllegalArgumentException("Unexpected format for file name: " + outputFile.getName());

        final EvaluatedDocument rendered = evaluator.render(template, templateData);

        try (InputStream stream = converter.convert(rendered, format.get()).getOutput()) {
            Files.copy(stream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Calls prepareTemplateFile and renderTemplate as one step.
     *
     * @param templateFile raw template document
     * @param templateData data to fill template with
     * @param outputFile   result document file
     * @throws IOException              on file system io error
     * @throws IllegalArgumentException when any argument is null or file types are unknown
     */
    public void render(File templateFile, TemplateData templateData, File outputFile) throws IOException {
        renderTemplate(prepareTemplateFile(templateFile), templateData, outputFile);
    }

    @Override
    public PreparedTemplate prepareTemplateFile(File templateFile) throws IOException {
        long before = 0;
        if (LOGGER.isDebugEnabled()) {
            before = System.currentTimeMillis();
        }

        final PreparedTemplate result = templateFactory.prepareTemplateFile(templateFile);
        LOGGER.info("Prepared template file {} at {}", templateFile, result.creationDateTime());

        if (LOGGER.isDebugEnabled()) {
            long after = System.currentTimeMillis();
            LOGGER.debug("Template file {} took {}ms", templateFile, after - before);
        }

        return result;
    }
}
