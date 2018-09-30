package stencil.impl;

import clojure.lang.AFunction;
import clojure.lang.IFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stencil.EvaluatedDocument;
import stencil.PreparedTemplate;
import stencil.TemplateData;
import stencil.TemplateDocumentFormats;
import stencil.functions.FunctionEvaluator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
import static stencil.impl.ClojureHelper.*;
import static stencil.impl.Logging.debugStopWatch;

/**
 * Default implementation that calls the engine written in Clojure.
 */
public final class NativeEvaluator {

    private final static Logger LOGGER = LoggerFactory.getLogger(NativeEvaluator.class);

    private final FunctionEvaluator functions = new FunctionEvaluator();


    /**
     * Evaluates a preprocessed template using the given data.
     *
     * @param template preprocessed template file
     * @param data     contains template variables
     * @return evaluated document ready to save to fs
     * @throws IllegalArgumentException when any arg is null
     */
    public EvaluatedDocument render(PreparedTemplate template, TemplateData data) {
        if (template == null) {
            throw new IllegalArgumentException("Template object is missing!");
        } else if (data == null) {
            throw new IllegalArgumentException("Template data is missing!");
        }

        final Consumer<Supplier<String>> stopwatch = debugStopWatch(LOGGER);
        stopwatch.accept(() -> "Starting document rendering for template " + template.getName());

        final IFn fn = findFunction("do-eval-stream");
        final Object result = fn.invoke(makeArgsMap(template.getSecretObject(), data.getData()));
        final InputStream stream = resultInputStream((Map) result);

        return build(stream, template.getTemplateFormat());
    }


    public FunctionEvaluator getFunctionEvaluator() {
        return functions;
    }

    private static EvaluatedDocument build(InputStream stream, TemplateDocumentFormats format) {
        return new EvaluatedDocument() {
            @Override
            public InputStream getInputStream() {
                return stream;
            }

            @Override
            public TemplateDocumentFormats getFormat() {
                return format;
            }
        };
    }

    private static InputStream resultInputStream(Map result) {
        if (!result.containsKey(KV_STREAM)) {
            throw new IllegalArgumentException("Input map does not contains :stream key!");
        } else {
            return (InputStream) result.get(KV_STREAM);
        }
    }

    @SuppressWarnings("unchecked")
    private Map makeArgsMap(Object template, Object data) {
        final Map result = new HashMap();
        result.put(KV_TEMPLATE, template);
        result.put(KV_DATA, data);
        result.put(KV_FUNCTION, new FunctionCaller());
        return result;
    }

    private final class FunctionCaller extends AFunction {

        /**
         * First argument is callable fn name as string.
         * Second argument is a collection of argumets to pass to fn.
         */
        @Override
        @SuppressWarnings("unchecked")
        public Object invoke(Object functionName, Object argsList) {
            if (!(functionName instanceof String)) {
                throw new IllegalArgumentException("First argument must be a String!");
            } else if (argsList == null) {
                argsList = emptyList();
            } else if (!(argsList instanceof Collection)) {
                throw new IllegalArgumentException("Second argument must be a collection!");
            }

            final Object[] args = new ArrayList((Collection) argsList).toArray();

            return functions.call(functionName.toString(), args);
        }
    }
}