package server;

import io.javalin.*;
import io.javalin.http.Context;
import com.google.gson.Gson;
import service.LoginRequest;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final UserService;

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

    public void login(Context context) {
        var serializer = new Gson();
        String reqJson = context.body();
        var loginReq = serializer.fromJson(reqJson, LoginRequest.class);
        var authData = UserService.login(loginReq);
        context.result(serializer.toJson(authData));

    }

}
