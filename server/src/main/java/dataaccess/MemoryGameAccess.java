package dataaccess;

import chess.ChessGame;
import model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class MemoryGameAccess implements GameAccess {

    private HashMap<Integer, GameData> gameMap = new HashMap<>();;
    @Override
    public void clear() {
        gameMap.clear();
    }

    @Override
    public int createGame(String gameName) {
        int newGameId = 1000;
        for (int i = 0; i < 3000; i++) {
            newGameId += 1;
            if (!gameMap.containsKey(newGameId)) {
                break;
            }
        }
        gameMap.put(newGameId, new GameData(newGameId, null, null, gameName, new ChessGame()));
        return newGameId;
        }

        @Override
    public GameData getGame(int gameID) {
        return gameMap.get(gameID);
        }

    @Override
    public void updateGame(String playerColor, String username, int gameID) {
        GameData gameData = getGame(gameID);
        if (playerColor.equals("BLACK")) {
            GameData newGameData = new GameData(gameData.gameID(),gameData.whiteUsername(),username,gameData.gameName(),gameData.game());
            gameMap.put(gameID,newGameData);
        }
        else {
            GameData newGameData = new GameData(gameData.gameID(),username,gameData.blackUsername(),gameData.gameName(),gameData.game());
            gameMap.put(gameID,newGameData);
        }
    }

    @Override
    public Collection<GameData> listGames() {
        Collection<GameData> gameList = new ArrayList<>(gameMap.values());
        return gameList;
    }
}

