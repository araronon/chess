package server.websocket;
import chess.ChessGame;
import dataaccess.*;
import jakarta.websocket.*;
import com.google.gson.Gson;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final UserAccess userAccess;
    private final AuthAccess authAccess;
    private final GameAccess gameAccess;

    public WebSocketHandler(UserAccess userAccess, AuthAccess authAccess, GameAccess gameAccess, UserAccess userAccess1, AuthAccess authAccess1, GameAccess gameAccess1) {
        this.userAccess = userAccess1;
        this.authAccess = authAccess1;
        this.gameAccess = gameAccess1;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {
        int gameId = -1;
        Session session = wsMessageContext.session;
        // user game command super class and makemovecommand sub. Need to deserialize twice.
        try {
            Gson Serializer = new Gson();
            UserGameCommand command = Serializer.fromJson(
                    wsMessageContext.message(), UserGameCommand.class);
            gameId = command.getGameID();
            String username = getUsername(command.getAuthToken());
            saveSession(gameId, session); // put the session in the gameID map of sessions

            switch (command.getCommandType()) { // check if makemove or usergamecommand
                case CONNECT -> connect(session, username, command);
//                case MAKE_MOVE -> makeMove(session, username, wsMessageContext.message());
//                case LEAVE -> leaveGame(session, username, (LeaveGameCommand) command);
//                case RESIGN -> resign(session, username, (ResignCommand) command);
            }
        } catch (UnauthorizedException ex) {
            sendMessage(session, gameId, new ErrorMessage("Error: unauthorized"));
        } catch (Exception ex) {
            ex.printStackTrace();
            sendMessage(session, gameId, new ErrorMessage("Error: " + ex.getMessage()));
        }
    }

    public void sendMessage(Session session, int GameID, String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }

    public void connect(Session session, String username, UserGameCommand command) throws DataAccessException, IOException {
        int gameID = command.getGameID();
        GameData gameData = gameAccess.getGame(gameID);
        String playerColor;
        if (username.equals(gameData.whiteUsername())) {
            playerColor = "WHITE";
        } else {
            playerColor = "BLACK";
        }
        var message = String.format("%s joined the game as %s", username, playerColor);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(session, notification, gameID);
    }

    public String getUsername(String authToken) throws DataAccessException {
        AuthData authData = authAccess.getAuth(authToken);
        return authData.username();
    }

    public void saveSession(int gameID, Session session) {
        connections.add(gameID, session);
    }
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    //
//    public void makeMove() {
//        MakeMoveCommand command = Serializer.fromJson(
//                wsMessageContext.message(), UserGameCommand.class);
//        send(command);
//    }

//    private void enter(String visitorName, Session session) throws IOException {
//        connections.add(session);
//        var message = String.format("%s is in the shop", visitorName);
//        var notification = new Notification(Notification.Type.ARRIVAL, message);
//        connections.broadcast(session, notification);
//    }
//
//    private void exit(String visitorName, Session session) throws IOException {
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(session, notification);
//        connections.remove(session);
//    }
//
//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast(null, notification);
//        } catch (Exception ex) {
//            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
//        }
//    }
}