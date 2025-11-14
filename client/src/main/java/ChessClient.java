
import java.util.Arrays;
import java.util.Scanner;
import com.google.gson.Gson;
import model.*;
import server.ResponseException;
import server.ServerFacade;
import service.*;


import static ui.EscapeSequences.*;

public class ChessClient  {
    private String visitorName = null;
    private ServerFacade server;
    private State state = State.LOGGEDOUT;
    private String authToken;

    public ChessClient(String serverUrl) throws ResponseException {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println(" Welcome to chess. Sign in to start.");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }


//    public void notify(Notification notification) {
//        System.out.println(RED + notification.message());
//        printPrompt();
//    }
//
    private void printPrompt() {
        if (state == State.LOGGEDOUT) {
            System.out.print("\n" + "[LOGGEDOUT] " + ">>> " + " ");
        } else {
            System.out.print("\n" + "[LOGGEDIN] " + ">>> " + " ");
        }
    }
//
//
    public String eval(String input) {
        try {
            String[] tokens = input.split(" ");
            String cmd = tokens[0].toLowerCase();
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
//                case "help" -> help();
                case "logout" -> logout();
//                case "creategame" -> createGame(params);
//                case "listgames" -> listgames();
//                case "joingame" -> joingame(params);
//                case "observegame" -> observeGame(params);
//                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
//        catch(Exception ex) {
//        return "Error: " + ex.getMessage();
//    }
    }

    public String help() {
        if (state == State.LOGGEDOUT) {
            return """
                    - login <yourname> <yourpassword> - logs you in
                    - register <yourname> <yourpassword> <youremail> - register an account
                    - help - get help
                    - quit - close the game
                    """;
        }
        return """
                - logout - sign out of your account
                - creategame <gamename> - create a new chess game
                - listgames - list all the chess games available
                - observegame <gameID> - look at a chess game
                - joingame <gameID> <WHITE|BLACK> - join a chess game
                - quit
                """;
    }



    public String login(String... params) throws ResponseException {
        assertLoggedOut();
        if (params.length == 2) {
            LoginRequest loginRequest = new LoginRequest(params[0], params[1]);
            LoginResult loginResult = server.login(loginRequest);
            state = State.LOGGEDIN;
            visitorName = params[0];
            authToken = loginResult.authToken();
            return String.format("You logged in as %s.", visitorName);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <yourname> <yourpassword>");
    }

    public String logout(String... params) throws ResponseException {
        assertLoggedIn();
        if (params.length == 0) {
            server.logout(authToken);
            state = State.LOGGEDOUT;
            visitorName = params[0];
            authToken = "no AuthToken here";
            return String.format("You logged out.");
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: no additional parameters");
    }

    public String register(String... params) throws ResponseException {
        assertLoggedOut();
        if (params.length == 3) {
            RegisterRequest registerRequest = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult registerResult = server.register(registerRequest);
            state = State.LOGGEDIN;
            visitorName = params[0];
            authToken = registerResult.authToken();
            return String.format("You logged in as %s.", visitorName);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <yourname> <yourpassword> <youremail>");
    }
//
//    public String rescuePet(String... params) throws ResponseException {
//        assertSignedIn();
//        if (params.length >= 2) {
//            String name = params[0];
//            PetType type = PetType.valueOf(params[1].toUpperCase());
//            var pet = new Pet(0, name, type);
//            pet = server.addPet(pet);
//            return String.format("You rescued %s. Assigned ID: %d", pet.name(), pet.id());
//        }
//        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <name> <CAT|DOG|FROG>");
//    }
//
//    public String listPets() throws ResponseException {
//        assertSignedIn();
//        PetList pets = server.listPets();
//        var result = new StringBuilder();
//        var gson = new Gson();
//        for (Pet pet : pets) {
//            result.append(gson.toJson(pet)).append('\n');
//        }
//        return result.toString();
//    }
//
//    public String adoptPet(String... params) throws ResponseException {
//        assertSignedIn();
//        if (params.length == 1) {
//            try {
//                int id = Integer.parseInt(params[0]);
//                Pet pet = getPet(id);
//                if (pet != null) {
//                    server.deletePet(id);
//                    return String.format("%s says %s", pet.name(), pet.sound());
//                }
//            } catch (NumberFormatException ignored) {
//            }
//        }
//        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <pet id>");
//    }
//
//    public String adoptAllPets() throws ResponseException {
//        assertSignedIn();
//        var buffer = new StringBuilder();
//        for (Pet pet : server.listPets()) {
//            buffer.append(String.format("%s says %s%n", pet.name(), pet.sound()));
//        }
//
//        server.deleteAllPets();
//        return buffer.toString();
//    }
//
//    public String signOut() throws ResponseException {
//        assertSignedIn();
//        ws.leavePetShop(visitorName);
//        state = State.SIGNEDOUT;
//        return String.format("%s left the shop", visitorName);
//    }
//
//    private Pet getPet(int id) throws ResponseException {
//        for (Pet pet : server.listPets()) {
//            if (pet.id() == id) {
//                return pet;
//            }
//        }
//        return null;
//    }
//
//    public String help() {
//        if (state == State.SIGNEDOUT) {
//            return """
//                    - signIn <yourname>
//                    - quit
//                    """;
//        }
//        return """
//                - list
//                - adopt <pet id>
//                - rescue <name> <CAT|DOG|FROG|FISH>
//                - adoptAll
//                - signOut
//                - quit
//                """;
//    }
//
    private void assertLoggedIn() throws ResponseException {
        if (state == State.LOGGEDOUT) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must log in");
        }
    }
    private void assertLoggedOut() throws ResponseException {
        if (state == State.LOGGEDIN) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must log out first");
        }
    }
}