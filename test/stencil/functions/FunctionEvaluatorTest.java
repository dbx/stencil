package stencil.functions;

import org.junit.Test;

import static stencil.functions.BasicFunctions.EMPTY;
import static org.junit.Assert.assertTrue;

public class FunctionEvaluatorTest {
    
    @Test
    public void allFunctionsMustBeNullSafe() {
        for (Function fun : new FunctionEvaluator().listFunctios()) {
            if (fun == EMPTY) continue;
            try {
                Object result = fun.call((Object) null);
                assertTrue("Not for " + fun, result == null || result.equals(""));
            } catch (IllegalArgumentException ignored) {
                // ok
            }
        }
    }
}