package io.github.erdos.stencil.impl;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import io.github.erdos.stencil.*;
import io.github.erdos.stencil.functions.FunctionEvaluator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static io.github.erdos.stencil.impl.ClojureHelper.*;
import static java.util.Collections.emptyList;

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

        final IFn fn = findFunction("do-eval-stream");
        final Object result = fn.invoke(makeArgsMap(template.getSecretObject(), data.getData()));
        final InputStream resultStream = (InputStream) ((Map) result).get(KV_STREAM);

        // TODO: itt kesobb lehet, hogy HTML stream jon, azt is tudni kell majd kezelni.
        return new EvaluatedDocument() {
            @Override
            public OutputDocumentFormats getFormat() {
                return OutputDocumentFormats.DOCX;
            }

            @Override
            public InputStream getInputStream() {
                return resultStream;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private Map makeArgsMap(Object template, Object data) {
        Map result = new HashMap();
        result.put(KV_TEMPLATE, template);
        result.put(KV_DATA, data);
        result.put(KV_FUNCTION, prepareFunctionCaller());
        return result;
    }

    /**
     * Builds a Clojure function instance that dispatches to java implementations.
     * <p>
     * First argument is callable fn name as string.
     * Second argument is a collection of argumets to pass to fn.
     */
    @SuppressWarnings("unchecked")
    private clojure.lang.IFn prepareFunctionCaller() {
        return new AFunction() {
            @Override
            public Object invoke(Object functionName, Object argsList) {

                if (functionName == null || !(functionName instanceof String)) {
                    throw new IllegalArgumentException("First argument must be a String!");
                } else if (argsList == null) {
                    argsList = emptyList();
                } else if (!(argsList instanceof Collection)) {
                    throw new IllegalArgumentException("Second argument must be a collection!");
                }

                final Object[] args = new ArrayList((Collection) argsList).toArray();

                return functions.call(functionName.toString(), args);
            }
        };
    }
}