package org.bayasystems.svimporter;

import org.itsallcode.openfasttrace.api.core.Importer;
import org.itsallcode.openfasttrace.api.core.ImportRequest;
import org.itsallcode.openfasttrace.api.core.ImportResult;
import org.itsallcode.openfasttrace.api.core.ImporterFactory;

public class SVImporterPlugin implements Importer, ImporterFactory {

    @Override
    public void initialize() {
        // Initialization logic for the SVImporterPlugin
    }

    @Override
    public ImportResult importFile(ImportRequest request) {
        // Logic to handle the import of SystemVerilog files
        // Process the request and return the result
        return null;
    }

    @Override
    public String getId() {
        return "SVImporter";
    }

    @Override
    public String getName() {
        return "SystemVerilog Importer";
    }

    @Override
    public String getDescription() {
        return "A plugin that imports SystemVerilog requirements and tests into the OpenFastTrace environment.";
    }

    @Override
    public Importer create() {
        return new SVImporterPlugin();
    }
}