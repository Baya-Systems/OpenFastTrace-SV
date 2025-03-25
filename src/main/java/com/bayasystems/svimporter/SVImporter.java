package com.bayasystems.svimporter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
                    "\\s*//\\s*\\[([^]]*)\\]\\s*(.*?)$");

    private static final Pattern TITLE_DESCRIPTION_PATTERN = Pattern.compile("([^:]+):\\s*(.*)");

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

    record ParsedItem(SpecificationItemId id, String title, String description) {
    }

    static public ParsedItem processLine(String line) {
        // Parse the line and create a new SVSpecificationItem
        final Matcher reqMatcher = REQ_PATTERN.matcher(line);
        if (reqMatcher.matches()) {
            final String body = reqMatcher.group(1);
            final String titleAndDesc = reqMatcher.group(2); 
            SpecificationItemId si = SpecificationItemId.parseId(body);
            final Matcher titleDescMatcher = TITLE_DESCRIPTION_PATTERN.matcher(titleAndDesc);
            if (titleDescMatcher.matches()) {
                final String title = titleDescMatcher.group(1);
                final String description = titleDescMatcher.group(2);
                return new ParsedItem(si, title, description);
            } else {
                return new ParsedItem(si, null, titleAndDesc);
            }
        }
        return null;
    }

    private void emitParsedItem(final ParsedItem item, int lineNumber) {
        if (item == null) {
            return;
        }
        listener.beginSpecificationItem();
        listener.setId(item.id);
        if (file != null) {
            listener.setLocation(file.getPath(), lineNumber);
        } else {
            listener.setLocation("unknown", lineNumber);
        }
        if (item.title != null) {
            listener.setTitle(item.title);
        }
        if (item.description != null) {
            listener.appendDescription(item.description);
        }
        listener.endSpecificationItem();
    }

    private void importStream(Stream<String> lines) {
        final int[] lineCounter = {0}; // needs to be an array so it can be modified inside the lambda
        lines.forEach(line -> {
            lineCounter[0]++;
            emitParsedItem(SVImporter.processLine(line), lineCounter[0]);
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
