import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

import org.example.Tokens.Token;
import org.example.Tokens.TokenType;

public class ScannerTest {

    @Test
    void testRegularExpressions() {
        Map<TokenType, String[]> regexExamples = new HashMap<>();

        regexExamples.put(TokenType.KEYWORD, new String[] { "int", "return", "float" });
        regexExamples.put(TokenType.IDENTIFIER, new String[] { "myVar", "_private", "a1_b2" });
        regexExamples.put(TokenType.INTEGER_LITERAL, new String[] { "0x1F", "075", "123", "0", "0b1010" });
        regexExamples.put(TokenType.FLOAT_LITERAL, new String[] { "3.14", ".5", "1e10", "2.0E-3" });
        regexExamples.put(TokenType.STRING_LITERAL, new String[] { "\"hello\"", "\"escaped \\\"quote\\\"\"", "\"a\\nb\"" });
        regexExamples.put(TokenType.CHARACTER_LITERAL, new String[] { "'a'", "'\\n'", "'\\''" });
        regexExamples.put(TokenType.OPERATOR, new String[] { "==", "+", "->", "!=" });
        regexExamples.put(TokenType.PREPROCESSOR, new String[] { "#include <stdio.h>", "#define MAX 10" });
        regexExamples.put(TokenType.COMMENT, new String[] { "// single line comment", "/* multi\nline\ncomment */" });
        regexExamples.put(TokenType.WHITESPACE, new String[] { " ", "\t", "\n", " \t\n" });

        for (Map.Entry<TokenType, String[]> e : regexExamples.entrySet()) {
            TokenType tt = e.getKey();
            String regex = tt.regularExpressionFactory();
            assertNotNull(regex, "regex for " + tt + " should not be null");

            Pattern p = Pattern.compile(regex, Pattern.DOTALL);
            for (String sample : e.getValue()) {
                boolean matched = p.matcher(sample).matches();
                assertTrue(matched, "Regex for " + tt + " did not match sample: [" + sample + "] using: " + regex);
            }
        }
    }

    @Test
    void testTokenizeBasicTokens() {
        String code =
                "int main() {\n" +
                        "  // single line comment\n" +
                        "  int x = 42;\n" +
                        "  float y = 3.14e-2;\n" +
                        "  char c = '\\n';\n" +
                        "  const char* s = \"hello \\\"world\\\"\";\n" +
                        "  /* block\n" +
                        "     comment */\n" +
                        "}\n";

        List<Token> tokens = org.example.Scanner.Scanner.Tokenize(code);
        assertNotNull(tokens, "Tokenize should not return null");
        assertFalse(tokens.isEmpty(), "Token list should not be empty");

        java.util.function.BiPredicate<TokenType, String> contains = (type, val) ->
                tokens.stream().anyMatch(t -> t.getType() == type && t.getValue().equals(val));

        assertTrue(contains.test(TokenType.KEYWORD, "int"), "Expected keyword 'int'");
        assertTrue(contains.test(TokenType.IDENTIFIER, "main"), "Expected identifier 'main'");
        assertTrue(contains.test(TokenType.COMMENT, "// single line comment"), "Expected single-line comment");
        assertTrue(contains.test(TokenType.INTEGER_LITERAL, "42"), "Expected integer literal 42");
        assertTrue(contains.test(TokenType.FLOAT_LITERAL, "3.14e-2"), "Expected float literal 3.14e-2");
        assertTrue(contains.test(TokenType.CHARACTER_LITERAL, "'\\n'"), "Expected character literal '\\n'");
        assertTrue(contains.test(TokenType.STRING_LITERAL, "\"hello \\\"world\\\"\""), "Expected string literal with escaped quote");
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.COMMENT && t.getValue().startsWith("/*")), "Expected block comment starting with /*");
    }

    @Test
    void testTokenizeFromFile(@TempDir Path tempDir) throws Exception {
        String code =
                "int foo;\n" +
                        "// file comment\n" +
                        "double z = 0.5;\n" +
                        "char q = 'x';\n";

        Path file = tempDir.resolve("sample.c");
        Files.writeString(file, code);

        String fileContent = Files.readString(file);
        List<Token> tokens = org.example.Scanner.Scanner.Tokenize(fileContent);

        assertNotNull(tokens);
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.COMMENT && t.getValue().startsWith("// file comment")),
                "Expected file comment token");
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.KEYWORD && t.getValue().equals("int")), "Expected keyword int");
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.IDENTIFIER && t.getValue().equals("foo")), "Expected identifier foo");
        assertTrue(tokens.stream().anyMatch(t -> t.getType() == TokenType.FLOAT_LITERAL || t.getType() == TokenType.INTEGER_LITERAL),
                "Expected numeric literal for z (float or integer depending on recognition)");
    }

    @Test
    void testSpecialCharactersRegex() {
        String separatorsRegex = "[;,(){}\\[\\]:.]";
        Pattern p = Pattern.compile(separatorsRegex);

        String[] examples = new String[] { ";", ",", "(", ")", "{", "}", "[", "]", ":", "." };
        for (String s : examples) {
            assertTrue(p.matcher(s).matches(), "separator regex should match: " + s);
        }
    }
}
