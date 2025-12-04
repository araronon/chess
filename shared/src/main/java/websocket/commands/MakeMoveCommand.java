package websocket.commands;

public class MakeMoveCommand extends UserGameCommand {
    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
    }
//    public MakeMoveCommand(//move argument) {
//        super(commandType, authToken, gameID);
//    }
}
