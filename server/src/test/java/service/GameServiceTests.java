package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameServiceTests {
    private static final String TOKEN = "valid-token";

    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private GameService gameService;

    @BeforeEach
    public void setUp() throws DataAccessException {
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        gameService = new GameService(authDAO, gameDAO);
        authDAO.createAuth(new AuthData(TOKEN, "hobbs"));
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
}