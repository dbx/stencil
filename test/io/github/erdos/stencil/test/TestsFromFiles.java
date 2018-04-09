package io.github.erdos.stencil.test;

import io.github.erdos.stencil.PreparedTemplate;
import io.github.erdos.stencil.Process;
import io.github.erdos.stencil.ProcessFactory;
import io.github.erdos.stencil.TemplateData;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.empty;

public class TestsFromFiles {

    /**
     * Az n-edig tesztesethez elohalassza az adat fajlt.
     */
    @SuppressWarnings("unchecked")
    private Optional<TemplateData> getTestData(int n) {
        final URL resource = this.getClass().getClassLoader().getResource("tests/" + n + "/data.json");
        if (resource == null) return empty();

        final ScriptEngineManager em = new ScriptEngineManager();
        final ScriptEngine engine = em.getEngineByExtension("js");
        final Path path = new File(resource.getPath()).toPath();

        try {
            ScriptObjectMirror parser = (ScriptObjectMirror) engine.eval("JSON.parse");
            Function<String, Object> caller = x -> parser.call("", x);
            String contents = new String(Files.readAllBytes(path));
            return Optional.of(TemplateData.fromMap((Map<String, Object>) caller.apply(contents)));
        } catch (ScriptException | IOException e) {
            return empty();
        }
    }

    //    @Test
    public void test1() throws URISyntaxException, IOException {
        runTest(1);
    }

    private void runTest(int n) throws URISyntaxException, IOException {

        URL input = this.getClass().getClassLoader().getResource("tests/" + n + "/input.xml");
        if (input == null)
            throw new IllegalArgumentException("Missing test file!");

        File templateFile = new File(input.toURI());

        Process proc = ProcessFactory.fromLibreOfficeHome(new File("/usr/lib64/libreoffice"));

        PreparedTemplate prepared = proc.prepareTemplateFile(templateFile);

        File output = File.createTempFile("test-", ".xml");
        TemplateData templateData = getTestData(n).get();
        proc.renderTemplate(prepared, templateData, output);

        System.out.println(output);
    }
}
