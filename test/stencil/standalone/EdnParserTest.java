package stencil.standalone;

import org.junit.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EdnParserTest {

    @Test
    public void testParse() {
        Optional<Object> result = EdnParser.parse("{1 2}");
        assertTrue(result.isPresent());
        assertTrue(result.get() instanceof Map);
    }

    @Test
    public void testParseEmpty() {
        Optional<Object> result = EdnParser.parse("");
        assertFalse(result.isPresent());
    }

    @Test
    public void testParseNull() {
        Optional<Object> result = EdnParser.parse(null);
        assertFalse(result.isPresent());
    }
}