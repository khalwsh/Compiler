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
                return "\\b(?:auto|break|case|char|const|continue|default|do|double|else|enum|extern|float|for|goto|if|inline|int|long|register|return|short|signed|sizeof|static|struct|switch|typedef|union|unsigned|void|volatile|while)\\b";
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
                return "(?s)/\\*.*?\\*/|//.*";
            case WHITESPACE:
                return "\\s+";
            case SPECIAL_CHARACTERS:
                return ";|,|\\(|\\)|\\{|\\}|\\[|\\]|:|\\.";
            default:
                return "";
        }
    }
}
