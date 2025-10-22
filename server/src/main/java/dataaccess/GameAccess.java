
package dataaccess;
import model.*;

public interface GameAccess {
    void clear();
    int createGame(String gameName);
    GameData getGame(int gameID);
}