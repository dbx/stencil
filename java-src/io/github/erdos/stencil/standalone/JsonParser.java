package io.github.erdos.stencil.standalone;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.empty;

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

    @SuppressWarnings("unchecked")
    private static Object cleanup(Object o) {
        if (o == null)
            return null;
        if (!(o instanceof ScriptObjectMirror)) {
            return o;
        } else {
            ScriptObjectMirror m = (ScriptObjectMirror) o;
            if (m.isArray()) {
                return m.values().stream().map(JsonParser::cleanup).collect(Collectors.toList());
            } else {
                return m.entrySet().stream().collect(Collectors.toMap(x -> cleanup(x.getKey()), x -> cleanup(x.getValue())));
            }
        }
    }
}