package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthResult register(RegisterRequest request) throws ServiceException, DataAccessException {
        if (request == null || isBlank(request.username())
                || isBlank(request.password()) || isBlank(request.email())) {
            throw new BadRequestException();
        }
        if (userDAO.getUser(request.username()) != null) {
            throw new AlreadyTakenException();
        }

        String hashedPassword = BCrypt.hashpw(request.password(), BCrypt.gensalt());
        userDAO.createUser(new UserData(request.username(), hashedPassword, request.email()));
        String authToken = generateToken();
        authDAO.createAuth(new AuthData(authToken, request.username()));
        return new AuthResult(request.username(), authToken);
    }

    public AuthResult login(LoginRequest request) throws ServiceException, DataAccessException {
        if (request == null || isBlank(request.username()) || isBlank(request.password())) {
            throw new BadRequestException();
        }
        UserData user = userDAO.getUser(request.username());
        if (user == null || !BCrypt.checkpw(request.password(), user.password())) {
            throw new UnauthorizedException();
        }

        String authToken = generateToken();
        authDAO.createAuth(new AuthData(authToken, request.username()));
        return new AuthResult(request.username(), authToken);
    }

    public void logout(String authToken) throws ServiceException, DataAccessException {
        if (authDAO.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }
        authDAO.deleteAuth(authToken);
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}