package io.github.erdos.stencil.standalone;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.empty;

final class JsonParser {

    private static final ScriptEngineManager em = new ScriptEngineManager();
    private static final ScriptEngine engine = em.getEngineByExtension("js");

    /**
     * Parses string and returns read object if any.
     */
    @SuppressWarnings({"unchecked", "unused"})
    public static Optional<Object> parse(String contents) {
        try {
            ScriptObjectMirror parser = (ScriptObjectMirror) engine.eval("JSON.parse");
            Function<String, Object> caller = x -> parser.call("", x);
            return Optional.of(caller.apply(contents));
        } catch (ScriptException e) {
            return empty();
        }
    }
}
