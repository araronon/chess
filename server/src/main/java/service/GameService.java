package service;
import dataaccess.*;
import model.*;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class GameService {
    private final MemoryUserAccess userAccess;
    private final MemoryAuthAccess authAccess;
    private final MemoryGameAccess gameAccess;

    public GameService(MemoryUserAccess userAccess, MemoryAuthAccess authAccess, MemoryGameAccess gameAccess) {
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
    }

    public Map<String,Collection<GameData>> listGames(String authToken) throws UnauthorizedException {
        if (authAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }
        Collection<GameData> gameList = gameAccess.listGames();
        Map<String, Collection<GameData>> formattedList = new HashMap<>();
        formattedList.put("games",gameList);
        return formattedList;
    }

    public GameResult createGame(GameRequest gameRequest, String authToken) throws UnauthorizedException, BadRequestException {
        if (gameRequest.gameName() == null || authToken == null) {
            throw new BadRequestException();
        }
        if (authAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }
        UserData user = userAccess.getUser(authToken);
        int gameID = gameAccess.createGame(gameRequest.gameName());
        return new GameResult(gameID);
    }

    public void joinGame(GameJoinRequest gameJoinRequest, String authToken) throws UnauthorizedException, BadRequestException, AlreadyTakenException {
        var authData = authAccess.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedException();
        }
        if (gameJoinRequest.playerColor() == null) {
            throw new BadRequestException();
        }
        if (!gameJoinRequest.playerColor().equals("BLACK") && !gameJoinRequest.playerColor().equals("WHITE")) {
            throw new BadRequestException();
        }
        var gameData = gameAccess.getGame(gameJoinRequest.gameID());
        if (gameData == null) {
            throw new BadRequestException();
        }
        // Steal Game
        if ((gameJoinRequest.playerColor().equals("BLACK") && gameData.blackUsername() != null) || (gameJoinRequest.playerColor().equals("WHITE") && gameData.whiteUsername() != null)) {
            throw new AlreadyTakenException();
        }
        gameAccess.updateGame(gameJoinRequest.playerColor(), authData.username(), gameJoinRequest.gameID());
    }

    public void clear() {
        userAccess.clear();
        gameAccess.clear();
        authAccess.clear();
    }
}
