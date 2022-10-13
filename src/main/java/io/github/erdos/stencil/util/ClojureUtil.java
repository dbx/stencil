package io.github.erdos.stencil.util;

public final class ClojureUtil {
    private ClojureUtil() {}

    public static void requireNamespace(String namespace) {
        clojure.lang.RT.var("clojure.core", "require")
                .invoke(clojure.lang.Symbol.intern(namespace));
    }
}
