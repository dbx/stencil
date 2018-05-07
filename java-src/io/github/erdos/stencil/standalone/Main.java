package io.github.erdos.stencil.standalone;

import io.github.erdos.stencil.PreparedTemplate;
import io.github.erdos.stencil.Process;
import io.github.erdos.stencil.ProcessFactory;
import io.github.erdos.stencil.TemplateData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;

import static io.github.erdos.stencil.impl.ClojureHelper.callShutdownAgents;
import static io.github.erdos.stencil.impl.FileHelper.removeExtension;

/**
 * Standalone template engine runner.
 */
@SuppressWarnings("unused")
public final class Main {

    public static void main(String... args) throws IOException {
        final Process process = ProcessFactory.fromLocalLibreOffice();
        final ArgsParser parsedArgs = ArgsParser.parse(args);

        if (parsedArgs.getDataFiles().isEmpty())
            throw new IllegalArgumentException("Missing data files to export!");

        process.start();
        final File templateFile = parsedArgs.getTemplateFile();
        final PreparedTemplate template = process.prepareTemplateFile(templateFile);

        if (parsedArgs.isPrintTemplateInfo()) {
            printTemplateInfo(template);
        }

        try {
            for (File dataFile : parsedArgs.getDataFiles()) {
                final String outputFileName = String.format("%s-%s.%s", removeExtension(templateFile), removeExtension(dataFile), parsedArgs.getOutputFormat().getExtension());
                final File outputFile = new File(templateFile.getParentFile(), outputFileName);
                final TemplateData data = readTemplateData(dataFile);

                System.out.printf("Rendering data file %s to file %s \n", dataFile.toString(), outputFile.toString());
                process.renderTemplate(template, data, outputFile);
            }
        } finally {
            process.stop();
            callShutdownAgents();
        }
    }

    private static void printTemplateInfo(PreparedTemplate template) {
        System.out.println("Template file: " + template.getTemplateFile());
        System.out.println("Template arguments:");
        template.getVariables().forEach(line -> System.out.println(" - " + line));
    }

    @SuppressWarnings("unchecked")
    private static TemplateData readTemplateData(File dataFile) throws IOException {
        Optional<Object> parsed = JsonParser.parse(new String(Files.readAllBytes(dataFile.toPath())));
        if (!parsed.isPresent())
            throw new IllegalArgumentException("Could not parse JSON file: " + dataFile);
        return TemplateData.fromMap((Map) parsed.get());
    }
}
