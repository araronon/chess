package service.service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;
import passoff.model.*;
import service.*;
import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceTests {

    private static final String TEST_USER = "TEST_USER";
    private static final String TEST_PASSWORD = "TEST_PASSWORD";
    private static final String TEST_EMAIL = "TEST_EMAIL";
    private static final String FALSE_PASSWORD = "falsePassword";
    private MemoryUserAccess userAccess = new MemoryUserAccess();
    private MemoryGameAccess gameAccess = new MemoryGameAccess();
    private MemoryAuthAccess authAccess = new MemoryAuthAccess();
    private UserService userService = new UserService(userAccess, authAccess, gameAccess);
    private GameService gameService = new GameService(userAccess, authAccess, gameAccess);

    @BeforeEach
    public void setup() throws DataAccessException {
        userService.clear();
        gameService.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Positive - Successful Registration")
    public void successfulRegistration() throws BadRequestException, AlreadyTakenException, DataAccessException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultActual = userService.register(registerRequestTest);
        Assertions.assertEquals(registerResultActual.username(), registerRequestTest.username(), "Username registered");
        Assertions.assertNotNull(registerResultActual.authToken(),"AuthToken registered");
    }

    @Test
    @Order(2)
    @DisplayName("Negative - Registration")
    public void failedRegistration() throws BadRequestException, AlreadyTakenException {
        RegisterRequest registerRequestTest = new RegisterRequest(null, null, null);
        Assertions.assertThrows(BadRequestException.class, () -> userService.register(registerRequestTest),"BadRequest");
    }

    @Test
    @Order(3)
    @DisplayName("Positive - Login")
    public void successfulLogin() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultTest = userService.register(registerRequestTest);
        LoginRequest loginRequestTest = new LoginRequest(TEST_USER,TEST_PASSWORD);
        LoginResult loginResultActual = userService.login(loginRequestTest);
        Assertions.assertEquals(loginResultActual.username(), loginRequestTest.username(), "Username registered");
        Assertions.assertNotNull(loginResultActual.authToken(),"AuthToken registered");
    }

    @Test
    @Order(4)
    @DisplayName("Negative - Login")
    public void failedLogin() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultTest = userService.register(registerRequestTest);
        LoginRequest loginRequestTest = new LoginRequest(TEST_USER,FALSE_PASSWORD);
        Assertions.assertThrows(UnauthorizedException.class, ()-> userService.login(loginRequestTest));
    }

    @Test
    @Order(5)
    @DisplayName("Positive - Logout")
    public void successfulLogout() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultTest = userService.register(registerRequestTest);
        LoginRequest loginRequestTest = new LoginRequest(TEST_USER,TEST_PASSWORD);
        LoginResult loginResultActual = userService.login(loginRequestTest);
        userService.logout(loginResultActual.authToken());
        Assertions.assertNotEquals(userService.login(loginRequestTest).authToken(), loginResultActual.authToken());
    }

    @Test
    @Order(6)
    @DisplayName("Negative - Logout")
    public void failedLogout() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultTest = userService.register(registerRequestTest);
        LoginRequest loginRequestTest = new LoginRequest(TEST_USER,TEST_PASSWORD);
        LoginResult loginResultActual = userService.login(loginRequestTest);
        Assertions.assertThrows(UnauthorizedException.class, ()->userService.logout("Fake Auth Token"));
    }

    @Test
    @Order(7)
    @DisplayName("Positive - Create Game")
    public void successfulCreateGame() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultTest = userService.register(registerRequestTest);
        LoginRequest loginRequestTest = new LoginRequest(TEST_USER,TEST_PASSWORD);
        LoginResult loginResultTest = userService.login(loginRequestTest);
        GameRequest gameRequestTest = new GameRequest("My Game", loginResultTest.authToken());
        GameResult gameResultActual = gameService.createGame(gameRequestTest);
        Assertions.assertEquals(1001, gameResultActual.gameID());
    }

    @Test
    @Order(8)
    @DisplayName("Negative - Create Game")
    public void failedCreateGame() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultTest = userService.register(registerRequestTest);
        LoginRequest loginRequestTest = new LoginRequest(TEST_USER,TEST_PASSWORD);
        LoginResult loginResultTest = userService.login(loginRequestTest);
        GameRequest gameRequestTest = new GameRequest(null, loginResultTest.authToken());
        Assertions.assertThrows(BadRequestException.class, ()->gameService.createGame(gameRequestTest));
    }

    @Test
    @Order(9)
    @DisplayName("Positive - List Game")
    public void successfulListGame() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultTest = userService.register(registerRequestTest);
        LoginRequest loginRequestTest = new LoginRequest(TEST_USER,TEST_PASSWORD);
        LoginResult loginResultTest = userService.login(loginRequestTest);
        GameRequest gameRequestTest = new GameRequest("My Game", loginResultTest.authToken());
        GameResult gameResultActual = gameService.createGame(gameRequestTest);
        Collection<GameData> gameListFirst = gameService.listGames(loginResultTest.authToken()).games();
        List<GameData> gameList = new ArrayList<>(gameListFirst);
        Assertions.assertEquals(gameList.get(0).gameName(), "My Game");
    }

    @Test
    @Order(10)
    @DisplayName("Negative - List Game")
    public void failedListGame() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultTest = userService.register(registerRequestTest);
        LoginRequest loginRequestTest = new LoginRequest(TEST_USER,TEST_PASSWORD);
        LoginResult loginResultTest = userService.login(loginRequestTest);
        GameRequest gameRequestTest = new GameRequest("My Game",loginResultTest.authToken());
        GameResult gameResultActual = gameService.createGame(gameRequestTest);
        Assertions.assertThrows(UnauthorizedException.class,()-> gameService.listGames("Not a valid authToken"));
    }

    @Test
    @Order(11)
    @DisplayName("Positive - Join Game")
    public void successfulJoinGame() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultTest = userService.register(registerRequestTest);
        LoginRequest loginRequestTest = new LoginRequest(TEST_USER,TEST_PASSWORD);
        LoginResult loginResultTest = userService.login(loginRequestTest);
        GameRequest gameRequestTest = new GameRequest("My Game",loginResultTest.authToken());
        GameResult gameResultActual = gameService.createGame(gameRequestTest);
        GameJoinRequest gameJoinRequestTest = new GameJoinRequest("BLACK", 1001, loginResultTest.authToken());
        gameService.joinGame(gameJoinRequestTest);
        Collection<GameData> gameListFirst = gameService.listGames(loginResultTest.authToken()).games();
        List<GameData> gameList = new ArrayList<>(gameListFirst);
        Assertions.assertEquals(gameList.get(0).blackUsername(), "TEST_USER");
    }

    @Test
    @Order(12)
    @DisplayName("Negative - Join Game")
    public void failedJoinGame() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultTest = userService.register(registerRequestTest);
        LoginRequest loginRequestTest = new LoginRequest(TEST_USER,TEST_PASSWORD);
        LoginResult loginResultTest = userService.login(loginRequestTest);
        GameRequest gameRequestTest = new GameRequest("My Game", loginResultTest.authToken());
        GameResult gameResultActual = gameService.createGame(gameRequestTest);
        GameJoinRequest gameJoinRequestTest = new GameJoinRequest("GREEN", 1001, loginResultTest.authToken());
        Assertions.assertThrows(BadRequestException.class,()-> gameService.joinGame(gameJoinRequestTest));
    }

    @Test
    @Order(13)
    @DisplayName("Positive - Clear")
    public void successfulClear() throws BadRequestException, AlreadyTakenException, UnauthorizedException, DataAccessException {
        RegisterRequest registerRequestTest = new RegisterRequest(TEST_USER, TEST_PASSWORD, TEST_EMAIL);
        RegisterResult registerResultTest = userService.register(registerRequestTest);
        LoginRequest loginRequestTest = new LoginRequest(TEST_USER,TEST_PASSWORD);
        LoginResult loginResultTest = userService.login(loginRequestTest);
        GameRequest gameRequestTest = new GameRequest("My Game", loginResultTest.authToken());
        GameResult gameResultActual = gameService.createGame(gameRequestTest);
        gameService.clear();
        Assertions.assertThrows(UnauthorizedException.class, ()-> userService.login(loginRequestTest));
    }
}