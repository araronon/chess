
import java.util.*;

import chess.*;
import com.google.gson.Gson;
import model.*;
import server.ResponseException;
import server.ServerFacade;
import service.*;
import ui.EscapeSequences;


import static ui.EscapeSequences.*;

public class ChessClient  {
    private String visitorName = null;
    private ServerFacade server;
    private State state = State.LOGGEDOUT;
    private String authToken;
    private HashMap<String, GameData> numberToId = new HashMap<>();

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
                case "observegame" -> observeGame(params);
                case "quit" -> "quit";
                default -> unrecognizedCmd();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
        catch(Exception ex) {
        return "Error: " + ex.getMessage();
    }
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
            Collections.shuffle(gameList);
            int i = 1;
            StringBuilder stringList = new StringBuilder();
            for (GameData gameData : gameList) {
                stringList.append(String.format("%d. Name: %s, Black Player: %s, White Player: %s\n", i, gameData.gameName(),
                        gameData.blackUsername(), gameData.whiteUsername()));
                numberToId.put(String.valueOf(i), gameData);
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
                if (numberToId.get(gameNumber) == null) {
                    throw new ResponseException("Incorrect Input: Game identifier doesn't exist.");
                }
                if (currentGameID.equals(String.valueOf(numberToId.get(gameNumber).gameID()))) {
                    GameJoinRequest gameJoinRequest = new GameJoinRequest(playerColor, Integer.parseInt(currentGameID), authToken);
                    server.joinGame(gameJoinRequest);
                    printBoard(numberToId.get(gameNumber).game(), playerColor);
                    return String.format("Successfully joined the game.");
                }
                throw new ResponseException("Incorrect Input: Game identifier doesn't exist.");
            }
        }
        throw new ResponseException("Expected: no additional parameters");
    }

    public String observeGame(String... params) throws ResponseException {
        assertLoggedIn();
        if (params.length == 1) {
            String gameNumber = params[0];
            List<GameData> gameList = new ArrayList<>(server.listGames(authToken).games());
            String currentGameID;
            for (GameData gameData : gameList) {
                currentGameID = String.valueOf(gameData.gameID());
                if (numberToId.get(gameNumber) == null) {
                    throw new ResponseException("Incorrect Input: Game identifier doesn't exist.");
                }
                if (currentGameID.equals(String.valueOf(numberToId.get(gameNumber).gameID()))) {
                    printBoard(numberToId.get(gameNumber).game(), "WHITE");
                    return String.format("Observing game %s from the white perspective.", currentGameID);
                }
            }
        }
        throw new ResponseException("Expected: no additional parameters");
    }




    public void printBoard(ChessGame game, String playerColor) {
        ChessBoard board = game.getBoard();
        String boardString = "";
        String background = "";
        String boardLabelString = "";
        int rowstart;
        int rowend;
        int rowcontrol;
        if (playerColor.equals("WHITE")) {
            boardLabelString = SET_BG_COLOR_WHITE + "   " + SET_TEXT_COLOR_BLACK + " a "
                    + " b " + " c " + " d " + " e " + " f " + " g " + " h " + "   " + RESET_BG_COLOR + RESET_TEXT_COLOR + "\n";
            rowstart = 8;
            rowend = 0;
            rowcontrol = -1;
        } else {
            boardLabelString = SET_BG_COLOR_WHITE + "   " + SET_TEXT_COLOR_BLACK + " h "
                    + " g " + " f " + " e " + " d " + " c " + " b " + " a " + "   " + RESET_BG_COLOR + RESET_TEXT_COLOR + "\n";
            rowstart = 1;
            rowend = 9;
            rowcontrol = 1;
        }

        boardString = boardString + boardLabelString;
        int displaycol = 0;
        for (int row = rowstart; (rowcontrol > 0 ? row < rowend : row > rowend); row += rowcontrol) {
            boardString = boardString + SET_TEXT_COLOR_BLACK + String.format(SET_BG_COLOR_WHITE + " %d ", row) + RESET_BG_COLOR;
            for (int col = 1; col < 9; col++) {
                if (playerColor.equals("BLACK")) {
                    displaycol = 9 - col;
                } else {displaycol = col;}
                if ((row + displaycol) % 2 == 0) {
                    background = SET_BG_COLOR_DARK_GREEN;
                } else {
                    background = SET_BG_COLOR_LIGHT_GREY;
                }
                String piece = checkPiece(board.getPiece(new ChessPosition(row, displaycol)));
                boardString = boardString + background + piece + RESET_BG_COLOR;
            }
            boardString = boardString + SET_TEXT_COLOR_BLACK + String.format(SET_BG_COLOR_WHITE + " %d ", row)
                    + RESET_BG_COLOR + RESET_TEXT_COLOR + "\n";
        }
        boardString = boardString + boardLabelString;
        System.out.print(boardString);
    }

    private String checkPiece(ChessPiece piece) {
        if (piece == null) {
            return "   ";
        }
        return switch (piece.getTeamColor()) {
            case ChessGame.TeamColor.WHITE -> switch (piece.getPieceType()) {
                case ChessPiece.PieceType.KING ->  WHITE_KING;
                case ChessPiece.PieceType.ROOK ->  WHITE_ROOK;
                case ChessPiece.PieceType.BISHOP ->  WHITE_BISHOP;
                case ChessPiece.PieceType.PAWN ->  WHITE_PAWN;
                case ChessPiece.PieceType.KNIGHT ->  WHITE_KNIGHT;
                case ChessPiece.PieceType.QUEEN ->  WHITE_QUEEN;
            };
            case ChessGame.TeamColor.BLACK -> switch (piece.getPieceType()) {
                case ChessPiece.PieceType.KING -> BLACK_KING;
                case ChessPiece.PieceType.ROOK -> BLACK_ROOK;
                case ChessPiece.PieceType.BISHOP -> BLACK_BISHOP;
                case ChessPiece.PieceType.PAWN -> BLACK_PAWN;
                case ChessPiece.PieceType.KNIGHT -> BLACK_KNIGHT;
                case ChessPiece.PieceType.QUEEN -> BLACK_QUEEN;
            };
        };
    }

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