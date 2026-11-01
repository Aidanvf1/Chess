package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.sql.DatabaseInitializer;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;
import io.javalin.Javalin;
import io.javalin.http.Context;
import service.ClearService;
import service.GameService;
import service.ServiceException;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final Gson gson = new Gson();

    public Server() {
        initializeDatabase();
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

    private void initializeDatabase() {
        try {
            DatabaseInitializer.initialize();
        } catch (DataAccessException ex) {
            throw new IllegalStateException("Unable to initialize the database", ex);
        }
    }

    private Handlers createHandlers() {
        UserDAO userDAO = new SQLUserDAO();
        AuthDAO authDAO = new SQLAuthDAO();
        GameDAO gameDAO = new SQLGameDAO();

        return new Handlers(
                new ClearService(userDAO, authDAO, gameDAO),
                new UserService(userDAO, authDAO),
                new GameService(authDAO, gameDAO));
    }

    private void registerEndpoints(Handlers handlers) {
        javalin.delete("/db", handlers::clear);
        javalin.post("/user", handlers::register);
        javalin.post("/session", handlers::login);
        javalin.delete("/session", handlers::logout);
        javalin.get("/game", handlers::listGames);
        javalin.post("/game", handlers::createGame);
        javalin.put("/game", handlers::joinGame);
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