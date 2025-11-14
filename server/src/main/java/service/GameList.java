package service;

import model.GameData;

import java.util.Collection;
import java.util.Map;

public record GameList(Collection<GameData> games) {
}
