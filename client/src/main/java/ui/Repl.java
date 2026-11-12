package ui;

import client.ResponseException;
import client.ServerFacade;
import model.AuthData;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private enum State {
        SIGNEDOUT,
        SIGNEDIN,
        QUIT
    }

    private final ServerFacade facade;
    private final Scanner scanner = new Scanner(System.in);
    private State state = State.SIGNEDOUT;
    private String authToken;

    public Repl(ServerFacade facade) {
        this.facade = facade;
    }

    public void run() {
        System.out.println("♕ Welcome to 240 Chess. Type help to get started.");
        while (state != State.QUIT) {
            String input = readInput();
            if (input == null) {
                state = State.QUIT;
            } else if (!input.isBlank()) {
                evaluate(input);
            }
        }
        System.out.println("See ya!");
    }

    private String readInput() {
        System.out.print("\n>>> ");
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    private void evaluate(String input) {
        String[] words = input.trim().split("\\s+");
        if (state == State.SIGNEDOUT) {
            handleSignedOut(words);
        } else {
            handleSignedIn(words);
        }
    }

    private void handleSignedOut(String[] words) {
        switch (words[0].toLowerCase()) {
            case "help" -> printSignedOutHelp();
            case "register" -> register(words);
            case "login" -> login(words);
            case "quit" -> state = State.QUIT;
            default -> printUnknownCommand();
        }
    }

    private void handleSignedIn(String[] words) {
        switch (words[0].toLowerCase()) {
            case "help" -> printSignedInHelp();
            case "create" -> createGame(words);
            case "logout" -> logout();
            default -> printUnknownCommand();
        }
    }

    private void register(String[] words) {
        if (words.length != 4) {
            System.out.println("Usage: register <USERNAME> <PASSWORD> <EMAIL>");
            return;
        }
        try {
            signIn(facade.register(words[1], words[2], words[3]), "Registered and logged in as ");
        } catch (ResponseException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void login(String[] words) {
        if (words.length != 3) {
            System.out.println("Usage: login <USERNAME> <PASSWORD>");
            return;
        }
        try {
            signIn(facade.login(words[1], words[2]), "Logged in as ");
        } catch (ResponseException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void signIn(AuthData authData, String greeting) {
        authToken = authData.authToken();
        state = State.SIGNEDIN;
        System.out.println(greeting + authData.username() + ".");
    }

    private void createGame(String[] words) {
        if (words.length < 2) {
            System.out.println("Usage: create <NAME>");
            return;
        }
        String gameName = String.join(" ", Arrays.copyOfRange(words, 1, words.length));
        try {
            facade.createGame(authToken, gameName);
            System.out.println("Created game \"" + gameName + "\".");
        } catch (ResponseException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void logout() {
        try {
            facade.logout(authToken);
            System.out.println("Logged out.");
        } catch (ResponseException ex) {
            System.out.println(ex.getMessage());
        }
        authToken = null;
        state = State.SIGNEDOUT;
    }

    private void printSignedOutHelp() {
        printCommand("register <USERNAME> <PASSWORD> <EMAIL>", "create an account");
        printCommand("login <USERNAME> <PASSWORD>", "sign in and play");
        printCommand("quit", "exit the program");
        printCommand("help", "show this list");
    }

    private void printSignedInHelp() {
        printCommand("create <NAME>", "start a new game");
        printCommand("list", "show all games");
        printCommand("play <NUMBER> <WHITE|BLACK>", "join a game as a color");
        printCommand("observe <NUMBER>", "watch a game");
        printCommand("logout", "sign out");
        printCommand("help", "show this list");
    }

    private void printCommand(String usage, String description) {
        System.out.println("  " + SET_TEXT_COLOR_BLUE + usage + RESET_TEXT_COLOR + " - " + description);
    }

    private void printUnknownCommand() {
        System.out.println("I dont know that one. Type help to see what you can do.");
    }
}