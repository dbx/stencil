package io.github.erdos.stencil;

import org.junit.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Ignore
public class ProcessTest {

    private static Process process;

    @BeforeClass
    public static void startProcess() {
        process = ProcessFactory.fromLocalLibreOffice();
        process.start();
    }

    @AfterClass
    public static void stopProcess() {
        process.stop();
        process = null;
    }

    @Test
    public void testDistinct() {
        //should not throw exception
        try {
            final Path tempSourcePath = Files.createTempFile("process-test", ".docx");
            final Path tempTargetPath = Files.createTempFile("process-test-target", ".docx");
            copyResource("templates/distinct.docx", tempSourcePath);
            final PreparedTemplate prepared = process.prepareTemplateFile(tempSourcePath.toFile());
            process.renderTemplate(prepared, TemplateData.empty(), tempTargetPath.toFile());
            tempSourcePath.toFile().deleteOnExit();
            tempTargetPath.toFile().deleteOnExit();
        } catch (Exception e) {
            Assert.fail("Render should not throw exception: " + e.getMessage());
        }
    }

    private static void copyResource(String resourceName, Path target) throws IOException {
        final InputStream is = Objects.requireNonNull(ProcessTest.class.getClassLoader().getResourceAsStream(resourceName),
                "Cannot find resource: " + resourceName);
        Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
    }

}
