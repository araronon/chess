import chess.*;
import server.ResponseException;
import server.Server;
import server.ServerFacade;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.EMPTY;
// Testing

public class Main {
    public static void main(String[] args) throws ResponseException {
        Server server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        String serverUrl = "http://localhost:" + port + "/";
//        String serverUrl = "http://localhost:8080/";
        ServerFacade serverFacade = new ServerFacade(serverUrl);
        serverFacade.clear();
        String boardString = "";
        boardString = SET_BG_COLOR_WHITE + "   " +  SET_BG_COLOR_WHITE + SET_TEXT_COLOR_BLACK + " a "
                + " b "
                + " c "
                + " d "
                + " e "
                + " f "
                + " g "
                + " h "
                + "   " + SET_BG_COLOR_DARK_GREY + WHITE_KING + "   ";
        System.out.print(boardString);
        if (args.length == 1) {
            serverUrl = args[0];
        }

        try {
            new ChessClient(serverUrl).run();

        }
        catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        } finally {server.stop();}
    }

}