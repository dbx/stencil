package stencil.test;

import stencil.impl.ZipHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertTrue;

public class ZipHelperTest {
    private final static URL TEMPLATE_URL = SimpleRender2Test.class.getClassLoader().getResource("tests/test2.odt");
    private final static File TEMPLATE_FILE = new File(requireNonNull(TEMPLATE_URL).getFile());

    @Rule
    public TemporaryFolder outputDirectoryFactory = new TemporaryFolder();

    private File outputDirectory;

    @Before
    public void setup() throws IOException {
        outputDirectory = outputDirectoryFactory.newFolder();
    }

    @Test
    public void unzipStreamIntoDirectoryTest() throws IOException {
        try (final InputStream fis = new FileInputStream(TEMPLATE_FILE)) {
            assertTrue(outputDirectory.delete());

            ZipHelper.unzipStreamIntoDirectory(fis, outputDirectory);

            final HashSet<String> files = new HashSet<>(asList(requireNonNull(outputDirectory.list())));

            assertTrue(files.contains("META-INF"));
            assertTrue(files.contains("settings.xml"));
            assertTrue(files.contains("Configurations2"));
            assertTrue(files.contains("manifest.rdf"));
            assertTrue(files.contains("Thumbnails"));
            assertTrue(files.contains("mimetype"));
            assertTrue(files.contains("meta.xml"));
            assertTrue(files.contains("styles.xml"));
            assertTrue(files.contains("content.xml"));
        }
    }
}
