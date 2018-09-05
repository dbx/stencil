package io.github.erdos.stencil.standalone;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

public class JsonParserTest {


    @Test
    public void testParseMap() {
        Optional<Object> parsed = JsonParser.parse("{\"a\": 23, \"b\": null}");

        assertTrue(parsed.isPresent());
        assertTrue(parsed.get() instanceof Map);
        assertFalse(parsed.get() instanceof List);
    }

    @Test
    public void testParseVector() {
        Optional<Object> parsed = JsonParser.parse("[11, 22, 33]");

        assertTrue(parsed.isPresent());
        assertTrue(parsed.get() instanceof List);
        assertFalse(parsed.get() instanceof Map);

        List p = (List) parsed.get();
        assertEquals(11, p.iterator().next());
    }

    @Test
    public void testParseVectorNested() {
        Optional<Object> parsed = JsonParser.parse("{\"a\": [11, 22, 33]}");

        assertTrue(parsed.isPresent());
        assertTrue(parsed.get() instanceof Map);

        List vec = (List) ((Map) parsed.get()).get("a");
        assertFalse(vec instanceof Map);
        assertEquals(11, vec.iterator().next());
    }
}