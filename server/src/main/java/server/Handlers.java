package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.ClearService;
import service.CreateGameRequest;
import service.GameService;
import service.JoinGameRequest;
import service.LoginRequest;
import service.RegisterRequest;
import service.ServiceException;
import service.UserService;

import java.util.Map;

public class Handlers {
    private final Gson gson = new Gson();
    private final ClearService clearService;
    private final UserService userService;
    private final GameService gameService;

    public Handlers(ClearService clearService, UserService userService, GameService gameService) {
        this.clearService = clearService;
        this.userService = userService;
        this.gameService = gameService;
    }

    public void clear(Context ctx) throws DataAccessException {
        clearService.clear();
        sendJson(ctx, Map.of());
    }

    public void register(Context ctx) throws ServiceException, DataAccessException {
        RegisterRequest request = gson.fromJson(ctx.body(), RegisterRequest.class);
        sendJson(ctx, userService.register(request));
    }

    public void login(Context ctx) throws ServiceException, DataAccessException {
        LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);
        sendJson(ctx, userService.login(request));
    }

    public void logout(Context ctx) throws ServiceException, DataAccessException {
        userService.logout(ctx.header("authorization"));
        sendJson(ctx, Map.of());
    }

    public void createGame(Context ctx) throws ServiceException, DataAccessException {
        CreateGameRequest request = gson.fromJson(ctx.body(), CreateGameRequest.class);
        sendJson(ctx, gameService.createGame(ctx.header("authorization"), request));
    }

    public void listGames(Context ctx) throws ServiceException, DataAccessException {
        sendJson(ctx, gameService.listGames(ctx.header("authorization")));
    }

    public void joinGame(Context ctx) throws ServiceException, DataAccessException {
        JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);
        gameService.joinGame(ctx.header("authorization"), request);
        sendJson(ctx, Map.of());
    }

    private void sendJson(Context ctx, Object body) {
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(body));
    }
}