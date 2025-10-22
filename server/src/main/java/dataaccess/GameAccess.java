
package dataaccess;
import model.*;

import java.util.Collection;

public interface GameAccess {
    void clear();
    int createGame(String gameName);
    GameData getGame(int gameID);
    void updateGame(String playerColor, String username, int gameID);
    Collection<GameData> listGames();
}