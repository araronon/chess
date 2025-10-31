package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLUserAccess implements UserAccess {
    public SQLUserAccess() throws DataAccessException {
        DBI dbi = new DBI();
        dbi.configureDatabase(createStatements);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  userdata (
              `username` varchar(256) NOT NULL,
              `hashedPassword` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private String hashUserPassword(String clearTextPassword) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
        return hashedPassword;
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE userdata";
        DBI dbi = new DBI();
        dbi.executeUpdate(statement);
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var hashedPassword = rs.getString("hashedPassword");
        var email = rs.getString("email");
        UserData userData = new UserData(username, hashedPassword, email);
        return userData;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, hashedPassword, email FROM userdata WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Data Access Exception");
        }
        return null;
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String hashedPassword = hashUserPassword(user.password());
        var statement = "INSERT INTO userdata (username, hashedPassword, email) VALUES (?, ?, ?)";
        DBI dbi = new DBI();
        dbi.executeUpdate(statement, user.username(), hashedPassword, user.email());
    }
}
