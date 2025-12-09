package org.example.Parser;

import org.example.Tokens.Token;
import org.example.Tokens.TokenType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ParserTest {

    // Helper: build token quickly
    private Token t(TokenType type, String value) {
        return new Token(type, value);
    }

    /**
     * int main() {
     *   int x,y;
     *   if (x == 42) {
     *     x = x - 3;
     *   } else {
     *     y = 3.1;
     *   }
     *   return 0;
     * }
     */
    private List<Token> buildValidSampleProgram() {
        List<Token> tokens = new ArrayList<>();

        // int main() {
        tokens.add(t(TokenType.KEYWORD, "int"));
        tokens.add(t(TokenType.IDENTIFIER, "main"));
        tokens.add(t(TokenType.SPECIAL_CHARACTERS, "("));
        tokens.add(t(TokenType.SPECIAL_CHARACTERS, ")"));
        tokens.add(t(TokenType.SPECIAL_CHARACTERS, "{"));

        // int x,y;
        tokens.add(t(TokenType.KEYWORD, "int"));
        tokens.add(t(TokenType.IDENTIFIER, "x"));
        tokens.add(t(TokenType.SPECIAL_CHARACTERS, ","));
        tokens.add(t(TokenType.IDENTIFIER, "y"));
        tokens.add(t(TokenType.SPECIAL_CHARACTERS, ";"));

        // if (x == 42) {
        tokens.add(t(TokenType.KEYWORD, "if"));
        tokens.add(t(TokenType.SPECIAL_CHARACTERS, "("));
        tokens.add(t(TokenType.IDENTIFIER, "x"));
        tokens.add(t(TokenType.OPERATOR, "=="));
        tokens.add(t(TokenType.INTEGER_LITERAL, "42"));
        tokens.add(t(TokenType.SPECIAL_CHARACTERS, ")"));
        tokens.add(t(TokenType.SPECIAL_CHARACTERS, "{"));

        // x = x - 3;
        tokens.add(t(TokenType.IDENTIFIER, "x"));
        tokens.add(t(TokenType.OPERATOR, "="));
        tokens.add(t(TokenType.IDENTIFIER, "x"));
        tokens.add(t(TokenType.OPERATOR, "-"));
        tokens.add(t(TokenType.INTEGER_LITERAL, "3"));
        tokens.add(t(TokenType.SPECIAL_CHARACTERS, ";"));

        // }
        tokens.add(t(TokenType.SPECIAL_CHARACTERS, "}"));

        // else {
        tokens.add(t(TokenType.KEYWORD, "else"));
        tokens.add(t(TokenType.SPECIAL_CHARACTERS, "{"));

        // y = 3.1;
        tokens.add(t(TokenType.IDENTIFIER, "y"));
        tokens.add(t(TokenType.OPERATOR, "="));
        tokens.add(t(TokenType.FLOAT_LITERAL, "3.1"));
        tokens.add(t(TokenType.SPECIAL_CHARACTERS, ";"));

        // }
        tokens.add(t(TokenType.SPECIAL_CHARACTERS, "}"));

        // return 0;
        tokens.add(t(TokenType.KEYWORD, "return"));
        tokens.add(t(TokenType.INTEGER_LITERAL, "0"));
        tokens.add(t(TokenType.SPECIAL_CHARACTERS, ";"));

        // closing }
        tokens.add(t(TokenType.SPECIAL_CHARACTERS, "}"));

        return tokens;
    }

    @Test
    public void testValidSampleProgram_noSyntaxErrors() {
        List<Token> tokens = buildValidSampleProgram();
        Parser parser = new Parser(tokens);
        parser.parseProgram();

        if (parser.hasErrors()) {
            fail("Parser reported errors for a valid sample program:\n" + String.join("\n", parser.getErrors()));
        }
        assertFalse(parser.hasErrors(), "Expected no syntax errors for valid sample program");
    }

    @Test
    public void testMissingSemicolon_detected() {
        List<Token> tokens = buildValidSampleProgram();

        int indexToRemove = -1;
        for (int i = 0; i < tokens.size(); i++) {
            Token tk = tokens.get(i);
            if (tk.getType() == TokenType.SPECIAL_CHARACTERS && ";".equals(tk.getValue())) {
                indexToRemove = i;
                break;
            }
        }
        assertTrue(indexToRemove > 0, "Couldn't find declaration semicolon in token stream");
        tokens.remove(indexToRemove);

        Parser parser = new Parser(tokens);
        parser.parseProgram();

        assertTrue(parser.hasErrors(), "Expected parser to report syntax error when semicolon is missing");
        boolean mentionsSemicolon = parser.getErrors().stream().anyMatch(s -> s.toLowerCase().contains(";") || s.toLowerCase().contains("semicolon"));
        if (!mentionsSemicolon) {
            System.out.println("Parser errors (missing semicolon test):");
            parser.getErrors().forEach(System.out::println);
        }
    }

    /**
     * Test: if without parentheses -> should produce syntax error.
     * e.g. int main() { if x == 1 { } }
     */
    @Test
    public void testIfMissingParenthesis_detected() {
        List<Token> tokens = new ArrayList<>();

        tokens.add(t(TokenType.KEYWORD, "int"));
        tokens.add(t(TokenType.IDENTIFIER, "main"));
        tokens.add(t(TokenType.SPECIAL_CHARACTERS, "("));
        tokens.add(t(TokenType.SPECIAL_CHARACTERS, ")"));
        tokens.add(t(TokenType.SPECIAL_CHARACTERS, "{"));
        tokens.add(t(TokenType.KEYWORD, "if"));
        tokens.add(t(TokenType.IDENTIFIER, "x"));
        tokens.add(t(TokenType.OPERATOR, "=="));
        tokens.add(t(TokenType.INTEGER_LITERAL, "1"));

        tokens.add(t(TokenType.SPECIAL_CHARACTERS, "{"));
        tokens.add(t(TokenType.SPECIAL_CHARACTERS, "}"));

        tokens.add(t(TokenType.SPECIAL_CHARACTERS, "}"));

        Parser parser = new Parser(tokens);
        parser.parseProgram();

        assertTrue(parser.hasErrors(), "Expected parser to detect missing '(' after if");

        boolean mentionsIfOrParen = parser.getErrors().stream().anyMatch(s -> s.toLowerCase().contains("if") || s.contains("(") || s.toLowerCase().contains("missing"));
        if (!mentionsIfOrParen) {
            System.out.println("Parser errors (if missing parenthesis):");
            parser.getErrors().forEach(System.out::println);
        }
    }
}
