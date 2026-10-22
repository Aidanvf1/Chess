package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import io.javalin.Javalin;
import io.javalin.http.Context;
import service.ClearService;
import service.ServiceException;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final Gson gson = new Gson();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        registerEndpoints(createHandlers());
        registerExceptionHandlers();
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private Handlers createHandlers() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        return new Handlers(
                new ClearService(userDAO, authDAO, gameDAO),
                new UserService(userDAO, authDAO));
    }

    private void registerEndpoints(Handlers handlers) {
        javalin.delete("/db", handlers::clear);
        javalin.post("/user", handlers::register);
        javalin.post("/session", handlers::login);
        javalin.delete("/session", handlers::logout);
    }

    private void registerExceptionHandlers() {
        javalin.exception(ServiceException.class,
                (e, ctx) -> sendError(ctx, e.statusCode(), e.getMessage()));
        javalin.exception(DataAccessException.class,
                (e, ctx) -> sendError(ctx, 500, "Error: " + e.getMessage()));
        javalin.exception(JsonSyntaxException.class,
                (e, ctx) -> sendError(ctx, 400, "Error: bad request"));
        javalin.exception(Exception.class,
                (e, ctx) -> sendError(ctx, 500, "Error: unexpected server error"));
    }

    private void sendError(Context ctx, int status, String message) {
        ctx.status(status);
        ctx.contentType("application/json");
        ctx.result(gson.toJson(Map.of("message", message)));
    }
}
