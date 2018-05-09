package io.github.erdos.stencil.functions;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class FunctionEvaluatorTest {


    @Test
    public void allFunctionsMustBeNullSafe() {
        for (Function fun : new FunctionEvaluator().listFunctios()) {
            try {
                Object result = fun.call((Object) null);
                assertTrue(result == null || result.equals(""));
            } catch (IllegalArgumentException ignored) {
                // ok
            }
        }
    }
}