package dataaccess;
import model.UserData;

public interface UserAccess {
    void clear() throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void createUser(UserData user) throws DataAccessException;
}
