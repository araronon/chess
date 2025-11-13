package client;

import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import model.GameData;
import org.junit.jupiter.api.*;
import server.ResponseException;
import server.Server;
import server.ServerFacade;
import service.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class ServerFacadeTests {

    private static Server server;
    private static final String TEST_USER = "TEST_USER";
    private static final String TEST_PASSWORD = "TEST_PASSWORD";
    private static final String TEST_EMAIL = "TEST_EMAIL";
    private static final String FALSE_PASSWORD = "falsePassword";
    private static String serverUrl;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        String serverUrl = "http://localhost:" + port + "/";
        serverFacade = new ServerFacade(serverUrl);

    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    @Order(1)
    @DisplayName("Positive - Successful Registration")
    public void successfulRegistration() throws BadRequestException, AlreadyTakenException, DataAccessException, ResponseException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultActual = serverFacade.register(registerRequestTest);
        Assertions.assertEquals(registerResultActual.username(), registerRequestTest.username(), "Username registered");
        Assertions.assertNotNull(registerResultActual.authToken(),"AuthToken registered");
    }

//    @Test
//    @Order(2)
//    @DisplayName("Negative - Registration")
//    public void failedRegistration() throws BadRequestException, AlreadyTakenException {
//        RegisterRequest registerRequestTest = new RegisterRequest(null, null, null);
//        Assertions.assertThrows(BadRequestException.class, () -> serverFacade.register(registerRequestTest),"BadRequest");
//    }

