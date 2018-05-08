package io.github.erdos.stencil.standalone;

import io.github.erdos.stencil.OutputDocumentFormats;
import org.junit.Test;

import java.io.File;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class ArgsParserTest {


    @Test
    public void longKeySeparated() {
        // GIVEN
        String params = "--output-type pdf template1 data1 data2 data3";
        // WHEN
        ArgsParser args = ArgsParser.parse(params.split("\\s+"));
        // THEN
        assertEquals(OutputDocumentFormats.PDF, args.getOutputFormat());
        assertEquals("template1", args.getTemplateFile().getName());
        assertEquals(asList("data1", "data2", "data3"), args.getDataFiles().stream().map(File::getName).collect(toList()));
    }

    @Test
    public void shortKeyOne() {
        // GIVEN
        String params = "-Tpdf t a";
        // WHEN
        ArgsParser args = ArgsParser.parse(params.split("\\s+"));
        // THEN
        assertEquals(OutputDocumentFormats.PDF, args.getOutputFormat());
    }

    @Test
    public void shortKeySeparated() {
        // GIVEN
        String params = "-T pdf t a";
        // WHEN
        ArgsParser args = ArgsParser.parse(params.split("\\s+"));
        // THEN
        assertEquals(OutputDocumentFormats.PDF, args.getOutputFormat());
    }

    @Test
    public void longKeyEquals() {
        // GIVEN
        String params = "--output-type=pdf t a";
        // WHEN
        ArgsParser args = ArgsParser.parse(params.split("\\s+"));
        // THEN
        assertEquals(OutputDocumentFormats.PDF, args.getOutputFormat());
    }

}