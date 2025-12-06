package server.websocket;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.*;
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
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final UserAccess userAccess;
    private final AuthAccess authAccess;
    private final GameAccess gameAccess;

    public WebSocketHandler(UserAccess userAccess, AuthAccess authAccess, GameAccess gameAccess) {
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {
        int gameId = -1;
        Session session = wsMessageContext.session;
        try {
            Gson serializer = new Gson();
            UserGameCommand command = serializer.fromJson(
                    wsMessageContext.message(), UserGameCommand.class);
            gameId = command.getGameID();
            String username = getUsername(command.getAuthToken());
            saveSession(gameId, session); // put the session in the gameID map of sessions

            switch (command.getCommandType()) { // check if makemove or usergamecommand
                case CONNECT -> connect(session, username, command);
                case MAKE_MOVE -> makeMove(session, username, wsMessageContext.message());
                case LEAVE -> leaveGame(session, username, command);
                case RESIGN -> resign(session, username, command);
            }
        }
        catch (InvalidMoveException ex) {
            sendMessage(session, gameId, new ErrorMessage(ex.getMessage()));
        }
        catch (Exception ex) {
            sendMessage(session, gameId, new ErrorMessage("Error happened."));
        }
    }

    public void sendMessage(Session session, int gameID, ServerMessage message) throws IOException {
        String errorMessage = new Gson().toJson(message);
        session.getRemote().sendString(errorMessage);
    }

    public void makeMove(Session session, String username, String message) throws DataAccessException, InvalidMoveException, IOException {
        Gson serializer = new Gson();
        MakeMoveCommand command = serializer.fromJson(
                message, MakeMoveCommand.class);
        ChessMove move = command.getMove();
        int gameID = command.getGameID();
        GameData gameData = gameAccess.getGame(gameID);
        ChessGame game = gameData.game();
        if (game.getGameOver().equals("YES")) {
            throw new InvalidMoveException("Game is over.");
        }
        ChessGame.TeamColor playerColor = null;
        String playerColorString = "";
        if (username.equals(gameData.whiteUsername())) {
            playerColor = ChessGame.TeamColor.WHITE;
            playerColorString = "WHITE";
        } else if (username.equals(gameData.blackUsername())) {
            playerColor = ChessGame.TeamColor.BLACK;
            playerColorString = "BLACK";
        }
        if (playerColor != game.getTeamTurn()) {
            throw new InvalidMoveException("It isn't your turn.");
        }
        game.makeMove(move);
        // check is in check, is in checkmate
        if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            var notification = new NotificationMessage(String.format("%s is now in checkmate. The game is over.", gameData.blackUsername()));
            connections.broadcast(session, notification, gameID, false);
            connections.broadcast(session, notification, gameID, true);
            game.setGameOver("YES");
        } else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
            var notification = new NotificationMessage(String.format("%s is now in check.", gameData.blackUsername()));
            connections.broadcast(session, notification, gameID, false);
            connections.broadcast(session, notification, gameID, true);
        }
        if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            var notification = new NotificationMessage(String.format("%s is now in checkmate. The game is over.", gameData.whiteUsername()));
            game.setGameOver("YES");
            connections.broadcast(session, notification, gameID, false);
            connections.broadcast(session, notification, gameID, true);
        } else if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
            var notification = new NotificationMessage(String.format("%s is now in check.", gameData.whiteUsername()));
            connections.broadcast(session, notification, gameID, false);
            connections.broadcast(session, notification, gameID, true);
        }

        GameData newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
        gameAccess.updateGame(playerColorString,username,gameID,game);
        var loadgame = new LoadGameMessage(newGameData);
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        int rows = startPosition.getRow();
        int cols = startPosition.getColumn();
        int rowe = endPosition.getRow();
        int cole = endPosition.getColumn();
        // convert cols to normal move notation
        String[] columns = {"a","b","c","d","e","f","g","h"};
        String colstart = columns[cols-1];
        String colend = columns[cole-1];
        var notificationmessage = String.format("%s made move from %s%d to %s%d.", username, colstart, rows,colend,rowe); // no pawn in here for now
        var notification = new NotificationMessage(notificationmessage);
        connections.broadcast(session, loadgame, gameID, false);
        connections.broadcast(session, notification, gameID, false);
        connections.broadcast(session, loadgame, gameID, true);
//        connections.broadcast(session, notification, gameID, true);
    }

    public void leaveGame(Session session, String username, UserGameCommand command) throws DataAccessException, IOException {
        int gameID = command.getGameID();
        GameData gameData = gameAccess.getGame(gameID);
        ChessGame game = gameData.game();
        String playerColor;
        if (username.equals(gameData.whiteUsername())) {
            playerColor = "WHITE";
            gameAccess.updateGame(playerColor,null,gameID,null);
        } else if (username.equals(gameData.blackUsername())) {
            playerColor = "BLACK";
            gameAccess.updateGame(playerColor,null,gameID,null);
        } else {
            playerColor = "OBSERVER"; // no update needed
        }
        var message = String.format("%s left the game as %s", username, playerColor);
        var notification = new NotificationMessage(message);
        connections.broadcast(session, notification, gameID, false);
        connections.remove(gameID, session);
    }

    public void resign(Session session, String username, UserGameCommand command) throws DataAccessException, InvalidMoveException, IOException {
        int gameID = command.getGameID();
        GameData gameData = gameAccess.getGame(gameID);
        ChessGame game = gameData.game();
        if (game.getGameOver().equals("YES")) {
            throw new InvalidMoveException("You can't resign again. The game is over.");
        }
        String playerColorString = "";
        if (username.equals(gameData.whiteUsername())) {
            playerColorString = "WHITE";
        } else if (username.equals(gameData.blackUsername())) {
            playerColorString = "BLACK";
        } else {
            playerColorString = "OBSERVER";
        }
        if (playerColorString.equals("OBSERVER")) {
            throw new InvalidMoveException("You can't resign as the observer.");
        }
        game.setGameOver("YES");
        GameData newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
        gameAccess.updateGame(playerColorString,username,gameID,game);
        var loadgame = new LoadGameMessage(newGameData);
        var notificationmessage = String.format("%s has resigned.", username); // no pawn in here for now
        var notification = new NotificationMessage(notificationmessage);
        connections.broadcast(session, notification, gameID, false);
        connections.broadcast(session, notification, gameID, true);
    }

    public void connect(Session session, String username, UserGameCommand command) throws DataAccessException, IOException {
        int gameID = command.getGameID();
        GameData gameData = gameAccess.getGame(gameID);
        String playerColor;
        if (username.equals(gameData.whiteUsername())) {
            playerColor = "WHITE";
        } else if (username.equals(gameData.blackUsername())) {
            playerColor = "BLACK";
        } else {
            playerColor = "OBSERVER";
        }
        var message = String.format("%s joined the game as %s", username, playerColor);
        var notification = new NotificationMessage(message);
        var loadgame = new LoadGameMessage(gameData);
        connections.broadcast(session, notification, gameID, false);
        connections.broadcast(session, loadgame, gameID, true);
    }

    public String getUsername(String authToken) throws DataAccessException {
        AuthData authData = authAccess.getAuth(authToken);
        return authData.username();
    }

    public void saveSession(int gameID, Session session) {
        connections.add(gameID, session);
    }
}