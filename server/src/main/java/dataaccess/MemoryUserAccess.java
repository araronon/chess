package dataaccess;
import model.UserData;
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
        userMap.put(user.username(), user);
    }
}
