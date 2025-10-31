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
        sqlUserAccess.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Positive - getUser")
    public void successfulgetUser() throws DataAccessException {
        SQLUserAccess sqlUserAccess = new SQLUserAccess();
        UserData testUser = new UserData("Testusername","Testpassword","Testemail");
        sqlUserAccess.createUser(testUser);
        UserData actualUser = sqlUserAccess.getUser(testUser.username());
        Assertions.assertEquals(testUser.username(), actualUser.username(), "Username registered");
    }
}
