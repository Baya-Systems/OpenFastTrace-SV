package com.bayasystems.oft.svimporter;

import org.junit.jupiter.api.Test;

import com.bayasystems.oft.svimporter.SVImporter.ParsedItem;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.input.RealFileInput;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

public class SVImporterTest {
    @Test
    void testProcessLine() {
        // Test processing a line with a requirement - this doesn't match your pattern
        // The pattern requires specific format: [type->type~name~digits]
        String reqLine = "// [req->dsn~feature-id~1] This is a requirement";
        ParsedItem pi = SVImporter.processLine(reqLine);
        assertNotNull(pi, "Should recognize a valid requirement");
        SpecificationItemId id = pi.id();
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

    @Test
    void testProcessFile() {
        // Get resource from classpath
        URL resourceUrl = getClass().getClassLoader().getResource("testdata/sample.sv");
        assertNotNull(resourceUrl, "Resource file not found");
        
        Path path = null;
        try {
            path = Path.of(resourceUrl.toURI());
        } catch (URISyntaxException e) {
            fail("Failed to convert resource URL to path: " + e.getMessage());
        }

        // mock the listener class
        ImportEventListener listener = mock(ImportEventListener.class);

        // Do the import process on the sample file
        SVImporter importer = new SVImporter(RealFileInput.forPath(path), listener);
        importer.runImport();

        // Verify that the importer has processed the file properly

        // verify that the addCoveredId method was called once with this id
        SpecificationItemId id = SpecificationItemId.createId("req", "id", 1);        
        verify(listener, times(1)).setId(id);

        SpecificationItemId c_id = SpecificationItemId.createId("dsn", "id", 1);
        verify(listener, times(1)).addCoveredId(c_id);
    }
}
