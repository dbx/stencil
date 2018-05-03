package io.github.erdos.stencil.standalone;

import io.github.erdos.stencil.OutputDocumentFormats;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.github.erdos.stencil.impl.FileHelper.extension;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

/**
 * Parses CLI arg list.
 */
public final class ArgsParser {

    private final OutputDocumentFormats outputFormat;
    private final File templateFile;
    private final List<File> dataFiles;

    private ArgsParser(OutputDocumentFormats outputFormat, File templateFile, List<File> dataFiles) {
        this.outputFormat = outputFormat;
        this.templateFile = templateFile;
        this.dataFiles = dataFiles;
    }

    /**
     * Tries to read first 1-2 elems of a queue.
     */
    private static <T> Optional<T> maybeRead(Queue<String> items, String shortKey, String longKey, Function<String, T> parser) {
        if (items.isEmpty()) {
            return Optional.empty();
        } else if (items.element().equals(shortKey)) {
            items.remove();
            return Optional.of(parser.apply(items.poll()));
        } else if (items.element().startsWith(shortKey)) {
            return Optional.of(parser.apply(requireNonNull(items.poll()).substring(shortKey.length())));
        } else if (items.element().equals(longKey)) {
            items.remove();
            return Optional.of(parser.apply(items.remove()));
        } else if (items.element().startsWith(longKey + "=")) {
            String poke = items.remove();
            return Optional.of(parser.apply(poke.substring(0, poke.length() - 1)));
        } else
            return Optional.empty();
    }

    /**
     * Parses CLI args and returns an ArgsParser instance
     *
     * @param args CLI arg list
     * @return new instance holding arguments
     * @throws IllegalArgumentException on arg format error
     */
    public static ArgsParser parse(String... args) {

        final Queue<String> arguments = new LinkedList<>(asList(args));

        OutputDocumentFormats outputFormat = null;

        while (!arguments.isEmpty()) {
            Optional<OutputDocumentFormats> mOut = maybeRead(arguments, "-T", "--output-type", (x) -> OutputDocumentFormats.ofExtension(x).orElseThrow(() -> new RuntimeException("Unexpected output format.")));
            if (mOut.isPresent()) {
                outputFormat = mOut.get();
                continue;
            }

            File templateFile = new File(arguments.remove());
            List<File> dataFiles = arguments.stream().map(File::new).collect(toList());

            if (outputFormat == null)
                outputFormat = OutputDocumentFormats.ofExtension(extension(templateFile))
                        .orElseThrow(iae("Can not parse template file extension '%s' as output type", extension(templateFile)));
            return new ArgsParser(outputFormat, templateFile, dataFiles);
        }

        throw iae("Template file parameter is missing!").get();
    }

    private static Supplier<IllegalArgumentException> iae(String msg, String... args) {
        return () -> new IllegalArgumentException(String.format(msg, (Object[]) args));
    }

    public File getTemplateFile() {
        return templateFile;
    }

    public List<File> getDataFiles() {
        return unmodifiableList(dataFiles);
    }

    public OutputDocumentFormats getOutputFormat() {
        return outputFormat;
    }
}
