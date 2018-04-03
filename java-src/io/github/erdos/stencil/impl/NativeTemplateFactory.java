package io.github.erdos.stencil.impl;

import clojure.lang.IFn;
import clojure.lang.Keyword;
import io.github.erdos.stencil.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import static io.github.erdos.stencil.impl.FileHelper.extension;
import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;

@SuppressWarnings("unused")
public final class NativeTemplateFactory implements TemplateFactory {

    private final Converter converter;

    public NativeTemplateFactory(Converter converter) {
        this.converter = converter;
    }

    private Optional<TemplateDocumentFormats> templateFormatFor(File f) {
        return InputDocumentFormats.ofExtension(f.getName()).map(InputDocumentFormats::templateConvertTo);
    }

    private InputStream convertInputToSupported(File templateFile) throws IOException {
        if (templateFile == null)
            throw new IllegalArgumentException("A file paraméter hiányzik!");
        if (!templateFile.exists())
            throw new FileNotFoundException("A sablonfájl nem létezik!");

        final InputDocumentFormats inputFormat = InputDocumentFormats
                .ofExtension(templateFile.getName())
                .orElseThrow(() -> new IllegalArgumentException("Ismeretlen fajl kiterjesztes!"));

        final TemplateDocumentFormats templateFormat = inputFormat.templateConvertTo();

        if (!inputFormat.templateShouldConvert()) {
            return new FileInputStream(templateFile);
        } else {
            return convertTo(templateFile, templateFormat.asOutputFormat());
        }
    }


    private InputStream convertTo(File f, OutputDocumentFormats format) throws IOException {
        final InputDocumentFormats inputFormat = InputDocumentFormats
                .ofExtension(extension(f))
                .orElseThrow(() -> new IllegalArgumentException("Could not recognize file type for " + f));

        return ((LibreOfficeConverter) converter).convert(new FileInputStream(f), inputFormat, format);
    }

    @Override
    public PreparedTemplate prepareTemplateFile(final File inputTemplateFile) throws IOException {
        final Optional<TemplateDocumentFormats> templateDocFormat = templateFormatFor(inputTemplateFile);

        if (!templateDocFormat.isPresent())
            throw new IllegalArgumentException("Unexpected type of file: " + inputTemplateFile.getName());

        try (InputStream input = convertInputToSupported(inputTemplateFile)) {
            return prepareTemplateImpl(templateDocFormat.get(), input);
        }
    }

    @SuppressWarnings("unchecked")
    private PreparedTemplate prepareTemplateImpl(TemplateDocumentFormats templateDocFormat, InputStream input) {
        final IFn prepareFunction = ClojureHelper.findFunction("prepare-template");

        Map<Keyword, Object> prepared = (Map<Keyword, Object>) prepareFunction.invoke(templateDocFormat.name(), input);

        final String templateName = (String) prepared.get(Keyword.intern("template-name"));
        final File templateFile = (File) prepared.get(Keyword.intern("template-file"));

        final Set variablesSet = prepared.containsKey(ClojureHelper.KV_VARIABLES)
                ? unmodifiableSet(new HashSet<Set>((Collection) prepared.get(ClojureHelper.KV_VARIABLES)))
                : emptySet();

        final LocalDateTime now = LocalDateTime.now();

        return new PreparedTemplate() {
            @Override
            public String getName() {
                return templateName;
            }

            @Override
            public File getTemplateFile() {
                return templateFile;
            }

            @Override
            public LocalDateTime creationDateTime() {
                return now;
            }

            @Override
            public Object getSecretObject() {
                return prepared;
            }

            @Override
            public Set<String> getVariables() {
                return variablesSet;
            }
        };
    }
}