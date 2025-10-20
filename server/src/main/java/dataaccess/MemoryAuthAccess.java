package dataaccess;
import java.util.UUID;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthAccess implements AuthAccess {

    private HashMap<String, AuthData> authMap = new HashMap<>();;

    public void clear() {
        authMap.clear();
    }

    public AuthData createAuth(String username) {
        String newAuthToken = generateToken();
        authMap.put(newAuthToken, new AuthData(newAuthToken, username));
        return authMap.get(newAuthToken);
    }

    public AuthData getAuth(String authToken) {
        return authMap.get(authToken);
    }

    public void deleteAuth(String authToken) {
        authMap.remove(authToken);
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}

