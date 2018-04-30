package io.github.erdos.stencil.test;

import io.github.erdos.stencil.PreparedTemplate;
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

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertFalse;

/**
 * Check that table columns are hidden.
 */
@Ignore
public class DocxTableColumnsTest {

    private final static URL TEMPLATE_URL = DocxTableColumnsTest.class.getClassLoader().getResource("tests/table_columns/test.docx");
    private final static File TEMPLATE_FILE = new File(requireNonNull(TEMPLATE_URL).getFile());

    private PreparedTemplate template;
    private Process process;
    private File outputFile;

    @Before
    public void setUp() throws IOException {
        process = ProcessFactory.fromLibreOfficeHome(new File("/usr/lib64/libreoffice"));
        process.start();
        outputFile = File.createTempFile("stencil", "test.txt");
        template = process.prepareTemplateFile(TEMPLATE_FILE);
    }

    @After
    public void tearDown() {
        process.stop();
        process = null;
    }

    @Test public void print () {
        System.out.println(">" + template.getSecretObject());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test() throws IOException {
        // GIVEN
        final Map data = new HashMap();

        data.put("customerName", "DBX Kft.");
        data.put("y", "bela");

        System.out.println("---");

        // WHEN
        process.renderTemplate(template, TemplateData.fromMap(data), outputFile);
        String output = getOutputContents();
        System.out.println("---");

        System.out.println(output);

        // these cells are hidden because of the hideTableColumnMarker marker in cells.
        for (String cellValue : asList("A1", "A2", "A3", "C1", "C2", "C3", "E1", "E2", "E3"))
            assertFalse(output.contains(cellValue));
    }

    private String getOutputContents() throws IOException {
        return new String(Files.readAllBytes(outputFile.toPath()));
    }
}