package com.bayasystems.svimporter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.Importer;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

public class SVImporter implements Importer {

    private final InputFile file;
    private final String content;
    private final ImportEventListener listener;

    private static final Pattern REQ_PATTERN = Pattern
            .compile(
                    "\\s*//\\s*\\[(req|dsn|impl|utest|itest|stest)\\s*->\\s*(req|dsn|impl|utest|itest|stest)~([\\w\\.\\-]+)~(\\d+)\\]\\s*(.*?)$");

    // private static final Pattern TITLE_DESCRIPTION_PATTERN = Pattern
    // .compile("([^:]+):\\s*(.*)");

    // private static final Pattern COVERS_PATTERN = Pattern
    // .compile("\\s*//\\s*\\[covers\\s+([\\w\\.\\-\\~]+(?:,\\s*[\\w\\.\\-\\~]+)*)\\]\\s*$");

    // private static final Pattern DEPENDS_PATTERN = Pattern
    // .compile("\\s*//\\s*\\[depends\\s+([\\w\\.\\-\\~]+(?:,\\s*[\\w\\.\\-\\~]+)*)\\]\\s*$");

    // private static final Pattern NEEDS_PATTERN = Pattern
    // .compile("\\s*//\\s*\\[needs\\s+([\\w\\+]+(?:,\\s*[\\w\\+]+)*)\\]\\s*$");

    // private static final Pattern TAGS_PATTERN = Pattern
    // .compile("\\s*//\\s*\\[tags\\s+([\\w\\.\\-]+(?:,\\s*[\\w\\.\\-]+)*)\\]\\s*$");

    SVImporter(final String content, final ImportEventListener listener) {
        this.content = Objects.requireNonNull(content);
        this.file = null;
        this.listener = Objects.requireNonNull(listener);
    }

    SVImporter(final InputFile file, final ImportEventListener listener) {
        this.content = null;
        this.file = Objects.requireNonNull(file);
        this.listener = Objects.requireNonNull(listener);
    }

    static public SpecificationItemId processLine(String line) {
        // Parse the line and create a new SVSpecificationItem
        final Matcher reqMatcher = REQ_PATTERN.matcher(line);
        if (reqMatcher.matches()) {
            final String artifactType = reqMatcher.group(1);
            final String name = reqMatcher.group(2);
            final int revision = Integer.parseInt(reqMatcher.group(3));
            // final String titleAndDesc = reqMatcher.group(4);
            return SpecificationItemId.createId(name, artifactType, revision);
        }
        return null;
    }

    @Override
    public void runImport() {
        if (file != null) {
            try (BufferedReader reader = file.createReader()) {
                String line;
                int lineNumber = 0;
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    final SpecificationItemId id = SVImporter.processLine(line);
                    if (id != null) {
                        listener.setLocation(file.getPath(), lineNumber);
                        listener.addCoveredId(id);
                    }
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        } else {
            for (String line : content.split("\\r?\\n")) {
                final SpecificationItemId id = SVImporter.processLine(line);
                if (id != null) {
                    listener.addCoveredId(id);
                }
            }
        }
    }

}
