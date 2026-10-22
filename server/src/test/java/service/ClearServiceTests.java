package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClearServiceTests {

    @Test
    public void clearRemovesAllData() throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        userDAO.createUser(new UserData("bob", "password", "bob@mail.com"));
        authDAO.createAuth(new AuthData("token123", "bob"));
        int gameID = gameDAO.createGame("test game");

        new ClearService(userDAO, authDAO, gameDAO).clear();

        Assertions.assertNull(userDAO.getUser("bob"));
        Assertions.assertNull(authDAO.getAuth("token123"));
        Assertions.assertNull(gameDAO.getGame(gameID));
        Assertions.assertTrue(gameDAO.listGames().isEmpty());
    }
}