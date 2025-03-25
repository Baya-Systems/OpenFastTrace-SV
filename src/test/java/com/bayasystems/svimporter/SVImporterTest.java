package com.bayasystems.svimporter;

import org.junit.jupiter.api.Test;
import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import static org.junit.jupiter.api.Assertions.*;

public class SVImporterTest {
    @Test
    void testProcessLine() {        
        // Test processing a line with a requirement - this doesn't match your pattern
        // The pattern requires specific format: [type->type~name~digits]
        String reqLine = "// [req->dsn~feature-id~1] This is a requirement";
        SpecificationItemId id = SVImporter.processLine(reqLine);
        assertNotNull(id, "Should recognize a valid requirement");
        assertEquals("dsn", id.getArtifactType(), "Should extract the correct artifact type");
        assertEquals("feature-id", id.getName(), "Should extract the correct requirement name");
        assertEquals(1, id.getRevision(), "Should extract the correct revision");
        
        // Test processing a regular code line without requirements
        String normalLine = "public void someMethod() { }";
        assertNull(SVImporter.processLine(normalLine), "Should return null for non-requirement lines");
        
        // Test processing an empty line
        String emptyLine = "";
        assertNull(SVImporter.processLine(emptyLine), "Should return null for empty lines");
        
        // Test processing a comment without requirements
        String commentLine = "// This is just a comment";
        assertNull(SVImporter.processLine(commentLine), "Should return null for comments without requirement syntax");
    }
}
