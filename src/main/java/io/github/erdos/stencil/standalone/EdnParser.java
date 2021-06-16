package io.github.erdos.stencil.standalone;

import clojure.lang.IFn;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

import java.util.Optional;

final class EdnParser {

    private EdnParser() {}

    static {
        final IFn req = RT.var("clojure.core", "require");
        req.invoke(Symbol.intern("clojure.edn"));
    }

    /**
     * Parses string and returns read object if any.
     */
    public static Optional<Object> parse(String contents) {
        try {
            Var variable = RT.var("clojure.edn", "read-string");
            return Optional.of(variable.invoke(contents));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
