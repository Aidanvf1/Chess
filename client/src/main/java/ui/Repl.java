package ui;

import chess.ChessBoard;
import chess.ChessGame;
import client.ResponseException;
import client.ServerFacade;
import model.AuthData;
import model.GameData;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private enum State {
        SIGNEDOUT,
        SIGNEDIN,
        QUIT
    }

    private final ServerFacade facade;
    private final BoardRenderer renderer = new BoardRenderer();
    private final Scanner scanner = new Scanner(System.in);
    private State state = State.SIGNEDOUT;
    private String authToken;
    private List<GameData> games = List.of();

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
        System.out.print("\n" + promptLabel() + " >>> ");
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    private String promptLabel() {
        return state == State.SIGNEDIN ? "[LOGGED_IN]" : "[LOGGED_OUT]";
    }

    private void evaluate(String input) {
        String[] words = input.trim().split("\\s+");
        try {
            if (state == State.SIGNEDOUT) {
                handleSignedOut(words);
            } else {
                handleSignedIn(words);
            }
        } catch (RuntimeException ex) {
            System.out.println("Something went wrong. Please try again.");
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
            case "list" -> listGames(words);
            case "play" -> playGame(words);
            case "observe" -> observeGame(words);
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

    private void listGames(String[] words) {
        if (words.length != 1) {
            System.out.println("Usage: list");
            return;
        }
        try {
            loadGames();
            printGames();
        } catch (ResponseException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void loadGames() throws ResponseException {
        games = facade.listGames(authToken);
    }

    private void printGames() {
        if (games.isEmpty()) {
            System.out.println("There are no games yet. Make one with: create <NAME>");
        }
        for (int index = 0; index < games.size(); index++) {
            System.out.println(describeGame(index + 1, games.get(index)));
        }
    }

    private String describeGame(int number, GameData game) {
        return "  " + number + ". " + game.gameName()
                + " - White: " + playerName(game.whiteUsername())
                + ", Black: " + playerName(game.blackUsername());
    }

    private String playerName(String username) {
        return username == null ? "(open)" : username;
    }

    private void playGame(String[] words) {
        if (words.length != 3) {
            System.out.println("Usage: play <NUMBER> <WHITE|BLACK>");
            return;
        }
        try {
            int number = parseNumber(words[1]);
            ChessGame.TeamColor color = parseColor(words[2]);
            GameData game = findGame(number);
            facade.joinGame(authToken, color, game.gameID());
            System.out.println("Joined \"" + game.gameName() + "\" as " + color.name().toLowerCase() + ".");
            printBoard(color);
        } catch (IllegalArgumentException | ResponseException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void observeGame(String[] words) {
        if (words.length != 2) {
            System.out.println("Usage: observe <NUMBER>");
            return;
        }
        try {
            GameData game = findGame(parseNumber(words[1]));
            System.out.println("Observing \"" + game.gameName() + "\".");
            printBoard(ChessGame.TeamColor.WHITE);
        } catch (IllegalArgumentException | ResponseException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private int parseNumber(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    "\"" + text + "\" is not a game number. Use a number from list, like 1.");
        }
    }

    private ChessGame.TeamColor parseColor(String text) {
        return switch (text.toLowerCase()) {
            case "white" -> ChessGame.TeamColor.WHITE;
            case "black" -> ChessGame.TeamColor.BLACK;
            default -> throw new IllegalArgumentException("Pick a color: white or black.");
        };
    }

    private GameData findGame(int number) throws ResponseException {
        loadGames();
        if (number < 1 || number > games.size()) {
            throw new IllegalArgumentException(
                    "There is no game number " + number + ". Type list to see the games.");
        }
        return games.get(number - 1);
    }

    private void printBoard(ChessGame.TeamColor perspective) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        System.out.print(renderer.render(board, perspective));
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