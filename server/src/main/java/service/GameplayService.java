package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

public class GameplayService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameplayService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ConnectResult connect(String authToken, Integer gameID)
            throws ServiceException, DataAccessException {
        AuthData auth = authenticate(authToken);
        GameData game = findGame(gameID);
        return new ConnectResult(auth.username(), roleOf(auth.username(), game), game.game());
    }

    private AuthData authenticate(String authToken) throws ServiceException, DataAccessException {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException();
        }
        return auth;
    }

    private GameData findGame(Integer gameID) throws ServiceException, DataAccessException {
        GameData game = gameID == null ? null : gameDAO.getGame(gameID);
        if (game == null) {
            throw new ServiceException("Error: that game does not exist", 400);
        }
        return game;
    }

    private String roleOf(String username, GameData game) {
        if (username.equals(game.whiteUsername())) {
            return "white";
        }
        if (username.equals(game.blackUsername())) {
            return "black";
        }
        return "an observer";
    }
}