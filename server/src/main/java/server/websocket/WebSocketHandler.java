package server.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsMessageContext;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

public class WebSocketHandler {
    private final Gson gson = new Gson();

    public void configure(WsConfig ws) {
        ws.onConnect(ctx -> ctx.enableAutomaticPings());
        ws.onMessage(this::handleMessage);
    }

    private void handleMessage(WsMessageContext ctx) {
        try {
            parseCommand(ctx.message());
            send(ctx, new ErrorMessage("Error: that command is not available yet"));
        } catch (JsonParseException ex) {
            send(ctx, new ErrorMessage("Error: could not read that command"));
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

    private void send(WsMessageContext ctx, ServerMessage message) {
        ctx.send(gson.toJson(message));
    }
}