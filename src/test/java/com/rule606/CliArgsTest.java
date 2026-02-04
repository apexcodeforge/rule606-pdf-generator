package com.rule606;

import com.rule606.cli.CliArgs;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CliArgsTest {

    @Test
    void parseMinimalArgs() {
        CliArgs args = CliArgs.parse(new String[]{"--xml", "input.xml", "--output", "output.pdf"});
        assertEquals("input.xml", args.getXmlFile().getName());
        assertEquals("output.pdf", args.getOutputFile().getName());
        assertNull(args.getXsdFile());
    }

    @Test
    void parseAllArgs() {
        CliArgs args = CliArgs.parse(new String[]{"--xsd", "schema.xsd", "--xml", "input.xml", "--output", "output.pdf"});
        assertEquals("schema.xsd", args.getXsdFile().getName());
        assertEquals("input.xml", args.getXmlFile().getName());
        assertEquals("output.pdf", args.getOutputFile().getName());
    }

    @Test
    void missingXmlThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                CliArgs.parse(new String[]{"--output", "output.pdf"}));
    }

    @Test
    void missingOutputThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                CliArgs.parse(new String[]{"--xml", "input.xml"}));
    }

    @Test
    void unknownArgThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                CliArgs.parse(new String[]{"--xml", "input.xml", "--output", "out.pdf", "--bad"}));
    }
}
