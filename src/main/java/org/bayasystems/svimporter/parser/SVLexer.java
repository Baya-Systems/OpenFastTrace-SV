package org.bayasystems.svimporter.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SVLexer {
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\s*(//.*|/\\*.*?\\*/|\\S+)");

    public List<String> tokenize(String sourceCode) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(sourceCode);

        while (matcher.find()) {
            String token = matcher.group(1);
            if (token != null) {
                tokens.add(token.trim());
            }
        }

        return tokens;
    }
}