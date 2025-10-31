package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static dataaccess.MemoryAuthAccess.generateToken;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLAuthAccess implements AuthAccess {
    public SQLAuthAccess() throws DataAccessException {
        DBI dbi = new DBI();
        dbi.configureDatabase(createStatements);
    }
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  authData (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(authToken)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private AuthData readAuth(ResultSet rs) throws SQLException {
        var authToken = rs.getString("authToken");
        var username = rs.getString("username");
        AuthData authData = new AuthData(authToken, username);
        return authData;
    }


    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String newAuthToken = generateToken();
        var statement = "INSERT INTO authData (authToken, username) VALUES (?, ?)";
        DBI dbi = new DBI();
        dbi.executeUpdate(statement, newAuthToken, username);
        return new AuthData(newAuthToken, username);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM authData WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Data Access Exception");
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (authToken == null) {
            throw new DataAccessException("Data Access Exception");
        }
        var statement = "DELETE FROM authData WHERE authToken=?";
        DBI dbi = new DBI();
        dbi.executeUpdate(statement, authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE authData";
        DBI dbi = new DBI();
        dbi.executeUpdate(statement);
    }
}
