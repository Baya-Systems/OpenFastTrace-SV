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
        ParsedItem pi = SVImporter.processLine(reqLine, false);
        assertNotNull(pi, "Should recognize a valid requirement");
        SpecificationItemId gid = pi.generated_id();
        assertEquals("req", gid.getArtifactType(), "Should extract the correct artifact type");
        assertNotNull(gid.getName(), "Should have some valid name for the generated SpecificationItemId");
        assertEquals(-1, gid.getRevision(), "Should extract the correct revision");
        SpecificationItemId id = pi.covered_id();
        assertEquals("dsn", id.getArtifactType(), "Should extract the correct artifact type");
        assertEquals("feature-id", id.getName(), "Should extract the correct requirement name");
        assertEquals(1, id.getRevision(), "Should extract the correct revision");

        // Test processing a regular code line without requirements
        String normalLine = "public void someMethod() { }";
        assertNull(SVImporter.processLine(normalLine, false), "Should return null for non-requirement lines");

        // Test processing an empty line
        String emptyLine = "";
        assertNull(SVImporter.processLine(emptyLine, false), "Should return null for empty lines");

        // Test processing a comment without requirements
        String commentLine = "// This is just a comment";
        assertNull(SVImporter.processLine(commentLine, false),
                "Should return null for comments without requirement syntax");

        String reqLine2 = "// [req~my-feature~2->dsn~feature-id~1] This is a requirement new id and version";
        ParsedItem pi2 = SVImporter.processLine(reqLine2, false);
        assertNotNull(pi2, "Should recognize a valid requirement with new id and version");
        SpecificationItemId gid2 = pi2.generated_id();
        assertEquals("req", gid2.getArtifactType(), "Should extract the correct artifact type");
        assertEquals("my-feature", gid2.getName(),
                "Should extract the correct name for the generated SpecificationItemId");
        assertEquals(2, gid2.getRevision(), "Should extract the correct revision");
        SpecificationItemId id2 = pi2.covered_id();
        assertEquals("dsn", id2.getArtifactType(), "Should extract the correct artifact type");
        assertEquals("feature-id", id2.getName(), "Should extract the correct requirement name");
        assertEquals(1, id2.getRevision(), "Should extract the correct revision");

        String line2 = "// [utest->arch~cashmere.90090.sf-and-cache-protection.data-and-tag-ecc~1 >> upass]";
        ParsedItem pi3 = SVImporter.processLine(line2, true);

        assertNotNull(pi3, "Should recognize a valid unit test requirement");
        SpecificationItemId gid3 = pi3.generated_id();
        assertEquals("utest", gid3.getArtifactType(), "Should extract the correct artifact type");
        assertNotNull(gid3.getName(), "Should have some valid name for the generated SpecificationItemId");
        assertEquals(-1, gid3.getRevision(), "Should extract the correct revision");
        SpecificationItemId id3 = pi3.covered_id();
        assertEquals("arch", id3.getArtifactType(), "Should extract the correct artifact type");
        assertEquals("cashmere.90090.sf-and-cache-protection.data-and-tag-ecc", id3.getName(),
                "Should extract the correct requirement name");
        assertEquals(1, id3.getRevision(), "Should extract the correct revision");

        String[] expected_needs = { "upass" };
        assertArrayEquals(expected_needs, pi3.needed_types());
    }

    @Test
    void testProcessLineWithTitleAndDescription() {
        // Test processing a line with title and description
        String line = "// [req->dsn~feature-id~1] My Feature:This feature does X, Y, Z.";
        ParsedItem pi = SVImporter.processLine(line, true);
        assertNotNull(pi, "Should recognize a valid requirement with title and description");
        assertEquals("My Feature", pi.title(), "Should extract the correct title");
        assertEquals("This feature does X, Y, Z.", pi.description(), "Should extract the correct description");

        // Test processing a line with title and description when processing is disabled
        ParsedItem pi2 = SVImporter.processLine(line, false);
        assertNotNull(pi2, "Should recognize a valid requirement even when title/description processing is disabled");
        assertNull(pi2.title(), "Title processing is disabled, should be null");
        assertNull(pi2.description(), "Description processing is disabled, should be null");
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
        SVImporter importer = new SVImporter(RealFileInput.forPath(path), listener, false);
        importer.runImport();

        // Verify that the importer has processed the file properly

        // verify that the addCoveredId method was called once with this id
        SpecificationItemId id = SpecificationItemId.createId("dsn", "id", -1);
        verify(listener, times(1)).setId(id);

        SpecificationItemId c_id = SpecificationItemId.createId("req", "id", 1);
        verify(listener, times(1)).addCoveredId(c_id);
    }
}
