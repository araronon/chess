import client.ResponseException;
import server.Server;
import client.ServerFacade;
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