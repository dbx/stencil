package stencil;

import stencil.impl.NativeEvaluator;
import stencil.impl.NativeTemplateFactory;

import java.io.File;
import java.io.IOException;

public final class API {

    public PreparedTemplate prepareTemplate(File templateFile) throws IOException {
        return new NativeTemplateFactory().prepareTemplateFile(templateFile);
    }

    public EvaluatedDocument render(PreparedTemplate template, TemplateData data) {
        return new NativeEvaluator().render(template, data);
    }
}
