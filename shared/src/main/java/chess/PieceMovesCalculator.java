package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {

    private ChessBoard board;
    private ChessPosition myPosition;
    private Collection<ChessMove> availableMoves;

    public PieceMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
        this.availableMoves = new ArrayList<>();
    }

    Collection<ChessMove> MoveCalculation() {
        ChessPiece piece = board.getPiece(myPosition);
        switch(piece.getPieceType()) {
            case ChessPiece.PieceType.BISHOP:
                availableMoves = new BishopMovesCalculator(board, myPosition).getMoves();
                break;

        }
        return availableMoves;
    }

}

class BishopMovesCalculator extends PieceMovesCalculator {

    private Collection<ChessMove> moves;
    private ChessBoard board;
    private ChessPosition myPosition;

    public BishopMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super();
        this.board = board;
        this.myPosition = myPosition;
    }

    public Collection<ChessMove> getMoves() {
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        ChessPiece currentPiece = board.getPiece(myPosition);
        // Iterate from the piece going from the inside out in all directions available (4 for bishop), stopping when hitting a piece or an edge. If you find a piece that is the enemy's then include it as a possible move (for capture)
        // Append all these possible moves to the Collection of Chess Moves and return the Collection.
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        for (int dirVec = 0; dirVec < directions.length; dirVec++) {
            int dirRow = directions[dirVec][0];
            int dirCol = directions[dirVec][1];
            for (int i = 1; i < 10; i++) {
                int checkCol = currentCol + dirCol * i;
                int checkRow = currentRow + dirRow * i;
                ChessPosition checkPos = new ChessPosition(checkRow, checkCol);
                ChessPiece checkPiece = board.getPiece(checkPos);
                if (checkRow <= 0 || checkCol <= 0 || checkRow >= 9 || checkCol >= 9 || checkPiece.getTeamColor() == currentPiece.getTeamColor()) {
                    break;
                } else {
                    moves.add(new ChessMove(myPosition, checkPos, null));
                }
            }
        }
        return moves;
    }



}
