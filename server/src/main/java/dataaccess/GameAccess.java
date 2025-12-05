
package dataaccess;
import chess.ChessGame;
import model.*;

import java.util.Collection;

public interface GameAccess {
    void clear() throws DataAccessException;
    int createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(String playerColor, String username, int gameID, ChessGame chessGame) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
}