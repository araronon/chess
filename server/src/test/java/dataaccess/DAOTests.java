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
    @Order(131)
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
