package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    private ChessMove move;
    public MakeMoveCommand(String authToken, Integer gameID, ChessMove move) {
        super(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }
//    public MakeMoveCommand(//move argument) {
//        super(commandType, authToken, gameID);
//    }
}
