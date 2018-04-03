package io.github.erdos.stencil.test;

import io.github.erdos.stencil.OutputDocumentFormats;
import io.github.erdos.stencil.impl.LibreOfficeConverter;
import io.github.erdos.stencil.impl.ZipHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static io.github.erdos.stencil.InputDocumentFormats.RTF;

public class LibreOfficeConverterTest {


    /*
    public static void main(String... args) throws IOException, InterruptedException {

        LibreOfficeConverter converter = new LibreOfficeConverter(new File("/usr/lib64/libreoffice"));

        converter.start();

        try (FileInputStream fis = new FileInputStream(new File("/home/erdos/sablon2_masik_formatum.rtf"));
             InputStream pdfStream = converter.convert(fis, RTF, OutputDocumentFormats.DOCX)) {
            ZipHelper.unzipStreamIntoDirectory(pdfStream, new File("/tmp/3"));

//        Files.copy(pdfStream, new File("/home/erdos/aaa.pdf").toPath());
        } finally {
            converter.stop();
        }
    }
    */
}