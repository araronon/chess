
import java.util.*;

import chess.*;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import model.*;
import client.ResponseException;
import client.ServerFacade;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;


import static ui.EscapeSequences.*;

public class ChessClient implements NotificationHandler {
    private String visitorName = null;
    private ServerFacade server;
    private WebSocketFacade wsserver;
    private State signinstate = State.LOGGEDOUT;
    private State gamestate = State.NOTJOINEDGAME;
    private State observedstate = State.NOTOBSERVINGGAME;
    private String authToken;
    private int globalGameID;
    private ChessGame globalGame;
    private HashMap<String, GameData> numberToId = new HashMap<>();
    private String globalTeamColor;
    private GameData globalGameData;

    public ChessClient(String serverUrl) throws ResponseException {
        server = new ServerFacade(serverUrl);
        wsserver = new WebSocketFacade(serverUrl, this);
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(((NotificationMessage) message));
            case ERROR -> displayError(((ErrorMessage) message));
            case LOAD_GAME -> loadGame(((LoadGameMessage) message));
        }
    }

    public void displayError(ErrorMessage message) {
        System.out.println("\n" + message.getMessage() + "\n");
        printPrompt();
    }

    public void displayNotification(NotificationMessage message) {
        System.out.println("\n" + message.getMessage() + "\n");
        printPrompt();
    }

    public void loadGame(LoadGameMessage message) {
        globalGameData = message.getGame();
        if (visitorName.equals(globalGameData.whiteUsername())) {
            globalTeamColor = "WHITE";
        } else if (visitorName.equals(globalGameData.blackUsername())) {
            globalTeamColor = "BLACK";
        } else {
            globalTeamColor = "WHITE"; // observe the game from white perspective
            return;
        }
        System.out.println("\n");
        printBoard(globalGameData.game(), globalTeamColor);
        System.out.println("\n");
        printPrompt();
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

    private void printPrompt() {
        if (signinstate == State.LOGGEDOUT) {
            System.out.print("\n" + "[LOGGEDOUT] " + ">>> " + " ");
            return;
        }
        if (gamestate == State.JOINEDGAME) {
            System.out.print("\n" + "[INGAME] " + ">>> " + " ");
            return;
        }
        if (observedstate == State.OBSERVINGGAME) {
            System.out.print("\n" + "[OBSERVINGGAME] " + ">>> " + " ");
            return;
        }
        System.out.print("\n" + "[LOGGEDIN] " + ">>> " + " ");
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
                // implement in gameplay
                case "redraw" -> redrawBoard();
                case "leave" -> leaveGame();
                case "makemove" -> makeMove(params);
                case "resign" -> resign();
                case "highlight" -> highlight(params);
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

    public String resign() throws ResponseException {
        assertLoggedIn();
        assertJoinedGame();
        System.out.print("Type y to confirm resignation. Any other key will not confirm");
        Scanner scanner = new Scanner(System.in);
        String confirmation = scanner.nextLine().toLowerCase();
        if (!confirmation.equals("y")) {
            return "Resignation not confirmed.";
        }

        wsserver.resign(visitorName, authToken, globalGameID);
        return "";
    }

    public String redrawBoard() throws ResponseException {
        assertLoggedIn();
        assertInGamePlay();
        printBoard(globalGameData.game(), globalTeamColor);
        return "";
    }

    public String highlight(String... params) throws ResponseException {
        assertLoggedIn();
        assertInGamePlay();
        if (params.length == 1) {
            String startcommand = params[0].toUpperCase();
            if (startcommand.length() != 2) {
                throw new ResponseException("Invalid argument command length for moves. Need a1 b1 format.");
            }
            char colstart = startcommand.charAt(0);
            char rowstart = startcommand.charAt(1);
            if (colstart < 'A' || colstart > 'H' || rowstart < '1'|| rowstart > '8')
            {
                throw new ResponseException("Invalid arguments for moves. Need a1 b1 format.");
            }
            int cols = colstart - 'A' + 1;
            int rows = rowstart - '0';
            ChessPosition desiredPosition = new ChessPosition(rows,cols);
            highlightBoard(desiredPosition, globalGameData.game(), globalTeamColor);
            return String.format("Printing highlighted board.");
        }
        throw new ResponseException("Expected: <chessposition>");
    }

    public String unrecognizedCmd() {
        return """
                Command not recognized. Type "help" for a list of possible commands
                """;
    }

    public String help() {
        if (signinstate == State.LOGGEDOUT) {
            return """
                    - login <yourname> <yourpassword> - logs you in
                    - register <yourname> <yourpassword> <youremail> - register an account
                    - help - get help
                    - quit - close the game
                    """;
        } if (gamestate == State.JOINEDGAME) {
            return """
                - leave - leave the game
                - resign - forfeit to the other player
                - highlight <chessposition> - give all eligible moves for a player
                - redraw - redraws the board from your perspective
                - makemove <startchessposition> <endchessposition> <promotionPiece> - note you must include a valid piece for promotion piece.
                """;
        } if (observedstate == State.OBSERVINGGAME) {
            return """
                - leave - leave the game
                - highlight <chessposition> - give all eligible moves for a player
                - redraw - redraws the board from your perspective
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

    public String makeMove(String ... params) throws ResponseException {
        assertLoggedIn();
        assertJoinedGame();
        if (params.length == 3) {
            String startcommand = params[0].toUpperCase();
            String endcommand = params[1].toUpperCase();
            String promotionPiece = params[2].toUpperCase();
            if (startcommand.length() != 2 || endcommand.length() != 2) {
                throw new ResponseException("Invalid argument command length for moves. Need a1 b1 format.");
            }
            char colstart = startcommand.charAt(0);
            char rowstart = startcommand.charAt(1);
            char colend = endcommand.charAt(0);
            char rowend = endcommand.charAt(1);
            if (colstart < 'A' || colstart > 'H' ||
                    colend < 'A'   || colend > 'H'   ||
                    rowstart < '1'|| rowstart > '8'||
                    rowend < '1'  || rowend > '8') {
                throw new ResponseException("Invalid arguments for moves. Need a1 b1 format.");
            }
            int cols = colstart - 'A' + 1;
            int rows = rowstart - '0';
            int cole = colend - 'A' + 1;
            int rowe = rowend - '0';
            ChessGame game = globalGameData.game();
            ChessPiece piece = game.getBoard().getPiece(new ChessPosition(rowe,cole));
            ChessPiece passedPiece = null;
            ChessPiece.PieceType promotePiece = null;
            if ((rowe == 8 || rowe == 1) && (piece.getPieceType() == ChessPiece.PieceType.PAWN)) {
                switch (promotionPiece) {
                    case "QUEEN" -> promotePiece = ChessPiece.PieceType.QUEEN;
                    case "ROOK" -> promotePiece = ChessPiece.PieceType.ROOK;
                    case "BISHOP" -> promotePiece = ChessPiece.PieceType.BISHOP;
                    case "KNIGHT" -> promotePiece = ChessPiece.PieceType.KNIGHT;
                }
                ChessGame.TeamColor teamColor = null;
                if (globalTeamColor.equals("WHITE")) {
                    teamColor = ChessGame.TeamColor.WHITE;
                } else {
                    teamColor = ChessGame.TeamColor.WHITE;
                }
                passedPiece = new ChessPiece(teamColor,promotePiece);
            }
            wsserver.makeMove(cols, rows, cole, rowe, promotePiece, authToken, globalGameID);
            return "";
        }
        throw new ResponseException("Expected: <startposition> <endposition> <promotionpiece>");
    }

    public String leaveGame(String ... params) throws ResponseException {
        assertLoggedIn();
        assertInGamePlay();
        gamestate = State.NOTJOINEDGAME;
        observedstate = State.NOTOBSERVINGGAME;
        wsserver.leaveGame(visitorName, authToken, globalGameID);
        return String.format("Successfully left the game");
    }

    public String login(String... params) throws ResponseException {
        assertLoggedOut();
        if (params.length == 2) {
            LoginRequest loginRequest = new LoginRequest(params[0], params[1]);
            LoginResult loginResult = server.login(loginRequest);
            signinstate = State.LOGGEDIN;
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
            if (gamestate == State.JOINEDGAME || observedstate == State.OBSERVINGGAME) {
                leaveGame();
            }
            signinstate = State.LOGGEDOUT;
            gamestate = State.NOTJOINEDGAME;
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
            signinstate = State.LOGGEDIN;
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
        assertNotJoinedGame();
        assertNotObservingGame();
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
                    globalTeamColor = playerColor;
                    wsserver.joinGame(visitorName, authToken, gameJoinRequest.gameID());
                    gamestate = State.JOINEDGAME;
                    globalGameID = gameData.gameID();
                    return String.format("Successfully joined the game.");
                }
            }
            throw new ResponseException("Incorrect Input: Game identifier doesn't exist.");
        }
        throw new ResponseException("Expected: <game number> <color>");
    }

    public String observeGame(String... params) throws ResponseException {
        assertLoggedIn();
        assertNotJoinedGame();
        assertNotObservingGame();
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
                    wsserver.joinGame(visitorName, authToken, gameData.gameID());
                    observedstate = State.OBSERVINGGAME;
                    globalGameID = gameData.gameID();
                    printBoard(numberToId.get(gameNumber).game(), "WHITE");
                    return String.format("Observing game %s from the white perspective.", gameNumber);
                }
            }
            throw new ResponseException("Incorrect Input: Game identifier doesn't exist.");
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

    public void highlightBoard(ChessPosition chessPosition, ChessGame game, String playerColor) throws ResponseException {
        ChessBoard board = game.getBoard();
        if (game.getBoard().getPiece(chessPosition) == null) {
            throw new ResponseException("No piece at this position.");
        }
        Collection<ChessMove> validMoves = game.validMoves(chessPosition);
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
                ChessPosition currentPosition = new ChessPosition(row, displaycol);
                for (ChessMove move : validMoves) {
                    ChessPosition validPosition = move.getEndPosition();
                    if (validPosition.equals(currentPosition)) {
                        background += SET_BG_COLOR_BLUE;
                    }
                    if (currentPosition.equals(move.getStartPosition())) {
                        background += SET_BG_COLOR_BLUE;
                    }
                }
                String piece = checkPiece(board.getPiece(currentPosition));
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
        if (signinstate == State.LOGGEDOUT) {
            throw new ResponseException("You must log in");
        }
    }
    private void assertLoggedOut() throws ResponseException {
        if (signinstate == State.LOGGEDIN) {
            throw new ResponseException("You must log out first");
        }
    }

    private void assertNotJoinedGame() throws ResponseException {
        if (gamestate == State.JOINEDGAME) {
            throw new ResponseException("You've already joined a game");
        }
    }

    private void assertJoinedGame() throws ResponseException {
        if (gamestate == State.NOTJOINEDGAME) {
            throw new ResponseException("You need to join a game first");
        }
    }

    private void assertNotObservingGame() throws ResponseException {
        if (observedstate == State.OBSERVINGGAME) {
            throw new ResponseException("You're already observing a game");
        }
    }

    private void assertObservingGame() throws ResponseException {
        if (observedstate == State.NOTOBSERVINGGAME) {
            throw new ResponseException("You need to be observing a game first");
        }
    }

    private void assertInGamePlay() throws ResponseException {
        if ((observedstate == State.NOTOBSERVINGGAME) && (gamestate == State.NOTJOINEDGAME)) {
            throw new ResponseException("To leave a game you need to be participating in a game");
        }
    }


}