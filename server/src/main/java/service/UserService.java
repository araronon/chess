package service;
import dataaccess.*;
import model.*;

public class UserService {


    private final MemoryUserAccess userAccess;
    private final MemoryAuthAccess authAccess;
    private final MemoryGameAccess gameAccess;

    public UserService(MemoryUserAccess userAccess, MemoryAuthAccess authAccess, MemoryGameAccess gameAccess) {
        this.userAccess = userAccess;
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws BadRequestException, AlreadyTakenException {
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

    public LoginResult login(LoginRequest loginRequest) throws BadRequestException, UnauthorizedException {
        if (loginRequest.username() == null || loginRequest.password() == null) {
            throw new BadRequestException();
        }
        UserData userInfo = userAccess.getUser(loginRequest.username());
        if (userInfo == null) {
            throw new UnauthorizedException();
        }
        if (loginRequest.password().equals(userInfo.password()) && (loginRequest.username().equals(userInfo.username()))) {
            AuthData authInfo = authAccess.createAuth(loginRequest.username());
            LoginResult loginResult = new LoginResult(authInfo.authToken(),authInfo.username());
            return loginResult;
        } else {
            throw new UnauthorizedException();
        }
    }

    public void logout(String authToken) throws UnauthorizedException {
        if (authAccess.getAuth(authToken) == null) {
            throw new UnauthorizedException();
        }
        authAccess.deleteAuth(authToken);
    }
    public void clear() {
        userAccess.clear();
        gameAccess.clear();
        authAccess.clear();
    }
    //public void logout(LogoutRequest logoutRequest) {}
}
