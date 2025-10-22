package org.example.Tokens;

public enum TokenType {
    KEYWORD,
    IDENTIFIER,

    INTEGER_LITERAL,
    FLOAT_LITERAL,
    STRING_LITERAL,
    CHARACTER_LITERAL,

    OPERATOR,

    PREPROCESSOR,
    COMMENT,

    WHITESPACE,

    SPECIAL_CHARACTERS;
    /**
     * Returns a regex (String) that matches this token type.
     */
    public String regularExpressionFactory() {
        switch (this) {
            case KEYWORD:
                return "\\b(?:char|int|double|float|string|short|long|unsigned|signed|auto|break|case|char|const|continue|default|do|else|enum|extern|for|goto|if|inline|register|return|sizeof|static|struct|switch|typedef|union|void|volatile|while)\\b";
            case IDENTIFIER:
                return "[A-Za-z_][A-Za-z0-9_]*";
            case INTEGER_LITERAL:
                return "(?:0x[0-9A-Fa-f]+|0b[01]+|0[0-7]*|[1-9][0-9]*)";
            case FLOAT_LITERAL:
                return "(?:[0-9]+\\.[0-9]*([eE][+-]?[0-9]+)?|\\.[0-9]+([eE][+-]?[0-9]+)?|[0-9]+[eE][+-]?[0-9]+)";
            case STRING_LITERAL:
                return "\"(?:\\\\.|[^\"\\\\])*\"";
            case CHARACTER_LITERAL:
                return "'(?:\\\\.|[^'\\\\])'";
            case OPERATOR:
                return "==|!=|<=|>=|\\+\\+|--|&&|\\|\\||<<|>>|->|\\+=|-=|\\*=|/=|%=|&=|\\|=|\\^=|=|\\+|-|\\*|/|%|<|>|!|&|\\||\\^|~|\\?|:";
            case PREPROCESSOR:
                return "#\\s*[A-Za-z_][A-Za-z0-9_]*.*";
            case COMMENT:
                return "/\\*.*?\\*/|//[^\\r\\n]*";
            case WHITESPACE:
                return "\\s+";
            case SPECIAL_CHARACTERS:
                return ";|,|\\(|\\)|\\{|\\}|\\[|\\]|:|\\.";
            default:
                return "";
        }
    }
}
