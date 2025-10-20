package dataaccess;
import model.UserData;
import java.util.HashMap;

public class UserAccess {
    private HashMap<String, UserData> usermap;

    public UserData getUser(String username, String password) throws DataAccessException {
        UserData userinfo = usermap.get(username);
        if (userinfo == null) {
            return null;
        }
        if (password == userinfo.password() && (username == userinfo.username())) {
            return userinfo;
        } else {
            return null;
        }
    }
}
