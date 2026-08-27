package io.github.erdos.stencil.standalone;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EdnParserTest {

    @Test
    void testParse() {
        Optional<Object> result = EdnParser.parse("{1 2}");
        assertTrue(result.isPresent());
        assertTrue(result.get() instanceof Map);
    }

    @Test
    void testParseEmpty() {
        Optional<Object> result = EdnParser.parse("");
        assertFalse(result.isPresent());
    }

    @Test
    void testParseNull() {
        Optional<Object> result = EdnParser.parse(null);
        assertFalse(result.isPresent());
    }
}