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

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Check that table columns are hidden.
 */
@Category(IntegrationTest.class)
public class DocxTableColumnsTest {

    private final static URL TEMPLATE_URL = DocxTableColumnsTest.class.getClassLoader().getResource("tests/table_columns/test.docx");
    private final static File TEMPLATE_FILE = new File(requireNonNull(TEMPLATE_URL).getFile());

    private PreparedTemplate template;
    private Process process;
    private File outputFile;

    @Before
    public void setUp() throws IOException {
        process = ProcessFactory.fromLocalLibreOffice();
        process.start();
        outputFile = File.createTempFile("stencil", "test.txt");
        template = process.prepareTemplateFile(TEMPLATE_FILE);
    }

    @After
    public void tearDown() {
        process.stop();
        process = null;
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

        // THEN

        // these cells are still visilbe
        for (String cellValue : asList("B1", "B2", "B3", "D1", "D2", "D3"))
            assertTrue(output.contains(cellValue));

        // these cells are hidden because of the hideTableColumnMarker marker in cells.
        for (String cellValue : asList("A1", "A2", "A3", "C1", "C2", "C3", "E1", "E2", "E3"))
            assertFalse(output.contains(cellValue));

        // second table: visible cells.
        for (String cellValue : asList("H1", "J1", "H2+I2", "J2", "H3", "I3+J3", "H4", "J4", "F5+G5+H5+I5+J5"))
            assertTrue(output.contains(cellValue));

        // these columns are hidden thus removed from the document
        for (String cellValue : asList("F1", "I1", "F3+G3", "F4", "G4", "I4"))
            assertFalse("Should be hidden: " + cellValue, output.contains(cellValue));
    }

    private String getOutputContents() throws IOException {
        return new String(Files.readAllBytes(outputFile.toPath()));
    }
}