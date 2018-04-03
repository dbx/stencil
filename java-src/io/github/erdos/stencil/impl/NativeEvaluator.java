package io.github.erdos.stencil.impl;

import clojure.lang.IFn;
import io.github.erdos.stencil.*;
import io.github.erdos.stencil.functions.BasicFunctions;
import io.github.erdos.stencil.functions.NumberFunctions;
import io.github.erdos.stencil.functions.StringFunctions;

import java.io.InputStream;
import java.util.Map;

/**
 * Default implementation that calls the engine written in Clojure.
 */
public class NativeEvaluator implements Evaluator {

    {
        registerFunctions(BasicFunctions.values());
        registerFunctions(StringFunctions.values());
        registerFunctions(NumberFunctions.values());
    }

    private void registerFunction(Function function) {
        // TODO
    }

    /**
     * Registers a function to this evaluator engine.
     * Registered functions can be invoked from inside template files.
     *
     * @param functions any number of function instances.
     */
    @SuppressWarnings("WeakerAccess")
    public void registerFunctions(Function... functions) {
        for (Function function : functions) {
            registerFunction(function);
        }
    }

    @Override
    public EvaluatedDocument render(PreparedTemplate template, TemplateData data) {
        if (template == null)
            throw new IllegalArgumentException("Template object is missing!");
        if (data == null)
            throw new IllegalArgumentException("Template data is missing!");

        final IFn fn = ClojureHelper.findFunction("do-eval-stream");
        final Object result = fn.invoke(template.getSecretObject(), data.getData());
        final InputStream stream = (InputStream) ((Map) result).get(ClojureHelper.KV_STREAM);

        // TODO: itt kesobb lehet, hogy HTML stream jon, azt is tudni kell majd kezelni.
        return new EvaluatedDocument() {
            @Override
            public OutputDocumentFormats getFormat() {
                return OutputDocumentFormats.DOCX;
            }

            @Override
            public InputStream getInputStream() {
                return stream;
            }
        };
    }
}