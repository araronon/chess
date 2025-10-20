package service;
import dataaccess.*;
import model.*;

public class UserService {
    private MemoryUserAccess userAccess = new MemoryUserAccess();
    private MemoryAuthAccess authAccess = new MemoryAuthAccess();
    private MemoryGameAccess gameAccess = new MemoryGameAccess();
    //public RegisterResult register(RegisterRequest registerRequest) {}
    public LoginResult login(LoginRequest loginRequest) throws BadRequestException, UnauthorizedException {
        UserData userInfo = userAccess.getUser(loginRequest.username());
        if (userInfo == null) {
            throw new BadRequestException();
        }
        if (loginRequest.password() == userInfo.password() && (loginRequest.username() == userInfo.username())) {
            AuthData authInfo = authAccess.createAuth(loginRequest.username());
            LoginResult loginResult = new LoginResult(authInfo.authToken(),authInfo.username());
            return loginResult;
        } else {
            throw new UnauthorizedException();
        }
    }
    public void clear() {
        userAccess.clear();
        gameAccess.clear();
        authAccess.clear();
    }
    //public void logout(LogoutRequest logoutRequest) {}
}
