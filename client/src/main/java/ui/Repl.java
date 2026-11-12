package ui;

import client.ServerFacade;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private enum State {
        SIGNEDOUT,
        QUIT
    }

    private final ServerFacade facade;
    private final Scanner scanner = new Scanner(System.in);
    private State state = State.SIGNEDOUT;

    public Repl(ServerFacade facade) {
        this.facade = facade;
    }

    public void run() {
        System.out.println("♕ Welcome to 240 Chess. Type help to get started.");
        while (state != State.QUIT) {
            String input = readInput();
            if (!input.isBlank()) {
                evaluate(input);
            }
        }
        System.out.println("See ya!");
    }

    private String readInput() {
        System.out.print("\n>>> ");
        return scanner.hasNextLine() ? scanner.nextLine() : "quit";
    }

    private void evaluate(String input) {
        String[] words = input.trim().split("\\s+");
        handleSignedOut(words);
    }

    private void handleSignedOut(String[] words) {
        switch (words[0].toLowerCase()) {
            case "help" -> printSignedOutHelp();
            case "quit" -> state = State.QUIT;
            default -> printUnknownCommand();
        }
    }

    private void printSignedOutHelp() {
        printCommand("register <USERNAME> <PASSWORD> <EMAIL>", "create an account");
        printCommand("login <USERNAME> <PASSWORD>", "sign in and play");
        printCommand("quit", "exit the program");
        printCommand("help", "show this list");
    }

    private void printCommand(String usage, String description) {
        System.out.println("  " + SET_TEXT_COLOR_BLUE + usage + RESET_TEXT_COLOR + " - " + description);
    }

    private void printUnknownCommand() {
        System.out.println("I dont know that one. Type help to see what you can do.");
    }
}
