package service;
import dataaccess.*;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {


    private final UserAccess userAccess;
    private final AuthAccess authAccess;
    private final GameAccess gameAccess;

    public UserService(UserAccess userAccess, AuthAccess authAccess, GameAccess gameAccess) {
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws BadRequestException, AlreadyTakenException, DataAccessException {
        UserData userCheck = userAccess.getUser(registerRequest.username());
        if (userCheck != null) {
            throw new AlreadyTakenException();
        }
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            throw new BadRequestException();
        }
        userAccess.createUser(new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));
        AuthData authInfo = authAccess.createAuth(registerRequest.username());
        RegisterResult registerResult = new RegisterResult(authInfo.username(),authInfo.authToken());
        return registerResult;
    }

    public LoginResult login(LoginRequest loginRequest) throws BadRequestException, UnauthorizedException, DataAccessException {
        if (loginRequest.username() == null || loginRequest.password() == null) {
            throw new BadRequestException();
        }
        UserData userInfo = userAccess.getUser(loginRequest.username());
        if (userInfo == null) {
            throw new UnauthorizedException();
        }
        if (verifyUser(userInfo.password(),loginRequest.password()) && (loginRequest.username().equals(userInfo.username()))) {
            AuthData authInfo = authAccess.createAuth(loginRequest.username());
            LoginResult loginResult = new LoginResult(authInfo.authToken(),authInfo.username());
            return loginResult;
        } else {
            throw new UnauthorizedException();
        }
    }

    boolean verifyUser(String hashedPassword, String providedClearTextPassword) {
        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    public void logout(String authToken) throws UnauthorizedException, DataAccessException {
        if (authAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }
        authAccess.deleteAuth(authToken);
    }
    public void clear() throws DataAccessException {
        userAccess.clear();
        gameAccess.clear();
        authAccess.clear();
    }
}
