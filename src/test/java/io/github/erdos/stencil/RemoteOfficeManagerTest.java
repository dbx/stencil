package io.github.erdos.stencil;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.remote.office.RemoteOfficeManager;
import org.jodconverter.remote.ssl.SslConfig;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Ignore
public class RemoteOfficeManagerTest {

    private static final GenericContainer<?> CONTAINER;
    private static final String URL;
    private static final int PORT = 9980;
    private static final Process PROCESS;

    static {
        CONTAINER = new GenericContainer<>(DockerImageName.parse("collabora/code"))
                .withExposedPorts(PORT)
                .waitingFor(new HttpWaitStrategy().allowInsecure().usingTls().forPort(PORT).forStatusCode(200));
        CONTAINER.start();
        URL = "https://" + CONTAINER.getHost() + ":" + CONTAINER.getMappedPort(PORT);

        final SslConfig sslConfig = new SslConfig();
        sslConfig.setEnabled(true);
        sslConfig.setTrustAll(true);
        final OfficeManager officeManeger = RemoteOfficeManager.builder()
                .urlConnection(URL)
                .sslConfig(sslConfig)
                .build();

        PROCESS = ProcessFactory.fromOfficeManager(officeManeger, false);
        PROCESS.start();
    }

    @Test
    public void testRemoteOfficeManager() {
        try (final InputStream inputStream = Objects.requireNonNull(
                RemoteOfficeManagerTest.class.getClassLoader().getResourceAsStream("converter/test.docx"), "Cannot find resource");
             final InputStream result = PROCESS.getConverter().convert(inputStream, InputDocumentFormats.DOCX, OutputDocumentFormats.PDF);
             final PDDocument document = Loader.loadPDF(IOUtils.toByteArray(result))) {
            Assert.assertEquals("Teszt dokumentum\n", new PDFTextStripper().getText(document));
        } catch (IOException e) {
            Assert.fail("Converter should not throw exception: " + e.getMessage());
        }
    }

    @AfterClass
    public static void stop() {
        PROCESS.stop();
        CONTAINER.stop();
        CONTAINER.close();
    }

}
