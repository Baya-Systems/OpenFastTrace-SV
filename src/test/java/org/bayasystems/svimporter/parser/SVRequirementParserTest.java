package org.bayasystems.svimporter.parser;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SVRequirementParserTest {

    @Test
    void testParseLineWithRequirement() {
        SVRequirementParser parser = new SVRequirementParser();
        String validLine = "// [req~MyRequirement~1] Title: This is a sample requirement";
        assertNotNull(parser.parseLine(validLine), "Parser should return an SVSpecificationItem for valid lines");
    }
}