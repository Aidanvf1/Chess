package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.ClearService;
import service.LoginRequest;
import service.RegisterRequest;
import service.ServiceException;
import service.UserService;

import java.util.Map;

public class Handlers {
    private final Gson gson = new Gson();
    private final ClearService clearService;
    private final UserService userService;

    public Handlers(ClearService clearService, UserService userService) {
        this.clearService = clearService;
        this.userService = userService;
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

    private void sendJson(Context ctx, Object body) {
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(body));
    }
}