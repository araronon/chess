package dataaccess;
import model.UserData;
import java.util.HashMap;

public class MemoryUserAccess implements UserAccess {
    private HashMap<String, UserData> usermap;

    @Override
    public void clear() {

    }

    @Override
    public UserData getUser(String username){
        return usermap.get(username);
    }

    @Override
    public void createUser(UserData user) {

    }
}
