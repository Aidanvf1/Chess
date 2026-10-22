package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserServiceTests {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    public void registerSuccess() throws ServiceException, DataAccessException {
        AuthResult result = userService.register(new RegisterRequest("hobbs", "hobbs", "hobbs@mail.com"));

        Assertions.assertEquals("hobbs", result.username());
        Assertions.assertNotNull(result.authToken());
        Assertions.assertNotNull(userDAO.getUser("hobbs"));
        Assertions.assertNotNull(authDAO.getAuth(result.authToken()));
    }

    @Test
    public void registerDuplicateUsername() throws ServiceException, DataAccessException {
        userService.register(new RegisterRequest("hobbs", "pass", "hobbs@mail.com"));

        Assertions.assertThrows(AlreadyTakenException.class,
                () -> userService.register(new RegisterRequest("hobbs", "other", "other@mail.com")));
    }

    @Test
    public void registerMissingPassword() {
        Assertions.assertThrows(BadRequestException.class,
                () -> userService.register(new RegisterRequest("hobbs", null, "hobbs@mail.com")));
    }

    @Test
    public void loginSuccess() throws ServiceException, DataAccessException {
        userService.register(new RegisterRequest("hobbs", "pass", "hobbs@mail.com"));

        AuthResult result = userService.login(new LoginRequest("hobbs", "pass"));

        Assertions.assertEquals("hobbs", result.username());
        Assertions.assertNotNull(authDAO.getAuth(result.authToken()));
    }

    @Test
    public void loginWrongPassword() throws ServiceException, DataAccessException {
        userService.register(new RegisterRequest("hobbs", "pass", "hobbs@mail.com"));

        Assertions.assertThrows(UnauthorizedException.class,
                () -> userService.login(new LoginRequest("hobbs", "wrong")));
    }

    @Test
    public void loginUnknownUser() {
        Assertions.assertThrows(UnauthorizedException.class,
                () -> userService.login(new LoginRequest("nobody", "pass")));
    }

    @Test
    public void logoutSuccess() throws ServiceException, DataAccessException {
        AuthResult result = userService.register(new RegisterRequest("hobbs", "pass", "hobbs@mail.com"));

        userService.logout(result.authToken());

        Assertions.assertNull(authDAO.getAuth(result.authToken()));
    }

    @Test
    public void logoutInvalidToken() {
        Assertions.assertThrows(UnauthorizedException.class,
                () -> userService.logout("not-a-real-token"));
    }
}