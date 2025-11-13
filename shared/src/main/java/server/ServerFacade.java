package server;

import com.google.gson.Gson;
import service.*;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

//    javalin.post("session", this::login);
//    javalin.delete("db", this::clear);
//    javalin.post("user",this::register);
//    javalin.delete("session", this::logout);
//    javalin.post("game",this::createGame);
//    javalin.get("game",this::listGames);
//    javalin.put("game",this::joinGame);

    public RegisterResult register(RegisterRequest register) throws ResponseException {
        var request = buildRequest("POST", "user", register);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResult.class);
    }

    public LoginResult login(LoginRequest login) throws ResponseException {
        var request = buildRequest("POST", "session", login);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public void logout(String authToken) throws ResponseException {
        var request = buildRequest("DELETE", "session", authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public void clear() throws ResponseException {
        var request = buildRequest("DELETE", "session", null); // nothing passed in
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public GameList listGames(String authToken) throws ResponseException {
        var request = buildRequest("GET", "game", authToken);
        var response = sendRequest(request);
        return handleResponse(response, GameList.class);
    }

    public GameResult createGame(GameRequest gameRequest) throws ResponseException {
        var request = buildRequest("POST", "game", gameRequest);
        var response = sendRequest(request);
        return handleResponse(response, GameResult.class);
    }

    public void joinGame(GameJoinRequest gameRequest) throws ResponseException {
        var request = buildRequest("PUT", "game", gameRequest);
        var response = sendRequest(request);
        handleResponse(response, null);
    }



//    public void deletePet(int id) throws ResponseException {
//        var path = String.format("/pet/%s", id);
//        var request = buildRequest("DELETE", path, null);
//        var response = sendRequest(request);
//        handleResponse(response, null);
//    }
//
//    public void deleteAllPets() throws ResponseException {
//        var request = buildRequest("DELETE", "/pet", null);
//        sendRequest(request);
//    }
//
//    public PetList listPets() throws ResponseException {
//        var request = buildRequest("GET", "/pet", null);
//        var response = sendRequest(request);
//        return handleResponse(response, PetList.class);
//    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}