package service;
import dataaccess.*;
import model.*;


public class GameService {
    private MemoryUserAccess userAccess = new MemoryUserAccess();
    private MemoryAuthAccess authAccess = new MemoryAuthAccess();
    private MemoryGameAccess gameAccess = new MemoryGameAccess();

    public GameResult createGame(GameRequest gameRequest, String authToken) throws UnauthorizedException {
        if (authAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }
        UserData user = userAccess.getUser(authToken);
        int gameID = gameAccess.createGame(gameRequest.gameName());
        return new GameResult(gameID);
    }
}
