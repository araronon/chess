package dataaccess;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;

public class MemoryUserAccess implements UserAccess {
    private HashMap<String, UserData> userMap = new HashMap<>();;

    @Override
    public void clear() {
        userMap.clear();
    }

    @Override
    public UserData getUser(String username){
        return userMap.get(username);
    }

    @Override
    public void createUser(UserData user) {
        String hashedPassword = hashUserPassword(user.password());
        userMap.put(user.username(), new UserData(user.username(),hashedPassword,user.email()));
    }

    public String hashUserPassword(String clearTextPassword) {
        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
        return hashedPassword;
    }
}
