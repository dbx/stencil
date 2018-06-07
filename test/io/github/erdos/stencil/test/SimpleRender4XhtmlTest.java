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

import static java.util.Objects.requireNonNull;

/**
 * Checks simple function calls.
 */
@Category(IntegrationTest.class)
public class SimpleRender4XhtmlTest {

    private final static URL TEMPLATE_URL = SimpleRender4XhtmlTest.class.getClassLoader().getResource("tests/3/test-table-3.xml");
    private final static File TEMPLATE_FILE = new File(requireNonNull(TEMPLATE_URL).getFile());

    private PreparedTemplate template;
    private Process process;
    private File outputFile;

    @Before
    public void setUp() throws IOException {
        process = ProcessFactory.fromLocalLibreOffice();
        process.start();
        outputFile = File.createTempFile("stencil", "test.xml");
        template = process.prepareTemplateFile(TEMPLATE_FILE);
    }

    @After
    public void tearDown() {
        process.stop();
        process = null;
    }

    @Test
    @SuppressWarnings({"unchecked", "unused"})
    public void test() throws IOException {
        process.renderTemplate(template, TemplateData.fromMap(new HashMap()), outputFile);
        String output = getOutputContents();

//        System.out.println(">>>");
//        System.out.println(output);
        // TODO: itt valami ellenorzes kell
    }

    private String getOutputContents() throws IOException {
        return new String(Files.readAllBytes(outputFile.toPath()));
    }
}