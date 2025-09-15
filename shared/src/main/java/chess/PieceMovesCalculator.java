package chess;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class PieceMovesCalculator {

    ChessBoard board;
    ChessPosition myPosition;
    Collection<ChessMove> availableMoves;

    public PieceMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
        this.availableMoves = availableMoves;
    }

    Collection<ChessMove> MoveCalculation() {
        ChessPiece piece = board.getPiece(myPosition);
        switch(piece.getPieceType()) {
            case ChessPiece.PieceType.BISHOP:
                availableMoves = new BishopMovesCalculator(board, myPosition).getMoves();
                break;


        }
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP)
    }



}

class BishopMovesCalculator extends PieceMovesCalculator {

    private Collection<ChessMove> moves;

    public BishopMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    public Collection<ChessMove> getMoves() {
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        ChessPiece currentPiece = board.getPiece(myPosition);
        // Iterate from the piece going from the inside out in all directions available (4 for bishop), stopping when hitting a piece or an edge. If you find a piece that is the enemy's then include it as a possible move (for capture)
        // Append all these possible moves to the Collection of Chess Moves and return the Collection.

        // diag up - for-loop
        int[][] directions = {{1,1},{1,-1},{-1}}
        for (int i = 1; i < 10; i++) {
            int checkCol = currentCol + i;
            int checkRow = currentRow + i;
            ChessPosition checkPos = new ChessPosition(checkRow, checkCol);
            ChessPiece checkPiece = board.getPiece(checkPos);
            if (checkRow <= 0 | checkCol <= 0 | checkRow >= 9 | checkCol >= 9 | checkPiece.getTeamColor() == currentPiece.getTeamColor()){
                break;
        } else {
                moves.add(new ChessMove(myPosition,checkPos,null));
            }
        }

        for (int i = 1; i < 10; i++) {
            int checkCol = currentCol + -i;
            int checkRow = currentRow + i;
            ChessPosition checkPos = new ChessPosition(checkRow, checkCol);
            ChessPiece checkPiece = board.getPiece(checkPos);
            if (checkRow <= 0 | checkCol <= 0 | checkRow >= 9 | checkCol >= 9 | checkPiece.getTeamColor() == currentPiece.getTeamColor()){
                break;
            } else {
                moves.add(new ChessMove(myPosition,checkPos,null));
            }
        }

        for (int i = 1; i < 10; i++) {
            int checkCol = currentCol + i;
            int checkRow = currentRow + -i;
            ChessPosition checkPos = new ChessPosition(checkRow, checkCol);
            ChessPiece checkPiece = board.getPiece(checkPos);
            if (checkRow <= 0 | checkCol <= 0 | checkRow >= 9 | checkCol >= 9 | checkPiece.getTeamColor() == currentPiece.getTeamColor()){
                break;
            } else {
                moves.add(new ChessMove(myPosition,checkPos,null));
            }
        }

        for (int i = 1; i < 10; i++) {
            int checkCol = currentCol + -i;
            int checkRow = currentRow + -i;
            ChessPosition checkPos = new ChessPosition(checkRow, checkCol);
            ChessPiece checkPiece = board.getPiece(checkPos);
            if (checkRow <= 0 | checkCol <= 0 | checkRow >= 9 | checkCol >= 9 | checkPiece.getTeamColor() == currentPiece.getTeamColor()){
                break;
            } else {
                moves.add(new ChessMove(myPosition,checkPos,null));
            }
        }
        return moves;
    // Recursion base-case

//    public ChessMove bishopMove(ChessMove move) {
//        ChessPosition endPosition = move.getEndPosition();
//        ChessPosition newEndPosition = new ChessPosition(endPosition.getColumn() + 1, endPosition.getRow() + 1);
//        ChessMove newMove = new ChessMove(move.getStartPosition(),newEndPosition, null);
//        return bishopMove;
//    }
//
//    public Collection<ChessMove> recursiveMove(ChessPosition position, List<ChessMove> moveList) {
//        ChessPiece piece = board.getPiece(myPosition);
//        if (piece != null) {
//            if (piece.ChessGame.TeamColor.getTeamColor() == WHITE ) {
//                return;
//            }
//            else {
//                moveCollection.add(move);
//            }
//        }
//        return recursiveMove(function, move);
//    }


}
