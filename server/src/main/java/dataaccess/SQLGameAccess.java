package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class SQLGameAccess implements GameAccess {
    @Override
    public void clear() {

    }

    @Override
    public int createGame(String gameName) {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public void updateGame(String playerColor, String username, int gameID) {

    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }
}