//    @Test
//    @Order(3)
//    @DisplayName("Positive - Login")
//    public void successfulLogin() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
//        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
//        RegisterResult registerResultTest = userService.register(registerRequestTest);
//        LoginRequest loginRequestTest = new LoginRequest(TEST_USER,TEST_PASSWORD);
//        LoginResult loginResultActual = userService.login(loginRequestTest);
//        Assertions.assertEquals(loginResultActual.username(), loginRequestTest.username(), "Username registered");
//        Assertions.assertNotNull(loginResultActual.authToken(),"AuthToken registered");
//    }
//
//    @Test
//    @Order(4)
//    @DisplayName("Negative - Login")
//    public void failedLogin() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
//        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
//        RegisterResult registerResultTest = userService.register(registerRequestTest);
//        LoginRequest loginRequestTest = new LoginRequest(TEST_USER,FALSE_PASSWORD);
//        Assertions.assertThrows(UnauthorizedException.class, ()-> userService.login(loginRequestTest));
//    }
//
//    @Test
//    @Order(5)
//    @DisplayName("Positive - Logout")
//    public void successfulLogout() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
//        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
//        RegisterResult registerResultTest = userService.register(registerRequestTest);
//        LoginRequest loginRequestTest = new LoginRequest(TEST_USER,TEST_PASSWORD);
//        LoginResult loginResultActual = userService.login(loginRequestTest);
//        userService.logout(loginResultActual.authToken());
//        Assertions.assertNotEquals(userService.login(loginRequestTest).authToken(), loginResultActual.authToken());
//    }
//
//    @Test
//    @Order(6)
//    @DisplayName("Negative - Logout")
//    public void failedLogout() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
//        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
//        RegisterResult registerResultTest = userService.register(registerRequestTest);
//        LoginRequest loginRequestTest = new LoginRequest(TEST_USER,TEST_PASSWORD);
//        LoginResult loginResultActual = userService.login(loginRequestTest);
//        Assertions.assertThrows(UnauthorizedException.class, ()->userService.logout("Fake Auth Token"));
//    }
//
//    @Test
//    @Order(7)
//    @DisplayName("Positive - Create Game")
//    public void successfulCreateGame() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
//        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
//        RegisterResult registerResultTest = userService.register(registerRequestTest);
//        LoginRequest loginRequestTest = new LoginRequest(TEST_USER,TEST_PASSWORD);
//        LoginResult loginResultTest = userService.login(loginRequestTest);
//        GameRequest gameRequestTest = new GameRequest("My Game", loginResultTest.authToken());
//        GameResult gameResultActual = gameService.createGame(gameRequestTest);
//        Assertions.assertEquals(1001, gameResultActual.gameID());
//    }
//
//    @Test
//    @Order(8)
//    @DisplayName("Negative - Create Game")
//    public void failedCreateGame() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
//        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
//        RegisterResult registerResultTest = userService.register(registerRequestTest);
//        LoginRequest loginRequestTest = new LoginRequest(TEST_USER,TEST_PASSWORD);
//        LoginResult loginResultTest = userService.login(loginRequestTest);
//        GameRequest gameRequestTest = new GameRequest(null, loginResultTest.authToken());
//        Assertions.assertThrows(BadRequestException.class, ()->gameService.createGame(gameRequestTest));
//    }
//
//    @Test
//    @Order(9)
//    @DisplayName("Positive - List Game")
//    public void successfulListGame() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
//        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
//        RegisterResult registerResultTest = userService.register(registerRequestTest);
//        LoginRequest loginRequestTest = new LoginRequest(TEST_USER,TEST_PASSWORD);
//        LoginResult loginResultTest = userService.login(loginRequestTest);
//        GameRequest gameRequestTest = new GameRequest("My Game", loginResultTest.authToken());
//        GameResult gameResultActual = gameService.createGame(gameRequestTest);
//        Map<String,Collection<GameData>> gameMap = gameService.listGames(loginResultTest.authToken()).gameInformation();
//        List<GameData> gameList = new ArrayList<>(gameMap.get("games"));
//        Assertions.assertEquals(gameList.get(0).gameName(), "My Game");
//    }
//
//    @Test
//    @Order(10)
//    @DisplayName("Negative - List Game")
//    public void failedListGame() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
//        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
//        RegisterResult registerResultTest = userService.register(registerRequestTest);
//        LoginRequest loginRequestTest = new LoginRequest(TEST_USER,TEST_PASSWORD);
//        LoginResult loginResultTest = userService.login(loginRequestTest);
//        GameRequest gameRequestTest = new GameRequest("My Game",loginResultTest.authToken());
//        GameResult gameResultActual = gameService.createGame(gameRequestTest);
//        Assertions.assertThrows(UnauthorizedException.class,()-> gameService.listGames("Not a valid authToken"));
//    }
//
//    @Test
//    @Order(11)
//    @DisplayName("Positive - Join Game")
//    public void successfulJoinGame() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
//        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
//        RegisterResult registerResultTest = userService.register(registerRequestTest);
//        LoginRequest loginRequestTest = new LoginRequest(TEST_USER,TEST_PASSWORD);
//        LoginResult loginResultTest = userService.login(loginRequestTest);
//        GameRequest gameRequestTest = new GameRequest("My Game",loginResultTest.authToken());
//        GameResult gameResultActual = gameService.createGame(gameRequestTest);
//        GameJoinRequest gameJoinRequestTest = new GameJoinRequest("BLACK", 1001, loginResultTest.authToken());
//        gameService.joinGame(gameJoinRequestTest);
//        Map<String,Collection<GameData>> gameMap = gameService.listGames(loginResultTest.authToken()).gameInformation();
//        List<GameData> gameList = new ArrayList<>(gameMap.get("games"));
//        Assertions.assertEquals(gameList.get(0).blackUsername(), "TEST_USER");
//    }
//
//    @Test
//    @Order(12)
//    @DisplayName("Negative - Join Game")
//    public void failedJoinGame() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
//        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
//        RegisterResult registerResultTest = userService.register(registerRequestTest);
//        LoginRequest loginRequestTest = new LoginRequest(TEST_USER,TEST_PASSWORD);
//        LoginResult loginResultTest = userService.login(loginRequestTest);
//        GameRequest gameRequestTest = new GameRequest("My Game", loginResultTest.authToken());
//        GameResult gameResultActual = gameService.createGame(gameRequestTest);
//        GameJoinRequest gameJoinRequestTest = new GameJoinRequest("GREEN", 1001, loginResultTest.authToken());
//        Assertions.assertThrows(BadRequestException.class,()-> gameService.joinGame(gameJoinRequestTest));
//    }
//
//    @Test
//    @Order(13)
//    @DisplayName("Positive - Clear")
//    public void successfulClear() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
//        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
//        RegisterResult registerResultTest = userService.register(registerRequestTest);
//        LoginRequest loginRequestTest = new LoginRequest(TEST_USER,TEST_PASSWORD);
//        LoginResult loginResultTest = userService.login(loginRequestTest);
//        GameRequest gameRequestTest = new GameRequest("My Game", loginResultTest.authToken());
//        GameResult gameResultActual = gameService.createGame(gameRequestTest);
//        gameService.clear();
//        Assertions.assertThrows(UnauthorizedException.class, ()-> userService.login(loginRequestTest));
//    }

}
