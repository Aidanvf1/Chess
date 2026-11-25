package server.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import dataaccess.DataAccessException;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsMessageContext;
import service.ConnectResult;
import service.GameplayService;
import service.ServiceException;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

public class WebSocketHandler {
    private final Gson gson = new Gson();
    private final GameplayService service;
    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(GameplayService service) {
        this.service = service;
    }

    public void configure(WsConfig ws) {
        ws.onConnect(ctx -> ctx.enableAutomaticPings());
        ws.onMessage(this::handleMessage);
        ws.onClose(connections::remove);
    }

    private void handleMessage(WsMessageContext ctx) {
        try {
            execute(ctx, parseCommand(ctx.message()));
        } catch (JsonParseException ex) {
            connections.send(ctx, new ErrorMessage("Error: could not read that command"));
        } catch (ServiceException ex) {
            connections.send(ctx, new ErrorMessage(ex.getMessage()));
        } catch (DataAccessException | RuntimeException ex) {
            connections.send(ctx, new ErrorMessage("Error: the server could not complete that request"));
        }
    }

    private UserGameCommand parseCommand(String json) {
        UserGameCommand command = gson.fromJson(json, UserGameCommand.class);
        if (command == null || command.getCommandType() == null) {
            throw new JsonParseException("missing command type");
        }
        return command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE
                ? gson.fromJson(json, MakeMoveCommand.class)
                : command;
    }

    private void execute(WsMessageContext ctx, UserGameCommand command)
            throws ServiceException, DataAccessException {
        switch (command.getCommandType()) {
            case CONNECT -> connect(ctx, command);
            default -> connections.send(ctx, new ErrorMessage("Error: that command is not available yet"));
        }
    }

    private void connect(WsMessageContext ctx, UserGameCommand command)
            throws ServiceException, DataAccessException {
        ConnectResult result = service.connect(command.getAuthToken(), command.getGameID());
        connections.add(command.getGameID(), ctx);
        connections.send(ctx, new LoadGameMessage(result.game()));
        String text = result.username() + " joined the game as " + result.role();
        connections.broadcast(command.getGameID(), new NotificationMessage(text), ctx);
    }
}