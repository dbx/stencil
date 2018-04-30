package io.github.erdos.stencil.test;

import io.github.erdos.stencil.Process;
import io.github.erdos.stencil.ProcessFactory;
import io.github.erdos.stencil.TemplateData;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Checks simple function calls.
 */
public class SimpleRender2Test {

    private final static URL TEMPLATE_URL = SimpleRender2Test.class.getClassLoader().getResource("tests/test2.odt");
    private final static File TEMPLATE_FILE = new File(TEMPLATE_URL.getFile());

    private Process process;
    private File outputFile;

    @Before
    public void setUp() throws IOException {
        process = ProcessFactory.fromLibreOfficeHome(new File("/usr/lib64/libreoffice"));
        process.start();
        outputFile = File.createTempFile("stencil", "test.txt");
    }

    @After
    public void tearDown() {
        process.stop();
        process = null;
    }

    @Test
    @Ignore
    @SuppressWarnings("unchecked")
    public void test() throws IOException {
        // GIVEN
        final Map data = new HashMap();

        data.put("x", "Geza kek az eg");
        data.put("y", "bela");

        // WHEN
        process.render(TEMPLATE_FILE, TemplateData.fromMap(data), outputFile);
        String output = getOutputContents();

        // THEN

        // simple string functions
        assertTrue(output.contains("Uppercase x is GEZA KEK AZ EG!"));
        assertTrue(output.contains("Lowercase x is geza kek az eg!"));
        assertTrue(output.contains("Titlecase x is Geza Kek Az Eg!"));
        assertTrue(output.contains("Double x is Geza kek az egGeza kek az eg!"));

        assertTrue(output.contains("Formatted hexadecimal number: 7b!"));
    }

    private String getOutputContents() throws IOException {
        return new String(Files.readAllBytes(outputFile.toPath()));
    }
}