package client;
// Chess
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ServerFacadeTests {

    private static Server server;
    private static final String TEST_USER = "TEST_USER5";
    private static final String TEST_PASSWORD = "TEST_PASSWORD";
    private static final String TEST_EMAIL = "TEST_EMAIL";
    private static final String FALSE_PASSWORD = "falsePassword";
    private static String serverUrl;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverUrl = "http://localhost:" + port + "/";
        serverFacade = new ServerFacade(serverUrl);
        serverFacade.clear();
    }

    @BeforeEach
    void clearall() throws ResponseException {
        serverFacade.clear();
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
        Assertions.assertNotNull(registerResultActual.authToken(), "AuthToken registered");
    }

    @Test
    @Order(2)
    @DisplayName("Negative - Registration")
    public void failedRegistration() throws BadRequestException, AlreadyTakenException, ResponseException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, null, TEST_EMAIL);
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.register(registerRequestTest));
    }

    @Test
    @Order(3)
    @DisplayName("Positive - Login")
    public void successfulLogin() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException, ResponseException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultTest = serverFacade.register(registerRequestTest);
        serverFacade.logout(registerResultTest.authToken());
        LoginRequest loginRequestTest = new LoginRequest(TEST_USER, TEST_PASSWORD);
        LoginResult loginResultActual = serverFacade.login(loginRequestTest);
        Assertions.assertEquals(loginResultActual.username(), loginRequestTest.username(), "Username registered");
        Assertions.assertNotNull(loginResultActual.authToken(), "AuthToken registered");
    }

    @Test
    @Order(4)
    @DisplayName("Negative - Login")
    public void failedLogin() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException, ResponseException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultTest = serverFacade.register(registerRequestTest);
        LoginRequest loginRequestTest = new LoginRequest(TEST_USER, FALSE_PASSWORD);
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(loginRequestTest));
    }

    @Test
    @Order(5)
    @DisplayName("Positive - Logout")
    public void successfulLogout() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException, ResponseException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultTest = serverFacade.register(registerRequestTest);
        serverFacade.logout(registerResultTest.authToken());
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.createGame(new GameRequest("test", "test")));

    }

    @Test
    @Order(6)
    @DisplayName("Negative - Logout")
    public void failedLogout() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException, ResponseException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultTest = serverFacade.register(registerRequestTest);
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.logout("Fake Auth Token"));
    }

    @Test
    @Order(7)
    @DisplayName("Positive - Create Game")
    public void successfulCreateGame() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException, ResponseException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultTest = serverFacade.register(registerRequestTest);
        serverFacade.logout(registerResultTest.authToken());
        LoginRequest loginRequestTest = new LoginRequest(TEST_USER, TEST_PASSWORD);
        LoginResult loginResultTest = serverFacade.login(loginRequestTest);
        GameRequest gameRequestTest = new GameRequest("My Game", loginResultTest.authToken());
        GameResult gameResultActual = serverFacade.createGame(gameRequestTest);
        Assertions.assertEquals(1001, gameResultActual.gameID());
    }

    @Test
    @Order(8)
    @DisplayName("Negative - Create Game")
    public void failedCreateGame() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException, ResponseException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultTest = serverFacade.register(registerRequestTest);
        serverFacade.logout(registerResultTest.authToken());
        LoginRequest loginRequestTest = new LoginRequest(TEST_USER, TEST_PASSWORD);
        LoginResult loginResultTest = serverFacade.login(loginRequestTest);
        GameRequest gameRequestTest = new GameRequest(null, loginResultTest.authToken());
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.createGame(gameRequestTest));
    }

    @Test
    @Order(9)
    @DisplayName("Positive - List Game")
    public void successfulListGame() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException, ResponseException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultTest = serverFacade.register(registerRequestTest);
        serverFacade.logout(registerResultTest.authToken());
        LoginRequest loginRequestTest = new LoginRequest(TEST_USER, TEST_PASSWORD);
        LoginResult loginResultTest = serverFacade.login(loginRequestTest);
        GameRequest gameRequestTest = new GameRequest("My Game", loginResultTest.authToken());
        GameResult gameResultActual = serverFacade.createGame(gameRequestTest);
        Collection<GameData> gameListFirst = serverFacade.listGames(loginResultTest.authToken()).games();
        List<GameData> gameList = new ArrayList<>(gameListFirst);
        Assertions.assertEquals(gameList.get(0).gameName(), "My Game");
    }

    @Test
    @Order(10)
    @DisplayName("Negative - List Game")
    public void failedListGame() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException, ResponseException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultTest = serverFacade.register(registerRequestTest);
        serverFacade.logout(registerResultTest.authToken());
        LoginRequest loginRequestTest = new LoginRequest(TEST_USER, TEST_PASSWORD);
        LoginResult loginResultTest = serverFacade.login(loginRequestTest);
        GameRequest gameRequestTest = new GameRequest("My Game", loginResultTest.authToken());
        GameResult gameResultActual = serverFacade.createGame(gameRequestTest);
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.listGames("Not a valid authToken"));
    }

    @Test
    @Order(11)
    @DisplayName("Positive - Join Game")
    public void successfulJoinGame() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException, ResponseException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultTest = serverFacade.register(registerRequestTest);
        serverFacade.logout(registerResultTest.authToken());
        LoginRequest loginRequestTest = new LoginRequest(TEST_USER, TEST_PASSWORD);
        LoginResult loginResultTest = serverFacade.login(loginRequestTest);
        GameRequest gameRequestTest = new GameRequest("My Game", loginResultTest.authToken());
        GameResult gameResultActual = serverFacade.createGame(gameRequestTest);
        GameJoinRequest gameJoinRequestTest = new GameJoinRequest("BLACK", 1001, loginResultTest.authToken());
        serverFacade.joinGame(gameJoinRequestTest);
        Collection<GameData> gameListFirst = serverFacade.listGames(loginResultTest.authToken()).games();
        List<GameData> gameList = new ArrayList<>(gameListFirst);
        Assertions.assertEquals(gameList.get(0).blackUsername(), "TEST_USER5");
    }

    @Test
    @Order(12)
    @DisplayName("Negative - Join Game")
    public void failedJoinGame() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException, ResponseException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultTest = serverFacade.register(registerRequestTest);
        serverFacade.logout(registerResultTest.authToken());
        LoginRequest loginRequestTest = new LoginRequest(TEST_USER, TEST_PASSWORD);
        LoginResult loginResultTest = serverFacade.login(loginRequestTest);
        GameRequest gameRequestTest = new GameRequest("My Game", loginResultTest.authToken());
        GameResult gameResultActual = serverFacade.createGame(gameRequestTest);
        GameJoinRequest gameJoinRequestTest = new GameJoinRequest("GREEN", 1001, loginResultTest.authToken());
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.joinGame(gameJoinRequestTest));
    }

    @Test
    @Order(13)
    @DisplayName("Positive - Clear")
    public void successfulClear() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException, ResponseException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultTest = serverFacade.register(registerRequestTest);
        serverFacade.logout(registerResultTest.authToken());
        LoginRequest loginRequestTest = new LoginRequest(TEST_USER, TEST_PASSWORD);
        LoginResult loginResultTest = serverFacade.login(loginRequestTest);
        GameRequest gameRequestTest = new GameRequest("My Game", loginResultTest.authToken());
        GameResult gameResultActual = serverFacade.createGame(gameRequestTest);
        serverFacade.clear();
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(loginRequestTest));
    }
}
