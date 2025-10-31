package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameAccess implements GameAccess {

    public SQLGameAccess() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  gameData (
              `gameID` varchar(256) NOT NULL,
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`),
              INDEX(gameID)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Data Access Exception",e);
        }
    }

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Data Access Exception", ex);
        }
    }
    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE gameData";
        executeUpdate(statement);
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        if (gameName == null) {
            throw new DataAccessException("Data Access Exception");
        }
        int newGameId = 1000;
        try (Connection conn = DatabaseManager.getConnection()) {
            for (int i = 0; i < 3000; i++) {
                newGameId += 1;
                var statement = "SELECT gameID FROM gameData WHERE gameID=?";
                try (PreparedStatement ps = conn.prepareStatement(statement)) {
                    ps.setInt(1, newGameId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            GameData gameData = new GameData(newGameId, null, null, gameName, new ChessGame());
                            var insertstatement = "INSERT INTO gameData (gameID, json) VALUES (?, ?)";
                            String json = new Gson().toJson(gameData);
                            executeUpdate(insertstatement, newGameId, json);
                            return newGameId;
                        }
                    }
                }
            }
        } catch (Exception e) {
                throw new DataAccessException("Data Access Exception");
            }
        return newGameId;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var json = rs.getString("json");
        Gson gson = new Gson();
        GameData gameData = gson.fromJson(json, GameData.class);
        return gameData;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, json FROM gameData WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Data Access Exception");
        }
        return null;
    }

    @Override
    public void updateGame(String playerColor, String username, int gameID) throws DataAccessException {
        GameData gameData = getGame(gameID);
        try (Connection conn = DatabaseManager.getConnection()) {
            if (playerColor.equals("BLACK")) {
                GameData newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), username, gameData.gameName(), gameData.game());
                var blackstatement = "UPDATE gameData SET json = ? WHERE gameID = ?";
                String json = new Gson().toJson(newGameData);
                executeUpdate(blackstatement, json, gameID);
            } else {
                GameData newGameData = new GameData(gameData.gameID(), username, gameData.blackUsername(), gameData.gameName(), gameData.game());
                var whitestatement = "UPDATE gameData SET json = ? WHERE gameID = ?";
                String json = new Gson().toJson(newGameData);
                executeUpdate(whitestatement, json, gameID);
            }
        } catch(Exception e) {
                throw new DataAccessException("Data Access Exception");
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> gameList = new ArrayList<>();
        Gson gson = new Gson();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT json FROM gameData";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String json = rs.getString("json");
                        GameData gameData = gson.fromJson(json, GameData.class);
                        gameList.add(gameData);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Data Access Exception");
        }
        return gameList;
    }
}