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
                availableMoves = new BishopMovesCalculator(board, myPosition);
                break;


        }
        if (piece.getPieceType() == ChessPiece.PieceType.BISHOP)
    }



}

class BishopMovesCalculator extends PieceMovesCalculator {

    public BishopMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    int currentrow = myPosition.getRow();
    int currentcol = myPosition.getColumn();
    // Iterate from the piece going from the inside out in all directions available (4 for bishop), stopping when hitting a piece or an edge. If you find a piece that is the enemy's then include it as a possible move (for capture)
    // Append all these possible moves to the Collection of Chess Moves and return the Collection.

    // diag up
    for (int i = 1; i < 10; i++) {
        int checkcol = currentcol + i;
        int checkrow = currentrow + i;
        if ()
    }
}
