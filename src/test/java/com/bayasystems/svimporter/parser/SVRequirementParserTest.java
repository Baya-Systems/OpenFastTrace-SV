package com.bayasystems.svimporter.parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.itsallcode.openfasttrace.api.core.Location;

class SVRequirementParserTest {

    @Test
    void testParseLineWithRequirement() {
        SVRequirementParser parser = new SVRequirementParser();
        String validLine = "// [req~MyRequirement~1] Title: This is a sample requirement";
        assertNotNull(parser.parseLine(validLine, Location.create("test", 0)), "Parser should return an SVSpecificationItem for valid lines");
    }
}