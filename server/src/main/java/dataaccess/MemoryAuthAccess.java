package dataaccess;
import java.util.UUID;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthAccess implements AuthAccess {

    private HashMap<String, AuthData> authMap;

    public AuthData createAuth(String username) {
        String newAuthToken = generateToken();
        authMap.put(newAuthToken, new AuthData(newAuthToken, username));
        return authMap.get(newAuthToken);
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}

