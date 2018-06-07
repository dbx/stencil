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

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertTrue;

/**
 * Checks simple substitutions and iterations on an example file.
 */
@Category(IntegrationTest.class)
public class SimpleRenderTest {

    private final static URL TEMPLATE_URL = SimpleRenderTest.class.getClassLoader().getResource("tests/test1.docx");
    private final static File TEMPLATE_FILE = new File(requireNonNull(TEMPLATE_URL).getFile());

    private Process process;
    private File outputFile;

    @Before
    public void setUp() throws IOException {
        process = ProcessFactory.fromLocalLibreOffice();
        process.start();
        outputFile = File.createTempFile("stencil", "test.txt");

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
        data.put("customer", singletonMap("fullName", "John"));
        data.put("listItems", asList(1, "2", "three"));

        data.put("roundable", 3.1415);

        data.put("tableItems", asList(map("name", "Pump", "value", 1000, "amt", 12),
                map("name", "Sponge", "value", 2000, "amt", 1),
                map("name", "Soap", "value", 3000, "amt", 3)));

        // WHEN
        process.render(TEMPLATE_FILE, TemplateData.fromMap(data), outputFile);
        String output = getOutputContents();

        // THEN

        // simple substitutions
        assertTrue(output.contains("Dear John!"));

        // table iterations
        assertTrue(output.contains("Pump"));
        assertTrue(output.contains("Sponge"));
        assertTrue(output.contains("Soap"));

        // list iterations
        assertTrue(output.contains("Item: 1 pcs"));
        assertTrue(output.contains("Item: 2 pcs"));
        assertTrue(output.contains("Item: three pcs"));

        // rounding fn call
        assertTrue(output.contains("Round 3.1 to 3!"));
        assertTrue(output.contains("Round 3.5 to 4!"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTemplateMetadata() throws IOException {
        // GIVEN

        // WHEN
        final PreparedTemplate template = process.prepareTemplateFile(TEMPLATE_FILE);
        final Set<String> variables = template.getVariables().getAllVariables();

        // THEN
        assertTrue(variables.contains("customer.fullName"));
        assertTrue(variables.contains("listItems"));
        // TODO: valamiert ezt mintha nem talalna meg.
        // assertTrue(variables.contains("tableItems.name"));
    }


    private String getOutputContents() throws IOException {
        return new String(Files.readAllBytes(outputFile.toPath()));
    }

    private Map<String, Object> map(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        Map<String, Object> out = new HashMap<>();
        out.put(k1, v1);
        out.put(k2, v2);
        out.put(k3, v3);
        return out;
    }
}
