package service;
import dataaccess.*;
import model.UserData;

public class UserService {
    private UserAccess userAccess = new UserAccess();
        //public RegisterResult register(RegisterRequest registerRequest) {}
        public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
            try {
                UserData userInfo = userAccess.getUser(loginRequest.username(), loginRequest.password());
                AuthData authInfo = createAuth(loginRequest.username())
            }
            catch (DataAccessException ex) {


            }
        }
        //public void logout(LogoutRequest logoutRequest) {}
    }
