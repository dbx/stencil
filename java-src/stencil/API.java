package stencil;

import stencil.impl.NativeEvaluator;
import stencil.impl.NativeTemplateFactory;

import java.io.File;
import java.io.IOException;

public final class API {

    public static PreparedTemplate prepareTemplate(File templateFile) throws IOException {
        return new NativeTemplateFactory().prepareTemplateFile(templateFile);
    }

    public static EvaluatedDocument render(PreparedTemplate template, TemplateData data) {
        return new NativeEvaluator().render(template, data);
    }
}
