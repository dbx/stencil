package io.github.erdos.stencil;

import io.github.erdos.stencil.functions.Function;
import io.github.erdos.stencil.impl.LibreOfficeConverter;
import org.apache.commons.lang3.time.StopWatch;
import org.jodconverter.core.office.OfficeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Used to control the document creation process.
 * <p>
 * The process instance has lifecycle too. Make sure to start/stop it.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Process implements TemplateFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(Process.class);

    private final Converter converter;

    Process(File libreOfficeHome) {
        converter = new LibreOfficeConverter(libreOfficeHome);
    }

    Process(OfficeManager officeManager) {
        converter = new LibreOfficeConverter(officeManager);
    }

    /**
     * Starts the embedded converter instance.
     *
     * @throws IllegalArgumentException
     *         when already started.
     */
    public void start() {
        converter.start();
    }

    /**
     * Stops the embedded converter instance.
     *
     * @throws IllegalStateException
     *         when already stopped or not yet been started.
     */
    public void stop() {
        converter.stop();
    }

    /**
     * Renders a preprocessed template.
     *
     * @param template
     *         preprocessed template
     * @param templateData
     *         data to fill template with
     * @param outputFile
     *         rendered document is written to this file
     *
     * @throws IOException
     *         on file system err
     * @throws IllegalArgumentException
     *         when any argument is null
     */
    public void renderTemplate(PreparedTemplate template, TemplateData templateData, File outputFile) throws IOException {
        renderTemplate(template, null, templateData, outputFile, null);
    }

    /**
     * Renders a preprocessed template.
     *
     * @param template
     *         preprocessed template
     * @param fragments
     *         preprocessed fragments
     * @param templateData
     *         data to fill template with
     * @param outputFile
     *         rendered document is written to this file
     * @param customFunctions
     *         custom functions to register
     *
     * @throws IOException
     *         on file system err
     * @throws IllegalArgumentException
     *         when any argument is null
     */
    public void renderTemplate(PreparedTemplate template, Map<String, PreparedFragment> fragments,
                               TemplateData templateData, File outputFile, final List<Function> customFunctions) throws IOException {
        Objects.requireNonNull(outputFile, "Output File is null!");
        Objects.requireNonNull(template, "Template is null");
        Objects.requireNonNull(templateData, "Template data is null");
        final Map<String, PreparedFragment> preparedFragments = Optional.ofNullable(fragments).orElseGet(Collections::emptyMap);
        preparedFragments.entrySet().stream().filter(fragmentEntry -> fragmentEntry.getValue() == null)
                .findFirst()
                .map(Map.Entry::getKey).ifPresent(k -> {
                    throw new IllegalArgumentException("Fragment '" + k + "' is null!");
                });

        final Optional<OutputDocumentFormats> format = OutputDocumentFormats.ofExtension(outputFile.getName());

        if (!format.isPresent()) {
            throw new IllegalArgumentException("Unexpected format for file name: " + outputFile.getName());
        }

        EvaluatedDocument rendered = API.render(template, preparedFragments, templateData, customFunctions);

        try (InputStream stream = converter.convert(rendered, format.get()).getOutput()) {
            Files.copy(stream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public Converter getConverter() {
        return converter;
    }

    /**
     * Calls prepareTemplateFile and renderTemplate as one step.
     *
     * @param templateFile
     *         raw template document
     * @param templateData
     *         data to fill template with
     * @param outputFile
     *         result document file
     *
     * @throws IOException
     *         on file system io error
     * @throws IllegalArgumentException
     *         when any argument is null or file types are unknown
     */
    public void render(File templateFile, TemplateData templateData, File outputFile) throws IOException {
        renderTemplate(prepareTemplateFile(templateFile), templateData, outputFile);
    }

    @Override
    public PreparedTemplate prepareTemplateFile(File templateFile, PrepareOptions prepareOptions) throws IOException {
        StopWatch stopWatch = null;
        final boolean isDebugLogging = LOGGER.isDebugEnabled();
        if (isDebugLogging) {
            stopWatch = new StopWatch();
            stopWatch.start();
        }

        final PreparedTemplate prepared = API.prepare(templateFile, prepareOptions);
        LOGGER.info("Prepared template file {} at {}", templateFile, prepared.creationDateTime());

        if (isDebugLogging) {
            stopWatch.stop();
            LOGGER.debug("Template file {} took {}ms", templateFile, stopWatch.getTime());
        }

        return prepared;
    }
}
