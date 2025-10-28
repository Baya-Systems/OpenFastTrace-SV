package com.bayasystems.oft.svimporter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.itsallcode.openfasttrace.api.core.SpecificationItemId;
import org.itsallcode.openfasttrace.api.importer.ImportEventListener;
import org.itsallcode.openfasttrace.api.importer.Importer;
import org.itsallcode.openfasttrace.api.importer.input.InputFile;

public class SVImporter implements Importer {

    private static final Logger LOG = Logger
            .getLogger(SVImporter.class.getName());

    private final InputFile file;
    private final String content;
    private final ImportEventListener listener;
    private final boolean processTitleAndDesc;

    private static final Pattern CONTEXT_PATTERN = Pattern
            .compile(
                    "\\s*//\\s*\\[([^]]*)\\]\\s*(.*?)$");

    // <covered-artifact-type> ['~' <name> '~' <revision>] '->'
    // <specification-object-id> ['<<' <list-of-needed-artifact-types>]

    private static final Pattern TITLE_DESCRIPTION_PATTERN = Pattern.compile("([^:]+):\\s*(.*)");

    // private static final Pattern COVERS_PATTERN = Pattern
    // .compile("\\s*//\\s*\\[covers\\s+([\\w\\.\\-\\~]+(?:,\\s*[\\w\\.\\-\\~]+)*)\\]\\s*$");

    // private static final Pattern DEPENDS_PATTERN = Pattern
    // .compile("\\s*//\\s*\\[depends\\s+([\\w\\.\\-\\~]+(?:,\\s*[\\w\\.\\-\\~]+)*)\\]\\s*$");

    // private static final Pattern NEEDS_PATTERN = Pattern
    // .compile("\\s*//\\s*\\[needs\\s+([\\w\\+]+(?:,\\s*[\\w\\+]+)*)\\]\\s*$");

    // private static final Pattern TAGS_PATTERN = Pattern
    // .compile("\\s*//\\s*\\[tags\\s+([\\w\\.\\-]+(?:,\\s*[\\w\\.\\-]+)*)\\]\\s*$");

    public SVImporter(final String content, final ImportEventListener listener, boolean processTitleAndDesc) {
        this.content = Objects.requireNonNull(content);
        this.file = null;
        this.listener = Objects.requireNonNull(listener);
        this.processTitleAndDesc = processTitleAndDesc;
    }

    public SVImporter(final InputFile file, final ImportEventListener listener, boolean processTitleAndDesc) {
        this.content = null;
        this.file = Objects.requireNonNull(file);
        this.listener = Objects.requireNonNull(listener);
        this.processTitleAndDesc = processTitleAndDesc;
    }

    public record ParsedItem(SpecificationItemId covered_id, SpecificationItemId generated_id, String[] needed_types,
            String title,
            String description) {
        public ParsedItem {
            Objects.requireNonNull(covered_id, "Covered item ID must not be null");
            Objects.requireNonNull(generated_id, "Specification item ID must not be null");
        }
    }

    static public ParsedItem processLine(String line, boolean processTitleAndDesc) {
        // Parse the line and create a new SVSpecificationItem
        final Matcher reqMatcher = CONTEXT_PATTERN.matcher(line);
        if (!reqMatcher.matches())
            return null;
        final String body = reqMatcher.group(1);
        final String titleAndDesc = reqMatcher.group(2);
        // body is of the form
        // <covered-artifact-type> ['~' [<name>] '~' <revision>] '->'
        // <specification-object-id> ['<<' <list-of-needed-artifact-types>]

        // 1. break body into components
        // covered-artfact is everything before '->'
        // needed-types is everything after '<<'
        // specification-object-id is everything between '->' and '<<'
        final String[] parts = body.split("->");
        if (parts.length == 1)
            return null; // ignore // [] without ->
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid tag: " + body + "; must have one '->' separator");
        }

        final String covering_artifact = parts[0].trim();
        final String[] parts2 = parts[1].split("<<");
        String spec_object = null;
        String[] needed_types = null;

        if (parts2.length == 1) {
            spec_object = parts[1].trim();
            needed_types = new String[0];
        } else if (parts2.length == 2) {
            spec_object = parts2[0].trim();
            needed_types = parts2[1].split(",");
        }

        // 2. parse components
        final SpecificationItemId coveredId = SpecificationItemId.parseId(spec_object);

        final String[] ca_parts = covering_artifact.split("~");
        SpecificationItemId generatedId = null;
        if (ca_parts.length == 1) {
            generatedId = SpecificationItemId.createId(ca_parts[0], coveredId.getName(), -1);
        } else if (ca_parts.length == 3) {
            generatedId = SpecificationItemId.createId(ca_parts[0], ca_parts[1], Integer.parseInt(ca_parts[2]));
        } else {
            throw new IllegalArgumentException(
                    "Invalid covered artifact: " + covering_artifact + "; must have one or three '~' separators");
        }

        String title = null;
        String description = null;
        if (processTitleAndDesc) {
            final Matcher titleDescMatcher = TITLE_DESCRIPTION_PATTERN.matcher(titleAndDesc);
            if (titleDescMatcher.matches()) {
                title = titleDescMatcher.group(1);
                description = titleDescMatcher.group(2);
            } else {
                title = titleAndDesc;
            }
        }

        return new ParsedItem(coveredId, generatedId, needed_types, title, description);
    }

    private void emitParsedItem(final ParsedItem item, int lineNumber) {
        if (item == null) {
            return;
        }
        final String needs = item.needed_types.length == 0 ? ""
                : ", needs artifact types " + String.join(", ", item.needed_types);
        LOG.finest(() -> "File " + this.file + ":" + lineNumber + ": found '" + item.generated_id
                + "' covering id '" + item.covered_id + "'" + needs);

        listener.beginSpecificationItem();
        if (file != null) {
            listener.setLocation(file.getPath(), lineNumber);
        } else {
            listener.setLocation("unknown", lineNumber);
        }
        listener.setId(item.generated_id);
        listener.addCoveredId(item.covered_id);
        for (String c : item.needed_types)
            listener.addNeededArtifactType(c);
        if (item.title != null) {
            listener.setTitle(item.title);
        }
        if (item.description != null) {
            listener.appendDescription(item.description);
        }
        listener.endSpecificationItem();
    }

    private void importStream(Stream<String> lines) {
        final int[] lineCounter = { 0 }; // needs to be an array so it can be modified inside the lambda
        lines.forEach(line -> {
            lineCounter[0]++;
            emitParsedItem(SVImporter.processLine(line, processTitleAndDesc), lineCounter[0]);
        });
    }

    @Override
    public void runImport() {
        if (file != null) {
            try (BufferedReader reader = file.createReader()) {
                importStream(reader.lines());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        } else {
            importStream(content.lines());
        }
    }

}
