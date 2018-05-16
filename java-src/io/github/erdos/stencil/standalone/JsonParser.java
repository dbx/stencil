package io.github.erdos.stencil.standalone;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

final class JsonParser {

    private static final ScriptEngineManager em = new ScriptEngineManager();
    private static final ScriptEngine engine = em.getEngineByExtension("js");

    /**
     * Parses string and returns read object if any.
     */
    @SuppressWarnings({"unchecked", "unused", "WeakerAccess"})
    public static Optional<Object> parse(String contents) {
        try {
            ScriptObjectMirror parser = (ScriptObjectMirror) engine.eval("JSON.parse");
            Function<String, Object> caller = x -> parser.call("", x);
            ScriptObjectMirror result = (ScriptObjectMirror) caller.apply(contents);
            return Optional.of(cleanup(result));
        } catch (ScriptException e) {
            return empty();
        }
    }

    /**
     * Maps Nashorn types to simple java types.
     * <p>
     * We need it because javascript arrays are maps too and it makes them more difficult to tell type in Clojure.
     */
    @SuppressWarnings("unchecked")
    private static Object cleanup(Object o) {
        if (o == null) {
            return null;
        } else if (o instanceof ScriptObjectMirror) {
            final ScriptObjectMirror m = (ScriptObjectMirror) o;
            if (m.isArray()) {
                return unmodifiableList(m.values().stream().map(JsonParser::cleanup).collect(toList()));
            } else {
                return unmodifiableMap(m.entrySet().stream().collect(toMap(x -> cleanup(x.getKey()), x -> cleanup(x.getValue()))));
            }
        } else if (o instanceof List) {
            return unmodifiableList(((List<Object>) o).stream().map(JsonParser::cleanup).collect(toList()));
        } else if (o instanceof Map) {
            return unmodifiableMap(((Map<Object, Object>) o).entrySet().stream().collect(toMap(x -> cleanup(x.getKey()), x -> cleanup(x.getValue()))));
        } else {
            return o;
        }
    }
}