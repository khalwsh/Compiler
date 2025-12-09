package org.example.Tokens;

public class Token {
    TokenType type;
    String value;
    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }
    public TokenType getType() {
        return type;
    }
    public String  getValue() {
        return value;
    }
    public String toString() {
        return "<Token Type: " + type + ",  Value: " + value + ">";
    }
}

