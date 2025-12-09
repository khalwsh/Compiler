package org.example.Parser;

import org.example.Tokens.Token;
import org.example.Tokens.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * recursive-descent parser mainly implements parsing for functions, var-declarations, if/else and expression statements.
 */
public class Parser {
    private final List<Token> tokens;
    private int pos = 0;
    private final List<String> errors = new ArrayList<>();

    public Parser(List<Token> tokens) {
        this.tokens = tokens != null ? tokens : new ArrayList<>();
    }

    public void parseProgram() {
        skipIgnorable();
        while (!isAtEnd()) {
            // C program start with main
            if (!parseFunction()) {
                Token t = peek();
                errors.add("token[" + pos + "] " + show(t) + " -> unexpected token at top-level");
                advance();
                skipIgnorable();
            }
        }
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }

    // Accepts: type functionName '(' ')' block
    private boolean parseFunction() {
        int start = pos;
        skipIgnorable();
        Token t = peek();
        if (t == null) return false;

        if (!(t.getType() == TokenType.KEYWORD && isTypeKeyword(t.getValue()))) return false;

        advance();

        skipIgnorable();
        Token nameTok = peek();
        if (nameTok == null) {
            errors.add("token[" + pos + "]: expected function name but found EOF");
            return false;
        }

        boolean nameIsValid = (nameTok.getType() == TokenType.IDENTIFIER)
                || (nameTok.getType() == TokenType.KEYWORD && "main".equals(nameTok.getValue()));
        if (!nameIsValid) {
            errors.add("token[" + pos + "] " + show(nameTok) + " -> expected function name (identifier or 'main')");
            pos = start;
            return false;
        }
        advance();

        skipIgnorable();
        // expect '('
        if (!expectSpecial("(")) {
            errors.add("token[" + pos + "]: expected '(' after function name");
            pos = start;
            return false;
        }
        skipIgnorable();
        if (!expectSpecial(")")) {
            errors.add("token[" + pos + "]: expected ')' (only empty parameter lists supported)");
            pos = start;
            return false;
        }

        skipIgnorable();
        if (!parseBlock()) {
            errors.add("token[" + pos + "]: expected function body (block)");
            pos = start;
            return false;
        }
        return true;
    }

    private boolean parseBlock() {
        if (!expectSpecial("{")) return false;
        skipIgnorable();
        while (!isAtEnd() && !checkSpecial("}")) {
            parseStatement();
            skipIgnorable();
        }
        if (!expectSpecial("}")) {
            errors.add("token[" + pos + "]: missing '}' for block");
            return false;
        }
        return true;
    }

    private void parseStatement() {
        skipIgnorable();
        Token t = peek();
        if (t == null) return;

        if (t.getType() == TokenType.KEYWORD && isTypeKeyword(t.getValue())) {
            parseVarDecl();
            return;
        }

        if (t.getType() == TokenType.KEYWORD && "if".equals(t.getValue())) {
            parseIf();
            return;
        }

        if (t.getType() == TokenType.KEYWORD && "return".equals(t.getValue())) {
            advance();
            parseExpression();
            if (!expectSpecial(";")) {
                errors.add("token[" + pos + "]: missing ';' after return");
                skipUntil(";");
                if (!isAtEnd()) advance();
            }
            return;
        }

        if (checkSpecial("{")) {
            parseBlock();
            return;
        }

        parseExpression();
        if (!expectSpecial(";")) {
            errors.add("token[" + pos + "]: missing ';' after expression");
            skipUntil(";");
            if (!isAtEnd()) advance();
        }
    }

    private void parseVarDecl() {
        Token typeTok = advance();
        skipIgnorable();

        if (!matchIdentifier()) {
            errors.add("token[" + pos + "] " + show(peek()) + " -> expected identifier after type '" + typeTok.getValue() + "'");
            // try to recover to semicolon
            skipUntil(";");
            if (!isAtEnd()) advance();
            return;
        }

        skipIgnorable();
        while (checkSpecial(",")) {
            advance();
            skipIgnorable();
            if (!matchIdentifier()) {
                errors.add("token[" + pos + "]: expected identifier after ',' in declaration");
                skipUntil(";");
                if (!isAtEnd()) advance();
                return;
            }
            skipIgnorable();
        }

        if (!expectSpecial(";")) {
            errors.add("token[" + pos + "]: missing ';' after variable declaration");
            skipUntil(";");
            if (!isAtEnd()) advance();
        }
    }

