
import java.util.*;

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
    private HashMap<String, String> numberToId = new HashMap<>();

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
                System.out.println(result);
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
                case "help" -> help();
                case "?" -> help();
                case "logout" -> logout();
                case "creategame" -> createGame(params);
                case "listgames" -> listGames(params);
                case "joingame" -> joinGame(params);
//                case "observegame" -> observeGame(params);
                case "quit" -> "quit";
                default -> unrecognizedCmd();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
//        catch(Exception ex) {
//        return "Error: " + ex.getMessage();
//    }
    }

    public String unrecognizedCmd() {
        return """
                Command not recognized. Type "help" for a list of possible commands
                """;
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
        throw new ResponseException("Expected: <yourname> <yourpassword>");
    }

    public String logout(String... params) throws ResponseException {
        assertLoggedIn();
        if (params.length == 0) {
            server.logout(authToken);
            state = State.LOGGEDOUT;
            visitorName = null;
            authToken = null;
            return String.format("You logged out.");
        }
        throw new ResponseException("Expected: no additional parameters");
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
        throw new ResponseException("Expected: <yourname> <yourpassword> <youremail>");
    }

    public String createGame(String... params) throws ResponseException {
        assertLoggedIn();
        if (params.length == 1) {
            String gameName = params[0];
            GameRequest gameRequest = new GameRequest(gameName, authToken);
            GameResult gameResult = server.createGame(gameRequest);
            return String.format("Successfully created game %s", gameRequest.gameName());
        }
        throw new ResponseException("Expected: no additional parameters");
    }

    public String listGames(String... params) throws ResponseException {
        assertLoggedIn();
        if (params.length == 0) {
            List<GameData> gameList = new ArrayList<>(server.listGames(authToken).games());
            int i = 1;
            StringBuilder stringList = new StringBuilder();
            for (GameData gameData : gameList) {
                stringList.append(String.format("%d. Name: %s, Black Player: %s, White Player: %s\n", i, gameData.gameName(),
                        gameData.blackUsername(), gameData.whiteUsername()));
                numberToId.put(String.valueOf(i), String.valueOf(gameData.gameID()));
                i++;
            }
            return stringList.toString();
        }
        throw new ResponseException("Expected: no additional parameters");
    }

    public String joinGame(String... params) throws ResponseException {
        assertLoggedIn();
        if (params.length == 2) {
            String gameNumber = params[0];
            String playerColor = params[1].toUpperCase();
            List<GameData> gameList = new ArrayList<>(server.listGames(authToken).games());
            String currentGameID;
            for (GameData gameData : gameList) {
                currentGameID = String.valueOf(gameData.gameID());
                if (currentGameID.equals(numberToId.get(gameNumber))) {
                    GameJoinRequest gameJoinRequest = new GameJoinRequest(playerColor, Integer.parseInt(currentGameID), authToken);
                    server.joinGame(gameJoinRequest);
                    // print out board with respect to the playercolor
                    return String.format("Successfully joined the game.");
                }
            }
        }
        throw new ResponseException("Expected: no additional parameters");
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
            throw new ResponseException("You must log in");
        }
    }
    private void assertLoggedOut() throws ResponseException {
        if (state == State.LOGGEDIN) {
            throw new ResponseException("You must log out first");
        }
    }
}