package org.example;

import org.example.Parser.Parser;
import org.example.Scanner.Scanner;
import org.example.Tokens.Token;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {

        String code = Scanner.getInputConsole();
        List<Token> tokens = Scanner.Tokenize(code);

        System.out.println("=== Tokens ===");
        for (int i = 0; i < tokens.size(); i++) {
            System.out.println("token[" + i + "] " + tokens.get(i));
        }
        System.out.println("==============");

        Parser parser = new Parser(tokens);
        parser.parseProgram();

        if (parser.hasErrors()) {
            System.out.println("Syntax errors found:");
            for (String e : parser.getErrors()) {
                System.out.println(e);
            }
        } else {
            System.out.println("No syntax errors found.");
        }
    }
}

