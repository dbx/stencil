package io.github.erdos.stencil.standalone;

import io.github.erdos.stencil.InputDocumentFormats;
import io.github.erdos.stencil.OutputDocumentFormats;
import io.github.erdos.stencil.Process;
import io.github.erdos.stencil.ProcessFactory;
import io.github.erdos.stencil.impl.LibreOfficeConverter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class OnlyConversionTest {


    @Disabled("Csak debugra")
    @Test
    void test() throws IOException {


        InputStream templateFile = null;

        Process process = ProcessFactory.fromLocalLibreOffice();
        process.start();

        InputStream result = ((LibreOfficeConverter) (process.getConverter())).convert(templateFile, InputDocumentFormats.DOCX, OutputDocumentFormats.PDF);

        process.stop();

        assertNotNull(result);
    }
}
