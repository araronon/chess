package server;

import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import com.google.gson.Gson;
import service.LoginRequest;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final UserService userService = new UserService();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        javalin.post("session", this::login);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    public void login(Context context) throws DataAccessException {
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
