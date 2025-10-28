package com.bayasystems.oft.svimporter;

import java.util.Objects;
import java.util.logging.Logger;

import org.itsallcode.openfasttrace.api.importer.*;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

import com.bayasystems.oft.svimporter.util.SVConstants;

public class SVImporterPlugin extends ImporterFactory {

    private static final Logger LOG = Logger
            .getLogger(SVImporterPlugin.class.getName());

    @Override
    public boolean supportsFile(InputFile file) {
        // Accept .sv and .v files
        final String filename = file.getPath();
        for (String pattern : SVConstants.supportedFilenamePatterns) {
            if (filename.toLowerCase().endsWith(pattern)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Importer createImporter(InputFile file, ImportEventListener listener) {
        if (!supportsFile(file))
        {
            throw new ImporterException(
                    "File '" + file + "' not supported for import. Supported file name patterns: "
                            + SVConstants.supportedFilenamePatterns);
        }
        LOG.finest(() -> "Creating importer for file " + file);

        return new SVImporter(Objects.requireNonNull(file), Objects.requireNonNull(listener), false);
    }

}