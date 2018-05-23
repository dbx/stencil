package io.github.erdos.stencil.test;

import io.github.erdos.stencil.*;
import io.github.erdos.stencil.Process;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertTrue;

/**
 * Checks simple function calls.
 */
@Category(IntegrationTest.class)
public class SimpleRender3XhtmlTest {

    private final static URL TEMPLATE_URL = SimpleRender3XhtmlTest.class.getClassLoader().getResource("tests/2/input.xhtml");
    private final static File TEMPLATE_FILE = new File(requireNonNull(TEMPLATE_URL).getFile());

    private PreparedTemplate template;
    private Process process;
    private File outputFile;

    @Before
    public void setUp() throws IOException {
        process = ProcessFactory.fromLibreOfficeHome(new File("/usr/lib64/libreoffice"));
        process.start();
        outputFile = File.createTempFile("stencil", "test.xml");
        template = process.prepareTemplateFile(TEMPLATE_FILE);
    }

    @After
    public void tearDown() {
        process.stop();
        process = null;
    }

    /**
     * Ellenorizzuk, hogy minden sablon valtozot sikerult felolvasni.
     */
    @Test
    public void testTemplateVariables() {
        Set<String> variables = template.getVariables().getAllVariables();

        assertTrue(variables.contains("customerName"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test() throws IOException {
        // GIVEN
        final Map data = new HashMap();

        data.put("customerName", "DBX Kft.");
        data.put("y", "bela");

        // WHEN
        process.renderTemplate(template, TemplateData.fromMap(data), outputFile);
        String output = getOutputContents();

        System.out.println(output);

        // THEN

        // simple string functions
/*        assertTrue(output.contains("Uppercase x is GEZA KEK AZ EG!"));
        assertTrue(output.contains("Lowercase x is geza kek az eg!"));
        assertTrue(output.contains("Titlecase x is Geza Kek Az Eg!"));
        assertTrue(output.contains("Double x is Geza kek az egGeza kek az eg!"));

        assertTrue(output.contains("Formatted hexadecimal number: 7b!"));
        */
    }

    private String getOutputContents() throws IOException {
        return new String(Files.readAllBytes(outputFile.toPath()));
    }
}