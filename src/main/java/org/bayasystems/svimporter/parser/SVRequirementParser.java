package org.bayasystems.svimporter.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.itsallcode.openfasttrace.api.core.*;

public class SVRequirementParser {
        private static final Pattern REQ_PATTERN = Pattern
                        .compile("\\s*//\\s*\\[(req|dsn|impl|utest|itest|stest)~([\\w\\.\\-]+)~(\\d+)\\]\\s*(.*?)$");

        private static final Pattern TITLE_DESCRIPTION_PATTERN = Pattern
                        .compile("([^:]+):\\s*(.*)");

        private static final Pattern COVERS_PATTERN = Pattern
                        .compile("\\s*//\\s*\\[covers\\s+([\\w\\.\\-\\~]+(?:,\\s*[\\w\\.\\-\\~]+)*)\\]\\s*$");

        private static final Pattern DEPENDS_PATTERN = Pattern
                        .compile("\\s*//\\s*\\[depends\\s+([\\w\\.\\-\\~]+(?:,\\s*[\\w\\.\\-\\~]+)*)\\]\\s*$");

        private static final Pattern NEEDS_PATTERN = Pattern
                        .compile("\\s*//\\s*\\[needs\\s+([\\w\\+]+(?:,\\s*[\\w\\+]+)*)\\]\\s*$");

        private static final Pattern TAGS_PATTERN = Pattern
                        .compile("\\s*//\\s*\\[tags\\s+([\\w\\.\\-]+(?:,\\s*[\\w\\.\\-]+)*)\\]\\s*$");

        public SVSpecificationItem parseLine(final String line) {
                final Matcher reqMatcher = REQ_PATTERN.matcher(line);
                if (reqMatcher.matches()) {
                        return parseRequirement(reqMatcher);
                }

                // Parse other SystemVerilog requirement syntax like covers, depends, etc.

                return null;
        }

        private SVSpecificationItem parseRequirement(final Matcher matcher) {
                final String artifactType = matcher.group(1);
                final String name = matcher.group(2);
                final int revision = Integer.parseInt(matcher.group(3));
                final String titleAndDesc = matcher.group(4);

                final SpecificationItemId id = SpecificationItemId.createId(artifactType, name, revision);

                String title = titleAndDesc;
                String description = "";

                final Matcher titleDescMatcher = TITLE_DESCRIPTION_PATTERN.matcher(titleAndDesc);
                if (titleDescMatcher.matches()) {
                        title = titleDescMatcher.group(1).trim();
                        description = titleDescMatcher.group(2).trim();
                }

                final Location location = Location.create(null, null); // Add file info and line numbers

                return new SVSpecificationItem(id, title, description, location);
        }
}