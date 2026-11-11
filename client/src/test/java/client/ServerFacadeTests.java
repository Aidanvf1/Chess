package client;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.List;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws ResponseException {
        facade.clear();
    }

    @Test
    public void clearRemovesExistingUsers() throws ResponseException {
        facade.register("hobbs", "password", "hobbs@email.com");

        facade.clear();

        Assertions.assertDoesNotThrow(() -> facade.register("hobbs", "password", "hobbs@email.com"));
    }

    @Test
    public void clearServerUnreachable() {
        Assertions.assertThrows(ResponseException.class, () -> new ServerFacade(1).clear());
    }

    @Test
    public void registerSuccess() throws ResponseException {
        AuthData authData = facade.register("hobbs", "password", "hobbs@email.com");

        Assertions.assertTrue(authData.authToken().length() > 10);
        Assertions.assertEquals("hobbs", authData.username());
    }

    @Test
    public void registerDuplicateUsername() throws ResponseException {
        facade.register("hobbs", "password", "hobbs@email.com");

        Assertions.assertThrows(ResponseException.class,
                () -> facade.register("hobbs", "ellie123", "ellie@email.com"));
    }

    @Test
    public void loginSuccess() throws ResponseException {
        facade.register("hobbs", "password", "hobbs@email.com");

        AuthData authData = facade.login("hobbs", "password");

        Assertions.assertTrue(authData.authToken().length() > 10);
        Assertions.assertEquals("hobbs", authData.username());
    }

    @Test
    public void loginWrongPassword() throws ResponseException {
        facade.register("hobbs", "password", "hobbs@email.com");

        Assertions.assertThrows(ResponseException.class, () -> facade.login("hobbs", "wrong"));
    }

    @Test
    public void logoutSuccess() throws ResponseException {
        AuthData authData = facade.register("hobbs", "password", "hobbs@email.com");

        Assertions.assertDoesNotThrow(() -> facade.logout(authData.authToken()));
        Assertions.assertThrows(ResponseException.class, () -> facade.logout(authData.authToken()));
    }

    @Test
    public void logoutInvalidToken() {
        Assertions.assertThrows(ResponseException.class, () -> facade.logout("not-a-real-token"));
    }

    @Test
    public void createGameSuccess() throws ResponseException {
        AuthData authData = facade.register("hobbs", "password", "hobbs@email.com");

        int gameID = facade.createGame(authData.authToken(), "Hobbs vs Ellie");

        Assertions.assertTrue(gameID > 0);
    }

    @Test
    public void createGameUnauthorized() {
        Assertions.assertThrows(ResponseException.class,
                () -> facade.createGame("not-a-real-token", "Hobbs vs Ellie"));
    }

    @Test
    public void listGamesSuccess() throws ResponseException {
        AuthData authData = facade.register("hobbs", "password", "hobbs@email.com");
        facade.createGame(authData.authToken(), "Hobbs vs Ellie");
        facade.createGame(authData.authToken(), "Ellie vs Hobbs");

        List<GameData> games = facade.listGames(authData.authToken());

        Assertions.assertEquals(2, games.size());
        Assertions.assertEquals("Hobbs vs Ellie", games.get(0).gameName());
        Assertions.assertEquals("Ellie vs Hobbs", games.get(1).gameName());
    }

    @Test
    public void listGamesUnauthorized() {
        Assertions.assertThrows(ResponseException.class, () -> facade.listGames("not-a-real-token"));
    }

    @Test
    public void joinGameSuccess() throws ResponseException {
        AuthData hobbs = facade.register("hobbs", "password", "hobbs@email.com");
        AuthData ellie = facade.register("ellie", "password", "ellie@email.com");
        int gameID = facade.createGame(hobbs.authToken(), "Hobbs vs Ellie");

        facade.joinGame(hobbs.authToken(), ChessGame.TeamColor.WHITE, gameID);
        facade.joinGame(ellie.authToken(), ChessGame.TeamColor.BLACK, gameID);

        GameData game = facade.listGames(hobbs.authToken()).get(0);
        Assertions.assertEquals("hobbs", game.whiteUsername());
        Assertions.assertEquals("ellie", game.blackUsername());
    }

    @Test
    public void joinGameColorTaken() throws ResponseException {
        AuthData hobbs = facade.register("hobbs", "password", "hobbs@email.com");
        AuthData ellie = facade.register("ellie", "password", "ellie@email.com");
        int gameID = facade.createGame(hobbs.authToken(), "Hobbs vs Ellie");
        facade.joinGame(hobbs.authToken(), ChessGame.TeamColor.WHITE, gameID);

        Assertions.assertThrows(ResponseException.class,
                () -> facade.joinGame(ellie.authToken(), ChessGame.TeamColor.WHITE, gameID));
    }
}