package dataaccess;

import chess.ChessGame;
import model.*;

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

    }

