package org.example;

import org.example.Scanner.Scanner;
import org.example.Tokens.Token;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        String code = Scanner.getInputConsole();
        List<Token> tokens = Scanner.Tokenize(code);
        for(Token token : tokens) {
            System.out.println(token);
        }
    }
}