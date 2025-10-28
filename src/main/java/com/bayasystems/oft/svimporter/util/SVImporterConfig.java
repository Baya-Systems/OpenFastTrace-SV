package com.bayasystems.oft.svimporter.util;

/**
 * Configuration utility for SystemVerilog Importer plugin.
 * Provides access to system properties with the 'oft.sv' prefix.
 */
public class SVImporterConfig {
    
    private static final String PROPERTY_PREFIX = "oft.sv.";
    
    /**
     * Property key for enabling title and description processing.
     */
    public static final String PROCESS_TITLE_DESCRIPTION_PROPERTY = PROPERTY_PREFIX + "process_title_description";
    
    /**
     * Get the configuration value for processing title and description from comments.
     * 
     * @return true if title and description processing is enabled, false otherwise (default)
     */
    public static boolean isProcessTitleDescriptionEnabled() {
        return Boolean.parseBoolean(System.getProperty(PROCESS_TITLE_DESCRIPTION_PROPERTY, "false"));
    }
    
    /**
     * Get the configuration value for processing title and description with a custom default.
     * 
     * @param defaultValue the default value to use if the property is not set
     * @return true if title and description processing is enabled, the default value otherwise
     */
    public static boolean isProcessTitleDescriptionEnabled(boolean defaultValue) {
        return Boolean.parseBoolean(System.getProperty(PROCESS_TITLE_DESCRIPTION_PROPERTY, String.valueOf(defaultValue)));
    }
}