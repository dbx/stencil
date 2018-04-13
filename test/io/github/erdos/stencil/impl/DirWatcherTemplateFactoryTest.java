package io.github.erdos.stencil.impl;

import io.github.erdos.stencil.PreparedTemplate;
import io.github.erdos.stencil.TemplateFactory;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DirWatcherTemplateFactoryTest implements TemplateFactory {

    private final Set<File> calledFiles = new HashSet<>();
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void cleanup() {
        calledFiles.clear();
    }

    @Test
    public void testLoadFilesOnStartup() throws IOException, InterruptedException {

        File folder = temporaryFolder.newFolder();
        System.out.println("Temp folder is: " + folder);

        DirWatcherTemplateFactory factory = new DirWatcherTemplateFactory(folder, this);
        factory.start();

        File file1 = new File(folder, "asd").getAbsoluteFile();

        (new FileOutputStream(file1)).close();
        assertFalse(calledFiles.contains(file1));
        Thread.sleep(1100L);
        assertTrue(calledFiles.contains(file1));
        factory.stop();
    }

    @Override
    public PreparedTemplate prepareTemplateFile(File templateFile) throws IOException {
        System.out.println("Adding: " + templateFile);
        calledFiles.add(templateFile);
        return new PreparedTemplate() {
            @Override
            public String getName() {
                return "asd";
            }

            @Override
            public File getTemplateFile() {
                return templateFile;
            }

            @Override
            public LocalDateTime creationDateTime() {
                return null;
            }

            @Override
            public Object getSecretObject() {
                return null;
            }

            @Override
            public Set<String> getVariables() {
                return null;
            }
        };
    }
}