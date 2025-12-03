package server;

import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import com.google.gson.Gson;
import model.GameJoinRequest;
import model.GameRequest;
import model.LoginRequest;
import model.RegisterRequest;
import server.websocket.WebSocketHandler;
import service.*;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final GameService gameService;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        UserAccess userAccess;
        AuthAccess authAccess;
        GameAccess gameAccess;
        try {
            userAccess = new SQLUserAccess();
            authAccess = new SQLAuthAccess();
            gameAccess = new SQLGameAccess();
        }
        catch (Exception ex) {
            userAccess = new MemoryUserAccess();
            authAccess = new MemoryAuthAccess();
            gameAccess = new MemoryGameAccess();
        }

        webSocketHandler = new WebSocketHandler(userAccess, authAccess, gameAccess);
        userService = new UserService(userAccess, authAccess, gameAccess);
        gameService = new GameService(userAccess, authAccess, gameAccess);
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.ws("/ws", ws -> {
            ws.onConnect(ctx -> {
                ctx.enableAutomaticPings();
                System.out.println("Websocket connected");
            });
            ws.onMessage(webSocketHandler);
            ws.onClose(_ -> System.out.println("Websocket closed"));
        });
        javalin.post("session", this::login);
        javalin.delete("db", this::clear);
        javalin.post("user",this::register);
        javalin.delete("session", this::logout);
        javalin.post("game",this::createGame);
        javalin.get("game",this::listGames);
        javalin.put("game",this::joinGame);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    public void joinGame(Context context) {
        try {
            var serializer = new Gson();
            String reqJson = context.body();
            String authToken = context.header("authorization");
            var gameJoinReq = serializer.fromJson(reqJson, GameJoinRequest.class);
            GameJoinRequest newGameJoinReq = new GameJoinRequest(gameJoinReq.playerColor(), gameJoinReq.gameID(), authToken);
            gameService.joinGame(newGameJoinReq);
            context.status(200).result("{}");
        }
        catch (BadRequestException ex) {
            var msg = String.format("{ \"message\": \"Error: bad request\" }");
            context.status(400).result(msg);
        }
        catch (UnauthorizedException ex) {
            var msg = String.format("{ \"message\": \"Error: unauthorized\" }");
            context.status(401).result(msg);
        }
        catch (AlreadyTakenException ex) {
            var msg = String.format("{ \"message\": \"Error: already taken\" }");
            context.status(403).result(msg);
        }
        catch (Exception ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            context.status(500).result(msg);
        }


    }

    public void listGames(Context context) {
        try {
            var serializer = new Gson();
            String authToken = context.header("authorization");
            var gameData = gameService.listGames(authToken);
            context.status(200).result(serializer.toJson(gameData));
        }
        catch (UnauthorizedException ex) {
            var msg = String.format("{ \"message\": \"Error: unauthorized\" }");
            context.status(401).result(msg);
        }
        catch (Exception ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            context.status(500).result(msg);
        }
    }

    public void createGame(Context context) {
        try {
            var serializer = new Gson();
            String reqJson = context.body();
            String authToken = context.header("authorization");
            var gameReq = serializer.fromJson(reqJson, GameRequest.class);
            GameRequest newGameRequest = new GameRequest(gameReq.gameName(), authToken);
            var idData = gameService.createGame(newGameRequest);
            context.status(200).result(serializer.toJson(idData));
        }
        catch (BadRequestException ex) {
            var msg = String.format("{ \"message\": \"Error: bad request\" }");
            context.status(400).result(msg);
        }
        catch (UnauthorizedException ex) {
            var msg = String.format("{ \"message\": \"Error: unauthorized\" }");
            context.status(401).result(msg);
        }
        catch (Exception ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            context.status(500).result(msg);
        }
    }

    public void logout(Context context) {
        try {
            var serializer = new Gson();
            String authToken = context.header("authorization");
            userService.logout(authToken);
            context.status(200).result("{}");
        }
        catch (UnauthorizedException ex) {
            var msg = String.format("{ \"message\": \"Error: unauthorized\" }");
            context.status(401).result(msg);
        }
        catch (Exception ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            context.status(500).result(msg);
        }
    }

    public void clear(Context context) {
        try {
            userService.clear();
            context.status(200).result("{}");
        }
        catch (Exception ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            context.status(500).result(msg);
        }
    }

    public void register(Context context) {
        try {
            var serializer = new Gson();
            String reqJson = context.body();
            var user = serializer.fromJson(reqJson, RegisterRequest.class);
            var authData = userService.register(user);
            context.status(200).result(serializer.toJson(authData));
        }
        catch (BadRequestException ex) {
            var msg = String.format("{ \"message\": \"Error: bad request\" }");
            context.status(400).result(msg);
        }
        catch (AlreadyTakenException ex) {
            var msg = String.format("{ \"message\": \"Error: already taken\" }");
            context.status(403).result(msg);
        }
        catch (Exception ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            context.status(500).result(msg);
        }
    }

    public void login(Context context) {
        try {
            var serializer = new Gson();
            String reqJson = context.body();
            var loginReq = serializer.fromJson(reqJson, LoginRequest.class);
            var authData = userService.login(loginReq);
            context.status(200).result(serializer.toJson(authData));
        }
        catch (BadRequestException ex) {
            var msg = String.format("{ \"message\": \"Error: bad request\" }");
            context.status(400).result(msg);
        }
        catch (UnauthorizedException ex) {
            var msg = String.format("{ \"message\": \"Error: unauthorized\" }");
            context.status(401).result(msg);
        }
        catch (Exception ex) {
            var msg = String.format("{ \"message\": \"Error: %s\" }", ex.getMessage());
            context.status(500).result(msg);
        }
    }

}
