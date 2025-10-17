package org.example.Scanner;

import org.example.Tokens.Token;
import org.example.Tokens.TokenType;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scanner {
    private static final String DEFAULT_SENTINEL = "DONE";
    private static final boolean skipWhitespace = true;

    public static String getInputConsole() throws IOException {
        System.out.println("Enter code (type '" + DEFAULT_SENTINEL + "' on a new line or send EOF to finish):");
        StringBuilder inputCode = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        boolean firstLine = true;
        while ((line = reader.readLine()) != null) {
            if (line.equals(DEFAULT_SENTINEL)) {
                break;
            }
            if (!firstLine) {
                inputCode.append(System.lineSeparator());
            } else {
                firstLine = false;
            }
            inputCode.append(line);
        }

        return inputCode.toString();
    }

    public static String getInputFile(String filePath) throws IOException {
        StringBuilder inputCode = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (!firstLine) inputCode.append(System.lineSeparator());
                else firstLine = false;
                inputCode.append(line);
            }
        }
        return inputCode.toString();
    }


    public static List<Token> Tokenize(String code) {
        List<Token> tokens = new ArrayList<>();
        boolean skipWhitespace = true;

        TokenType[] order = TokenType.values();

        Pattern[] patterns = new Pattern[order.length];
        for (int i = 0; i < order.length; ++i) {
            String regex = order[i].regularExpressionFactory();
            patterns[i] = Pattern.compile(regex, Pattern.DOTALL);
        }

        int pos = 0;
        final int n = code.length();
        while (pos < n) {
            boolean matched = false;

            for (int i = 0; i < order.length; ++i) {
                Pattern p = patterns[i];
                Matcher m = p.matcher(code);
                m.region(pos, n);
                if (m.lookingAt()) {
                    String value = m.group();
                    TokenType type = order[i];

                    if (!(skipWhitespace && type == TokenType.WHITESPACE)) {
                        tokens.add(new Token(type, value));
                    }

                    pos = m.end();
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                throw new IllegalArgumentException("Unexpected character at position " + pos + ": '" + code.charAt(pos) + "'");
            }
        }

        return tokens;
    }

}
