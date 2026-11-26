package service;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
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

    public synchronized MoveResult makeMove(String authToken, Integer gameID, ChessMove move)
            throws ServiceException, DataAccessException {
        AuthData auth = authenticate(authToken);
        GameData gameData = findGame(gameID);
        checkCanMove(auth.username(), gameData, move);
        ChessGame game = gameData.game();
        applyMove(game, move);
        endGameIfFinished(game);
        gameDAO.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(),
                gameData.blackUsername(), gameData.gameName(), game));
        return new MoveResult(auth.username(), game, describeMove(auth.username(), move),
                describeStatus(game, gameData));
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

    private void checkCanMove(String username, GameData gameData, ChessMove move) throws ServiceException {
        ChessGame game = gameData.game();
        if (move == null) {
            throw new ServiceException("Error: no move was given", 400);
        }
        if (game.isGameOver()) {
            throw new ServiceException("Error: the game is over", 400);
        }
        if (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
            throw new ServiceException("Error: observers cannot make moves", 403);
        }
        if (!username.equals(playerFor(game.getTeamTurn(), gameData))) {
            throw new ServiceException("Error: it is not your turn", 403);
        }
    }

    private void applyMove(ChessGame game, ChessMove move) throws ServiceException {
        try {
            game.makeMove(move);
        } catch (InvalidMoveException ex) {
            throw new ServiceException("Error: that move is not allowed", 400);
        }
    }

    private void endGameIfFinished(ChessGame game) {
        ChessGame.TeamColor nextTeam = game.getTeamTurn();
        if (game.isInCheckmate(nextTeam) || game.isInStalemate(nextTeam)) {
            game.setGameOver(true);
        }
    }

    private String describeMove(String username, ChessMove move) {
        return username + " moved " + square(move.getStartPosition()) + " to " + square(move.getEndPosition());
    }

    private String describeStatus(ChessGame game, GameData gameData) {
        ChessGame.TeamColor nextTeam = game.getTeamTurn();
        String player = playerFor(nextTeam, gameData);
        if (game.isInCheckmate(nextTeam)) {
            return player + " is in checkmate";
        }
        if (game.isInStalemate(nextTeam)) {
            return player + " is in stalemate";
        }
        return game.isInCheck(nextTeam) ? player + " is in check" : null;
    }

    private String playerFor(ChessGame.TeamColor team, GameData gameData) {
        return team == ChessGame.TeamColor.WHITE ? gameData.whiteUsername() : gameData.blackUsername();
    }

    private String square(ChessPosition position) {
        return "" + (char) ('a' + position.getColumn() - 1) + position.getRow();
    }
}