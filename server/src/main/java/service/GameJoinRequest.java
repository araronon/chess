package service;

public record GameJoinRequest(String playerColor, int gameID, String authToken) {
}
