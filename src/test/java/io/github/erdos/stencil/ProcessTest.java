package io.github.erdos.stencil;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.fail;

@Disabled
@IntegrationTest
class ProcessTest {

    private static Process process;

    @BeforeAll
    static void startProcess() {
        process = ProcessFactory.fromLocalLibreOffice();
        process.start();
    }

    @AfterAll
    static void stopProcess() {
        process.stop();
        process = null;
    }

    @Test
    void testDistinct() {
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
            fail("Render should not throw exception: " + e.getMessage());
        }
    }

    private static void copyResource(String resourceName, Path target) throws IOException {
        final InputStream is = Objects.requireNonNull(ProcessTest.class.getClassLoader().getResourceAsStream(resourceName),
                "Cannot find resource: " + resourceName);
        Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
    }

}
