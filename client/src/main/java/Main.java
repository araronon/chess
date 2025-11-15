import client.ResponseException;

public class Main {
    public static void main(String[] args) throws ResponseException {
        String serverUrl = "http://localhost:8080/";
        if (args.length == 1) {
            serverUrl = args[0];
        }
        try {
            new ChessClient(serverUrl).run();
        }
        catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}