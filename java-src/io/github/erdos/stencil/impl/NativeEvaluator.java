package io.github.erdos.stencil.impl;

import clojure.lang.IFn;
import io.github.erdos.stencil.*;
import io.github.erdos.stencil.functions.FunctionEvaluator;

import java.io.InputStream;
import java.util.Map;

/**
 * Default implementation that calls the engine written in Clojure.
 */
public class NativeEvaluator implements Evaluator {

    // TODO: dispatch to this object
    @SuppressWarnings("unused")
    private final FunctionEvaluator functions = new FunctionEvaluator();

    public FunctionEvaluator getFunctionEvaluator() {
        return functions;
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