package io.github.erdos.stencil.standalone;

import io.github.erdos.stencil.OutputDocumentFormats;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class ArgsParser {

    public static ArgsParser parse(String... args) {

        Iterator<String> iter = Arrays.asList(args).iterator();

        OutputDocumentFormats outputFormat;
        File templateFile;
        List<File> dataFiles;

        while (iter.hasNext()) {
            String arg = iter.next();

            if (arg.equals("-T") || arg.equals("--output-type")) {
                outputFormat = OutputDocumentFormats.ofExtension(iter.next()).get();
            } else if (arg.startsWith("--output-type=")) {
                outputFormat = OutputDocumentFormats.ofExtension(arg.substring(14)).get();
            } else if (arg.startsWith("-T")) {
                outputFormat = OutputDocumentFormats.ofExtension(arg.substring(2)).get();
            } else {

                // ha mar nincs tobb argumentum, a maradekot ugy ertelmezzuk, mint a sablonfajl es az adatfajlok.
                templateFile = new File(arg);
                dataFiles = new LinkedList<>();
                while (iter.hasNext()) {
                    dataFiles.add(new File(iter.next()));
                }
            }
        }

        return null;
    }
}
