# C Scanner (Java)

A lightweight lexical analyzer written in **Java** that tokenizes C source code into meaningful tokens such as keywords, identifiers, literals, operators, and more.

---

## Features

- Recognizes **C language tokens**:
  - Keywords (`int`, `float`, `if`, `while`, etc.)
  - Identifiers
  - Integer and floating-point literals
  - String and character literals
  - Operators (`+`, `-`, `==`, `+=`, etc.)
  - Preprocessor directives (`#include`, `#define`, etc.)
  - Comments (`// single-line`, `/* multi-line */`)
  - Whitespace and special characters (`;`, `{`, `}`, `(`, `)`, etc.)

- Built using **Java Regex Engine** for flexible and maintainable patterns.  
- Easily extensible — add new token types in the `TokenType` enum.  
- Fully tested with **JUnit 5** for correctness and edge cases.  
- Can tokenize directly from **file input** or **string source code**.

