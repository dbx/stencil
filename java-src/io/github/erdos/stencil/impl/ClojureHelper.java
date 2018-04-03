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
     * :stream keyword
     */
    public static final Keyword KV_STREAM = Keyword.intern("stream");

    /**
     * :variables keyword
     */
    public static final Keyword KV_VARIABLES = Keyword.intern("variables");


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
