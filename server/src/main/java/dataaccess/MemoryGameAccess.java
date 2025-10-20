package dataaccess;

import model.*;

import java.util.HashMap;

public class MemoryGameAccess implements GameAccess {

    private HashMap<String, String> gameMap;
    @Override
    public void clear() {
        gameMap.clear();
    }
}

