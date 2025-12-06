package client.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;

import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import websocket.commands.UserGameCommand;
import client.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
                        NotificationMessage msg = new Gson().fromJson(message, NotificationMessage.class);
                        notificationHandler.notify(msg);
                    }
                    if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME) {
                        LoadGameMessage msg = new Gson().fromJson(message, LoadGameMessage.class);
                        notificationHandler.notify(msg);
                    }
                    if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.ERROR) {
                        ErrorMessage msg = new Gson().fromJson(message, ErrorMessage.class);
                        notificationHandler.notify(msg);
                    }
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException("WebSocketFacade Exception: " + ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void resign(String username, String authToken, int gameID) throws ResponseException {
        try {
            UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (Exception ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void joinGame(String username, String authToken, int gameID) throws ResponseException {
        try {
            UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (Exception ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void makeMove(int cols, int rows, int cole, int rowe, ChessPiece.PieceType promotionPiece,
                         String authToken, int gameID) throws ResponseException {
        try {
            ChessMove move = new ChessMove(new ChessPosition(rows,cols), new ChessPosition(rowe,cole), promotionPiece);
            MakeMoveCommand moveCommand = new MakeMoveCommand(authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(moveCommand));
        } catch (Exception ex) {
            throw new ResponseException(ex.getMessage());
        }
    }

    public void leaveGame(String username, String authToken, int gameID) throws ResponseException {
        try {
            UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (Exception ex) {
            throw new ResponseException(ex.getMessage());
        }
    }
}