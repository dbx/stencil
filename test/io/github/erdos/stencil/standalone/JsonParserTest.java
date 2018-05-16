package io.github.erdos.stencil.standalone;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JsonParserTest {


    @Test
    public void testParseMap() {
        Optional<Object> parsed = JsonParser.parse("{\"a\": 23}");

        assertTrue(parsed.isPresent());
        assertTrue(parsed.get() instanceof Map);
        assertFalse(parsed.get() instanceof List);
    }

    @Test
    public void testParseVector() {
        Optional<Object> parsed = JsonParser.parse("[11, 22, 33]");

        assertTrue(parsed.isPresent());
        System.out.println(parsed.get().getClass());
        assertTrue(parsed.get() instanceof List);
        assertFalse(parsed.get() instanceof Map);
    }
}