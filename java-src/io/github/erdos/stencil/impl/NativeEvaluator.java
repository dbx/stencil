package io.github.erdos.stencil.impl;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import clojure.lang.Keyword;
import io.github.erdos.stencil.*;
import io.github.erdos.stencil.functions.FunctionEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static io.github.erdos.stencil.impl.ClojureHelper.*;
import static io.github.erdos.stencil.impl.Logging.debugStopWatch;
import static java.util.Collections.emptyList;

/**
 * Default implementation that calls the engine written in Clojure.
 */
public class NativeEvaluator implements Evaluator {

    private final FunctionEvaluator functions = new FunctionEvaluator();

    private final static Logger LOGGER = LoggerFactory.getLogger(NativeEvaluator.class);

    public FunctionEvaluator getFunctionEvaluator() {
        return functions;
    }

    @Override
    public EvaluatedDocument render(PreparedTemplate template, TemplateData data) {
        if (template == null)
            throw new IllegalArgumentException("Template object is missing!");
        if (data == null)
            throw new IllegalArgumentException("Template data is missing!");

        final Consumer<Supplier<String>> stopwatch = debugStopWatch(LOGGER);
        stopwatch.accept(() -> "Starting document rendering for template " + template.getName());

        final IFn fn = findFunction("do-eval-stream");
        final Object result = fn.invoke(makeArgsMap(template.getSecretObject(), data.getData()));
        final InputStream resultStream = resultInputStream((Map) result);
        final OutputDocumentFormats templateForm = resultTemplateFormat((Map) result);

        stopwatch.accept(() -> "Rendering " + template.getName() + " took {}ms");

        return new EvaluatedDocument() {
            @Override
            public OutputDocumentFormats getFormat() {
                return templateForm;
            }

            @Override
            public InputStream getInputStream() {
                return resultStream;
            }
        };
    }

    private InputStream resultInputStream(Map result) {
        return (InputStream) result.get(KV_STREAM);
    }

    private OutputDocumentFormats resultTemplateFormat(Map result) {
        final Keyword templateFormat = (Keyword) result.get(KV_FORMAT);

        return OutputDocumentFormats
                .ofExtension(templateFormat.getName())
                .orElseThrow(() -> new IllegalStateException("Unexpected template format: " + templateFormat));
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