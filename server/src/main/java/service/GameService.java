package service;
import dataaccess.*;
import model.*;

import java.util.ArrayList;
import java.util.Collection;


public class GameService {

    private final UserAccess userAccess;
    private final AuthAccess authAccess;
    private final GameAccess gameAccess;

    public GameService(UserAccess userAccess, AuthAccess authAccess, GameAccess gameAccess) {
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
    }

    public GameList listGames(String authToken) throws UnauthorizedException, DataAccessException {
        if (authAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }
        Collection<GameData> gameList = gameAccess.listGames();
        if (gameList == null) {
            gameList = new ArrayList<>();
        }
        GameList gamesList = new GameList(gameList);
        return gamesList;
    }

    public GameResult createGame(GameRequest gameRequest) throws UnauthorizedException, BadRequestException, DataAccessException {
        String authToken = gameRequest.authToken();
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

    public void joinGame(GameJoinRequest gameJoinRequest) throws UnauthorizedException,
            AlreadyTakenException, BadRequestException, DataAccessException {
        String authToken = gameJoinRequest.authToken();
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
        if ((gameJoinRequest.playerColor().equals("BLACK") && gameData.blackUsername() != null)
                || (gameJoinRequest.playerColor().equals("WHITE") && gameData.whiteUsername() != null)) {
            throw new AlreadyTakenException();
        }
        gameAccess.updateGame(gameJoinRequest.playerColor(), authData.username(), gameJoinRequest.gameID());
    }

    public void clear() throws DataAccessException {
        userAccess.clear();
        gameAccess.clear();
        authAccess.clear();
    }
}
