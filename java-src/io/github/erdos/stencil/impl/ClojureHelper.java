package io.github.erdos.stencil.impl;

import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Symbol;

/**
 * Clojure utilities.
 */
@SuppressWarnings("WeakerAccess")
public class ClojureHelper {

    /**
     * Clojure :stream keyword
     */
    public static final Keyword KV_STREAM = Keyword.intern("stream");

    /**
     * Clojure :variables keyword
     */
    public static final Keyword KV_VARIABLES = Keyword.intern("variables");

    /**
     * Clojure :functions keyword
     */
    public static final Keyword KV_FUNCTIONS = Keyword.intern("functions");

    static {
        final IFn req = RT.var("clojure.core", "require");
        req.invoke(Symbol.intern("stencil.process"));
    }

    /**
     * Finds a function in stencil.process namespace and returns it.
     *
     * @param functionName name of var
     * @return function with for given name
     */
    public static IFn findFunction(String functionName) {
        return RT.var("stencil.process", functionName);
    }
}
