package com.bayasystems.oft.svimporter.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SVImporterConfigTest {

    private String originalPropertyValue;

    @BeforeEach
    void setUp() {
        // Save the original value
        originalPropertyValue = System.getProperty(SVImporterConfig.PROCESS_TITLE_DESCRIPTION_PROPERTY);
    }

    @AfterEach
    void tearDown() {
        // Restore the original value
        if (originalPropertyValue == null) {
            System.clearProperty(SVImporterConfig.PROCESS_TITLE_DESCRIPTION_PROPERTY);
        } else {
            System.setProperty(SVImporterConfig.PROCESS_TITLE_DESCRIPTION_PROPERTY, originalPropertyValue);
        }
    }

    @Test
    void testGetProcessTitleDescriptionDefault() {
        // Clear the property to test default behavior
        System.clearProperty(SVImporterConfig.PROCESS_TITLE_DESCRIPTION_PROPERTY);
        
        assertFalse(SVImporterConfig.isProcessTitleDescriptionEnabled(), 
                "Default value should be false when property is not set");
    }

    @Test
    void testGetProcessTitleDescriptionTrue() {
        System.setProperty(SVImporterConfig.PROCESS_TITLE_DESCRIPTION_PROPERTY, "true");
        
        assertTrue(SVImporterConfig.isProcessTitleDescriptionEnabled(), 
                "Should return true when property is set to 'true'");
    }

    @Test
    void testGetProcessTitleDescriptionFalse() {
        System.setProperty(SVImporterConfig.PROCESS_TITLE_DESCRIPTION_PROPERTY, "false");
        
        assertFalse(SVImporterConfig.isProcessTitleDescriptionEnabled(), 
                "Should return false when property is set to 'false'");
    }

    @Test
    void testGetProcessTitleDescriptionWithCustomDefault() {
        // Clear the property to test custom default behavior
        System.clearProperty(SVImporterConfig.PROCESS_TITLE_DESCRIPTION_PROPERTY);
        
        assertTrue(SVImporterConfig.isProcessTitleDescriptionEnabled(true), 
                "Should return custom default value when property is not set");
        
        assertFalse(SVImporterConfig.isProcessTitleDescriptionEnabled(false), 
                "Should return custom default value when property is not set");
    }

    @Test
    void testGetProcessTitleDescriptionWithCustomDefaultOverride() {
        System.setProperty(SVImporterConfig.PROCESS_TITLE_DESCRIPTION_PROPERTY, "true");
        
        assertTrue(SVImporterConfig.isProcessTitleDescriptionEnabled(false), 
                "Should return property value even when custom default is different");
    }

    @Test
    void testPropertyConstant() {
        assertEquals("oft.sv.process_title_description", SVImporterConfig.PROCESS_TITLE_DESCRIPTION_PROPERTY,
                "Property constant should have correct value");
    }
}