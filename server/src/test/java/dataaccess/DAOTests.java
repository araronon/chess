package dataaccess;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;
import passoff.model.*;
import service.*;
import java.util.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DAOTests {

    @BeforeEach
    public void setup() throws DataAccessException {
        SQLUserAccess sqlUserAccess = new SQLUserAccess();
        SQLAuthAccess sqlAuthAccess = new SQLAuthAccess();
        SQLGameAccess sqlGameAccess = new SQLGameAccess();
        sqlUserAccess.clear();
        sqlAuthAccess.clear();
        sqlGameAccess.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Positive - getUser")
    public void successfulGetUser() throws DataAccessException {
        SQLUserAccess sqlUserAccess = new SQLUserAccess();
        UserData testUser = new UserData("Testusername","Testpassword","Testemail");
        sqlUserAccess.createUser(testUser);
        UserData actualUser = sqlUserAccess.getUser(testUser.username());
        Assertions.assertEquals(testUser.username(), actualUser.username(), "Username registered");
    }

    @Test
    @Order(2)
    @DisplayName("Negative - getUser")
    public void unsuccessfulGetUser() throws DataAccessException {
        SQLUserAccess sqlUserAccess = new SQLUserAccess();
        UserData testUser = new UserData("Testusername","Testpassword","Testemail");
        sqlUserAccess.createUser(testUser);
        UserData actualUser = sqlUserAccess.getUser("Nonexistent User");
        Assertions.assertNull(actualUser, "Not a real user");
    }

    @Test
    @Order(3)
    @DisplayName("Positive - createUser")
    public void successfulCreateUser() throws DataAccessException {
        SQLUserAccess sqlUserAccess = new SQLUserAccess();
        UserData testUser = new UserData("Testusername","Testpassword","Testemail");
        sqlUserAccess.createUser(testUser);
        UserData actualUser = sqlUserAccess.getUser(testUser.username());
        Assertions.assertEquals(testUser.email(), actualUser.email(), "Email registered");
    }

    @Test
    @Order(4)
    @DisplayName("Negative - createUser")
    public void unsuccessfulCreateUser() throws DataAccessException {
        SQLUserAccess sqlUserAccess = new SQLUserAccess();
        UserData testUser = new UserData("Testusername","Testpassword","Testemail");
        sqlUserAccess.createUser(testUser);
        Assertions.assertThrows(DataAccessException.class, ()->sqlUserAccess.createUser(testUser));
    }

    @Test
    @Order(5)
    @DisplayName("Positive - createAuth")
    public void successfulCreateAuth() throws DataAccessException {
        SQLAuthAccess sqlAuthAccess = new SQLAuthAccess();
        SQLUserAccess sqlUserAccess = new SQLUserAccess();
        UserData testUser = new UserData("Testusername","Testpassword","Testemail");
        sqlUserAccess.createUser(testUser);
        AuthData authData = sqlAuthAccess.createAuth(testUser.username());
        Assertions.assertNotNull(authData.authToken(), "Successful AuthToken");
    }

    @Test
    @Order(6)
    @DisplayName("Negative - createAuth")
    public void unsuccessfulCreateAuth() throws DataAccessException {
        SQLAuthAccess sqlAuthAccess = new SQLAuthAccess();
        SQLUserAccess sqlUserAccess = new SQLUserAccess();
        UserData testUser = new UserData("Testusername","Testpassword","Testemail");
        sqlUserAccess.createUser(testUser);
        Assertions.assertThrows(DataAccessException.class, ()->sqlAuthAccess.createAuth(null));
    }

    @Test
    @Order(7)
    @DisplayName("Positive - getAuth")
    public void successfulGetAuth() throws DataAccessException {
        SQLAuthAccess sqlAuthAccess = new SQLAuthAccess();
        SQLUserAccess sqlUserAccess = new SQLUserAccess();
        UserData testUser = new UserData("Testusername","Testpassword","Testemail");
        sqlUserAccess.createUser(testUser);
        AuthData authData = sqlAuthAccess.createAuth(testUser.username());
        AuthData authDataGetAuth = sqlAuthAccess.getAuth(authData.authToken());
        Assertions.assertEquals(authData.authToken(),authDataGetAuth.authToken(), "Successful AuthToken");
    }

    @Test
    @Order(8)
    @DisplayName("Negative - getAuth")
    public void unsuccessfulGetAuth() throws DataAccessException {
        SQLAuthAccess sqlAuthAccess = new SQLAuthAccess();
        SQLUserAccess sqlUserAccess = new SQLUserAccess();
        UserData testUser = new UserData("Testusername","Testpassword","Testemail");
        sqlUserAccess.createUser(testUser);
        Assertions.assertNull(sqlAuthAccess.getAuth(testUser.username()));
    }

    @Test
    @Order(9)
    @DisplayName("Positive - deleteAuth")
    public void successfulDeleteAuth() throws DataAccessException {
        SQLAuthAccess sqlAuthAccess = new SQLAuthAccess();
        SQLUserAccess sqlUserAccess = new SQLUserAccess();
        UserData testUser = new UserData("Testusername","Testpassword","Testemail");
        sqlUserAccess.createUser(testUser);
        AuthData authData = sqlAuthAccess.createAuth(testUser.username());
        sqlAuthAccess.deleteAuth(authData.authToken());
        Assertions.assertNull(sqlAuthAccess.getAuth(authData.authToken()));
    }

    @Test
    @Order(10)
    @DisplayName("Negative - deleteAuth")
    public void unsuccessfulDeleteAuth() throws DataAccessException {
        SQLAuthAccess sqlAuthAccess = new SQLAuthAccess();
        SQLUserAccess sqlUserAccess = new SQLUserAccess();
        UserData testUser = new UserData("Testusername","Testpassword","Testemail");
        sqlUserAccess.createUser(testUser);
        Assertions.assertThrows(DataAccessException.class,()-> sqlAuthAccess.deleteAuth(null));
    }

    @Test
    @Order(11)
    @DisplayName("Positive - createGame")
    public void successfulCreateGame() throws DataAccessException {
        SQLAuthAccess sqlAuthAccess = new SQLAuthAccess();
        SQLUserAccess sqlUserAccess = new SQLUserAccess();
        SQLGameAccess sqlGameAccess = new SQLGameAccess();
        UserData testUser = new UserData("Testusername","Testpassword","Testemail");
        sqlUserAccess.createUser(testUser);
        int gameID = sqlGameAccess.createGame("Test Game");
        Assertions.assertEquals(1001,gameID);
    }

    @Test
    @Order(12)
    @DisplayName("Negative - CreateGame")
    public void unsuccessfulCreateGame() throws DataAccessException {
        SQLAuthAccess sqlAuthAccess = new SQLAuthAccess();
        SQLUserAccess sqlUserAccess = new SQLUserAccess();
        SQLGameAccess sqlGameAccess = new SQLGameAccess();
        UserData testUser = new UserData("Testusername","Testpassword","Testemail");
        sqlUserAccess.createUser(testUser);
        Assertions.assertThrows(DataAccessException.class, ()->sqlGameAccess.createGame(null));
    }

    @Test
    @Order(13)
    @DisplayName("Positive - getGame")
    public void successfulGetGame() throws DataAccessException {
        SQLAuthAccess sqlAuthAccess = new SQLAuthAccess();
        SQLUserAccess sqlUserAccess = new SQLUserAccess();
        SQLGameAccess sqlGameAccess = new SQLGameAccess();
        UserData testUser = new UserData("Testusername","Testpassword","Testemail");
        sqlUserAccess.createUser(testUser);
        int gameID = sqlGameAccess.createGame("Test Game");
        GameData gameData = sqlGameAccess.getGame(gameID);
        Assertions.assertEquals(gameData.gameID(),gameID);
    }

    @Test
    @Order(14)
    @DisplayName("Negative - GetGame")
    public void unsuccessfulGetGame() throws DataAccessException {
        SQLAuthAccess sqlAuthAccess = new SQLAuthAccess();
        SQLUserAccess sqlUserAccess = new SQLUserAccess();
        SQLGameAccess sqlGameAccess = new SQLGameAccess();
        UserData testUser = new UserData("Testusername","Testpassword","Testemail");
        sqlUserAccess.createUser(testUser);
        Assertions.assertNull(sqlGameAccess.getGame(39939));
    }

    @Test
    @Order(15)
    @DisplayName("Positive - updateGame")
    public void successfulUpdateGame() throws DataAccessException {
        SQLAuthAccess sqlAuthAccess = new SQLAuthAccess();
        SQLUserAccess sqlUserAccess = new SQLUserAccess();
        SQLGameAccess sqlGameAccess = new SQLGameAccess();
        UserData testUser = new UserData("Testusername","Testpassword","Testemail");
        sqlUserAccess.createUser(testUser);
        int gameID = sqlGameAccess.createGame("Test Game");
        sqlGameAccess.updateGame("BLACK","Testusername",1001, null);
        GameData gameData = sqlGameAccess.getGame(gameID);
        Assertions.assertEquals(gameData.blackUsername(),testUser.username());
    }

    @Test
    @Order(16)
    @DisplayName("Negative - joinGame")
    public void unsuccessfulJoinGame() throws DataAccessException {
        SQLAuthAccess sqlAuthAccess = new SQLAuthAccess();
        SQLUserAccess sqlUserAccess = new SQLUserAccess();
        SQLGameAccess sqlGameAccess = new SQLGameAccess();
        UserData testUser = new UserData("Testusername","Testpassword","Testemail");
        sqlUserAccess.createUser(testUser);
        int gameID = sqlGameAccess.createGame("Test Game");
        Assertions.assertThrows(DataAccessException.class, ()->sqlGameAccess.updateGame("BLACK","Testusername",50000, null));
    }

    @Test
    @Order(17)
    @DisplayName("Positive - listGames")
    public void successfulListGames() throws DataAccessException {
        SQLAuthAccess sqlAuthAccess = new SQLAuthAccess();
        SQLUserAccess sqlUserAccess = new SQLUserAccess();
        SQLGameAccess sqlGameAccess = new SQLGameAccess();
        UserData testUser = new UserData("Testusername","Testpassword","Testemail");
        sqlUserAccess.createUser(testUser);
        int gameID = sqlGameAccess.createGame("Test Game");
        sqlGameAccess.updateGame("BLACK","Testusername",1001, null);
        List<GameData> gameDataActual = new ArrayList<>(sqlGameAccess.listGames());
        Assertions.assertEquals(gameDataActual.get(0).blackUsername(),testUser.username());
    }

    @Test
    @Order(18)
    @DisplayName("Negative - listGames")
    public void unsuccessfulListGames() throws DataAccessException {
        SQLAuthAccess sqlAuthAccess = new SQLAuthAccess();
        SQLUserAccess sqlUserAccess = new SQLUserAccess();
        SQLGameAccess sqlGameAccess = new SQLGameAccess();
        UserData testUser = new UserData("Testusername","Testpassword","Testemail");
        sqlUserAccess.createUser(testUser);
        int gameID = sqlGameAccess.createGame("Test Game");
        List<GameData> gameDataActual = new ArrayList<>(sqlGameAccess.listGames());
        Assertions.assertEquals(gameDataActual.get(0).whiteUsername(),null);
    }



    @Test
    @Order(19)
    @DisplayName("Positive - clear")
    public void successfulClear() throws DataAccessException {
        SQLUserAccess sqlUserAccess = new SQLUserAccess();
        UserData testUser = new UserData("Testusername","Testpassword","Testemail");
        sqlUserAccess.createUser(testUser);
        sqlUserAccess.clear();
        sqlUserAccess.createUser(testUser);
        UserData actualUser = sqlUserAccess.getUser(testUser.username());
        Assertions.assertEquals(testUser.username(), actualUser.username());
    }

}