    private void parseIf() {
        advance();
        skipIgnorable();
        if (!expectSpecial("(")) {
            errors.add("token[" + pos + "]: expected '(' after if");
            return;
        }
        parseExpression();
        if (!expectSpecial(")")) {
            errors.add("token[" + pos + "]: missing ')' after if condition");
        }
        skipIgnorable();
        // a statement (could be block or single stmt)
        parseStatement();
        skipIgnorable();
        if (peek() != null && peek().getType() == TokenType.KEYWORD && "else".equals(peek().getValue())) {
            advance(); // consume else
            skipIgnorable();
            parseStatement();
        }
    }

    private void parseExpression() {
        parsePrimary();
        skipIgnorable();
        while (peek() != null && peek().getType() == TokenType.OPERATOR) {
            advance();
            skipIgnorable();
            parsePrimary();
            skipIgnorable();
        }
    }

    private void parsePrimary() {
        skipIgnorable();
        Token t = peek();
        if (t == null) return;

        if (t.getType() == TokenType.IDENTIFIER || isLiteralType(t.getType())) {
            advance();
            return;
        }

        if (t.getType() == TokenType.KEYWORD) {
            if ("main".equals(t.getValue())) {
                advance();
                return;
            } else {
                errors.add("token[" + pos + "] " + show(t) + " -> Unexpected token inside expression: " + t.getValue());
                advance();
                return;
            }
        }

        if (checkSpecial("(")) {
            advance();
            parseExpression();
            if (!expectSpecial(")")) {
                errors.add("token[" + pos + "]: missing ')' in expression");
            }
            return;
        }

        errors.add("token[" + pos + "] " + show(t) + " -> unexpected token inside expression");
        advance();
    }

    private boolean isLiteralType(TokenType tt) {
        return tt == TokenType.INTEGER_LITERAL || tt == TokenType.FLOAT_LITERAL ||
                tt == TokenType.STRING_LITERAL || tt == TokenType.CHARACTER_LITERAL;
    }

    private boolean matchIdentifier() {
        Token t = peek();
        if (t != null && t.getType() == TokenType.IDENTIFIER) {
            advance();
            return true;
        }
        if (t != null && t.getType() == TokenType.KEYWORD && "main".equals(t.getValue())) {
            advance();
            return true;
        }
        return false;
    }

    private boolean expectSpecial(String s) {
        Token t = peek();
        if (t != null && t.getType() == TokenType.SPECIAL_CHARACTERS && s.equals(t.getValue())) {
            advance();
            return true;
        }
        return false;
    }

    private boolean checkSpecial(String s) {
        Token t = peek();
        return t != null && t.getType() == TokenType.SPECIAL_CHARACTERS && s.equals(t.getValue());
    }

    private void skipUntil(String v) {
        while (!isAtEnd()) {
            Token t = peek();
            if (t != null && v.equals(t.getValue())) break;
            advance();
        }
    }

    private void skipIgnorable() {
        while (!isAtEnd() && (peek().getType() == TokenType.WHITESPACE || peek().getType() == TokenType.COMMENT)) {
            advance();
        }
    }

    private Token peek() {
        if (pos >= tokens.size()) return null;
        return tokens.get(pos);
    }

    private Token advance() {
        if (pos >= tokens.size()) return null;
        return tokens.get(pos++);
    }

    private boolean isAtEnd() {
        return pos >= tokens.size();
    }

    private boolean isTypeKeyword(String s) {
        List<String> types = Arrays.asList("int", "void", "char", "float", "double", "short", "long", "unsigned", "signed");
        return types.contains(s);
    }

    private String show(Token t) {
        if (t == null) return "<EOF>";
        return "<" + t.getType() + ":" + t.getValue() + ">";
    }
}
