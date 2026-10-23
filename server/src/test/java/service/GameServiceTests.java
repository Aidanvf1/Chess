package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameServiceTests {
    private static final String TOKEN = "valid-token";
    private static final String OTHER_TOKEN = "other-token";

    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private GameService gameService;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        gameService = new GameService(authDAO, gameDAO);
        authDAO.createAuth(new AuthData(TOKEN, "hobbs"));
        authDAO.createAuth(new AuthData(OTHER_TOKEN, "wilson"));
    }

    @Test
    public void createGameSuccess() throws ServiceException, DataAccessException {
        CreateGameResult result = gameService.createGame(TOKEN, new CreateGameRequest("my game"));

        Assertions.assertNotNull(gameDAO.getGame(result.gameID()));
        Assertions.assertEquals("my game", gameDAO.getGame(result.gameID()).gameName());
    }

    @Test
    public void createGameBlankName() {
        Assertions.assertThrows(BadRequestException.class,
                () -> gameService.createGame(TOKEN, new CreateGameRequest(" ")));
    }

    @Test
    public void createGameUnauthorized() {
        Assertions.assertThrows(UnauthorizedException.class,
                () -> gameService.createGame("bad-token", new CreateGameRequest("my game")));
    }

    @Test
    public void listGamesSuccess() throws ServiceException, DataAccessException {
        gameService.createGame(TOKEN, new CreateGameRequest("first"));
        gameService.createGame(TOKEN, new CreateGameRequest("second"));

        ListGamesResult result = gameService.listGames(TOKEN);

        Assertions.assertEquals(2, result.games().size());
    }

    @Test
    public void listGamesUnauthorized() {
        Assertions.assertThrows(UnauthorizedException.class,
                () -> gameService.listGames("bad-token"));
    }

    @Test
    public void joinGameSuccess() throws ServiceException, DataAccessException {
        int gameID = gameService.createGame(TOKEN, new CreateGameRequest("my game")).gameID();

        gameService.joinGame(TOKEN, new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID));

        GameData game = gameDAO.getGame(gameID);
        Assertions.assertEquals("hobbs", game.whiteUsername());
        Assertions.assertNull(game.blackUsername());
    }

    @Test
    public void joinGameColorAlreadyTaken() throws ServiceException, DataAccessException {
        int gameID = gameService.createGame(TOKEN, new CreateGameRequest("my game")).gameID();
        gameService.joinGame(TOKEN, new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID));

        Assertions.assertThrows(AlreadyTakenException.class,
                () -> gameService.joinGame(OTHER_TOKEN,
                        new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID)));
    }

    @Test
    public void joinGameNonexistentGame() {
        Assertions.assertThrows(BadRequestException.class,
                () -> gameService.joinGame(TOKEN, new JoinGameRequest(ChessGame.TeamColor.WHITE, 999)));
    }

    @Test
    public void joinGameMissingColor() throws ServiceException, DataAccessException {
        int gameID = gameService.createGame(TOKEN, new CreateGameRequest("my game")).gameID();

        Assertions.assertThrows(BadRequestException.class,
                () -> gameService.joinGame(TOKEN, new JoinGameRequest(null, gameID)));
    }

    @Test
    public void joinGameUnauthorized() throws ServiceException, DataAccessException {
        int gameID = gameService.createGame(TOKEN, new CreateGameRequest("my game")).gameID();

        Assertions.assertThrows(UnauthorizedException.class,
                () -> gameService.joinGame("bad-token",
                        new JoinGameRequest(ChessGame.TeamColor.BLACK, gameID)));
    }
}