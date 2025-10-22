package service;
import dataaccess.*;
import model.*;


public class GameService {
    private final MemoryUserAccess userAccess;
    private final MemoryAuthAccess authAccess;
    private final MemoryGameAccess gameAccess;

    public GameService(MemoryUserAccess userAccess, MemoryAuthAccess authAccess, MemoryGameAccess gameAccess) {
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
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
        if (authAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }
        if (!gameJoinRequest.playerColor().equals("BLACK") && !gameJoinRequest.playerColor().equals("WHITE")) {
            throw new BadRequestException();
        }
        if (gameAccess.getGame(gameJoinRequest.gameID()) == null) {
            throw new BadRequestException();
        }
        GameData gameData = gameAccess.getGame(gameJoinRequest.gameID());
        if (gameData.playerColor())
    }

    public void clear() {
        userAccess.clear();
        gameAccess.clear();
        authAccess.clear();
    }
}
