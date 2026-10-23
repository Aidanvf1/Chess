package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class GameService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateGameResult createGame(String authToken, CreateGameRequest request)
            throws ServiceException, DataAccessException {
        if (request == null || request.gameName() == null || request.gameName().isBlank()) {
            throw new BadRequestException();
        }
        authenticate(authToken);

        int gameID = gameDAO.createGame(request.gameName());
        return new CreateGameResult(gameID);
    }

    public ListGamesResult listGames(String authToken) throws ServiceException, DataAccessException {
        authenticate(authToken);

        Collection<GameSummary> summaries = new ArrayList<>();
        for (GameData game : gameDAO.listGames()) {
            summaries.add(new GameSummary(game.gameID(), game.whiteUsername(),
                    game.blackUsername(), game.gameName()));
        }
        return new ListGamesResult(summaries);
    }

    public void joinGame(String authToken, JoinGameRequest request)
            throws ServiceException, DataAccessException {
        if (request == null || request.playerColor() == null) {
            throw new BadRequestException();
        }
        AuthData auth = authenticate(authToken);

        GameData game = gameDAO.getGame(request.gameID());
        if (game == null) {
            throw new BadRequestException();
        }
        if (isColorTaken(game, request.playerColor())) {
            throw new AlreadyTakenException();
        }

        gameDAO.updateGame(withPlayer(game, request.playerColor(), auth.username()));
    }

    private AuthData authenticate(String authToken) throws ServiceException, DataAccessException {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new UnauthorizedException();
        }
        return auth;
    }

    private boolean isColorTaken(GameData game, ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) {
            return game.whiteUsername() != null;
        }
        return game.blackUsername() != null;
    }

    private GameData withPlayer(GameData game, ChessGame.TeamColor color, String username) {
        if (color == ChessGame.TeamColor.WHITE) {
            return new GameData(game.gameID(), username, game.blackUsername(),
                    game.gameName(), game.game());
        }
        return new GameData(game.gameID(), game.whiteUsername(), username,
                game.gameName(), game.game());
    }
}