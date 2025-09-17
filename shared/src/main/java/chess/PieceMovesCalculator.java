package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {

    private ChessBoard board;
    private ChessPosition myPosition;
    private Collection<ChessMove> availableMoves;
    private Collection<ChessMove> moves;

    public PieceMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
        this.availableMoves = new ArrayList<>();
        this.moves = new ArrayList<>();
    }

    Collection<ChessMove> moveCalculation() {
        ChessPiece piece = board.getPiece(myPosition);
        switch(piece.getPieceType()) {
            case ChessPiece.PieceType.BISHOP:
                availableMoves = new BishopMovesCalculator(board, myPosition).getMoves();
                break;
            case ChessPiece.PieceType.ROOK:
                availableMoves = new RookMovesCalculator(board, myPosition).getMoves();
                break;
            case ChessPiece.PieceType.KING:
                availableMoves = new KingMovesCalculator(board, myPosition).getMoves();
                break;
            case ChessPiece.PieceType.QUEEN:
                availableMoves = new QueenMovesCalculator(board, myPosition).getMoves();
                break;
            case ChessPiece.PieceType.KNIGHT:
                availableMoves = new KnightMovesCalculator(board, myPosition).getMoves();
                break;
            case ChessPiece.PieceType.PAWN:
                availableMoves = new PawnMovesCalculator(board, myPosition).getMoves();
                break;
        }
        return availableMoves;
    }

    Collection<ChessMove> sliderMovement(int[][] direction, int maxSteps) {
        ChessPiece piece = board.getPiece(myPosition);
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        for (var dirVec : direction) {
            for (int i = 1; i < maxSteps; i++) {
                int newRow = currentRow + i * dirVec[0];
                int newCol = currentCol + i * dirVec[1];
                if (newRow <= 0 || newRow >= 9 || newCol <= 0 || newCol >= 9) {
                    break;
                }
                ChessPosition newPosition = new ChessPosition(newRow,newCol);
                ChessPiece newPiece = board.getPiece(newPosition);
                if (newPiece == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                } else if (newPiece.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                    break;
                } else if (newPiece.getTeamColor() == piece.getTeamColor()){
                    break;
                }
            }
        }
        return moves;
    }

}

class BishopMovesCalculator extends PieceMovesCalculator {

    private Collection<ChessMove> moves;
    private ChessBoard board;
    private ChessPosition myPosition;

    public BishopMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
        this.moves = new ArrayList<>();
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
                if (checkRow <= 0 || checkCol <= 0 || checkRow >= 9 || checkCol >= 9 ) {
                    break;
                }
                ChessPiece checkPiece = board.getPiece(checkPos);
                if (checkPiece == null) {
                    moves.add(new ChessMove(myPosition, checkPos, null));
                } else if (checkPiece.getTeamColor() != currentPiece.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, checkPos, null));
                    break;
                } else {
                    break;
                }
            }
        }
        return moves;
    }
}

class RookMovesCalculator extends PieceMovesCalculator {

    private Collection<ChessMove> moves;
    private ChessBoard board;
    private ChessPosition myPosition;

    public RookMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
        this.moves = new ArrayList<>();
        this.board = board;
        this.myPosition = myPosition;
    }

    public Collection<ChessMove> getMoves() {
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        ChessPiece currentPiece = board.getPiece(myPosition);
        // Iterate from the piece going from the inside out in all directions available (4 for bishop), stopping when hitting a piece or an edge. If you find a piece that is the enemy's then include it as a possible move (for capture)
        // Append all these possible moves to the Collection of Chess Moves and return the Collection.
        int[][] directions = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        for (int dirVec = 0; dirVec < directions.length; dirVec++) {
            int dirRow = directions[dirVec][0];
            int dirCol = directions[dirVec][1];
            for (int i = 1; i < 10; i++) {
                int checkCol = currentCol + dirCol * i;
                int checkRow = currentRow + dirRow * i;
                ChessPosition checkPos = new ChessPosition(checkRow, checkCol);
                if (checkRow <= 0 || checkCol <= 0 || checkRow >= 9 || checkCol >= 9 ) {
                    break;
                }
                ChessPiece checkPiece = board.getPiece(checkPos);
                if (checkPiece == null) {
                    moves.add(new ChessMove(myPosition, checkPos, null));
                } else if (checkPiece.getTeamColor() != currentPiece.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, checkPos, null));
                    break;
                } else {
                    break;
                }
            }
        }
        return moves;
    }
}

class KingMovesCalculator extends PieceMovesCalculator {

    private Collection<ChessMove> moves;
    private ChessBoard board;
    private ChessPosition myPosition;

    public KingMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
        this.moves = new ArrayList<>();
        this.board = board;
        this.myPosition = myPosition;
    }

    public Collection<ChessMove> getMoves() {
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        ChessPiece currentPiece = board.getPiece(myPosition);
        // Iterate from the piece going from the inside out in all directions available (4 for bishop), stopping when hitting a piece or an edge. If you find a piece that is the enemy's then include it as a possible move (for capture)
        // Append all these possible moves to the Collection of Chess Moves and return the Collection.
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        for (int dirVec = 0; dirVec < directions.length; dirVec++) {
            int dirRow = directions[dirVec][0];
            int dirCol = directions[dirVec][1];
            for (int i = 1; i < 2; i++) {
                int checkCol = currentCol + dirCol * i;
                int checkRow = currentRow + dirRow * i;
                ChessPosition checkPos = new ChessPosition(checkRow, checkCol);
                if (checkRow <= 0 || checkCol <= 0 || checkRow >= 9 || checkCol >= 9 ) {
                    break;
                }
                ChessPiece checkPiece = board.getPiece(checkPos);
                if (checkPiece == null) {
                    moves.add(new ChessMove(myPosition, checkPos, null));
                } else if (checkPiece.getTeamColor() != currentPiece.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, checkPos, null));
                    break;
                } else {
                    break;
                }
            }
        }
        return moves;
    }
}

class QueenMovesCalculator extends PieceMovesCalculator {

    private Collection<ChessMove> moves;
    private ChessBoard board;
    private ChessPosition myPosition;

    public QueenMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
        this.moves = new ArrayList<>();
        this.board = board;
        this.myPosition = myPosition;
    }

    public Collection<ChessMove> getMoves() {
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        ChessPiece currentPiece = board.getPiece(myPosition);
        // Iterate from the piece going from the inside out in all directions available (4 for bishop), stopping when hitting a piece or an edge. If you find a piece that is the enemy's then include it as a possible move (for capture)
        // Append all these possible moves to the Collection of Chess Moves and return the Collection.
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        for (int dirVec = 0; dirVec < directions.length; dirVec++) {
            int dirRow = directions[dirVec][0];
            int dirCol = directions[dirVec][1];
            for (int i = 1; i < 10; i++) {
                int checkCol = currentCol + dirCol * i;
                int checkRow = currentRow + dirRow * i;
                ChessPosition checkPos = new ChessPosition(checkRow, checkCol);
                if (checkRow <= 0 || checkCol <= 0 || checkRow >= 9 || checkCol >= 9 ) {
                    break;
                }
                ChessPiece checkPiece = board.getPiece(checkPos);
                if (checkPiece == null) {
                    moves.add(new ChessMove(myPosition, checkPos, null));
                } else if (checkPiece.getTeamColor() != currentPiece.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, checkPos, null));
                    break;
                } else {
                    break;
                }
            }
        }
        return moves;
    }
}

class PawnMovesCalculator extends PieceMovesCalculator {

    private Collection<ChessMove> moves;
    private ChessBoard board;
    private ChessPosition myPosition;

    public PawnMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
        this.moves = new ArrayList<>();
        this.board = board;
        this.myPosition = myPosition;
    }

    public Collection<ChessMove> getMoves() {
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        ChessPiece currentPiece = board.getPiece(myPosition);
        int[][] directions = {{1, 0}, {1, 1}, {1, -1}};
        int endBoard = 8;
        int startPawn = 2;
        if (currentPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            directions = new int[][]{{-1, 0}, {-1, 1}, {-1, -1}};
            endBoard = 1;
            startPawn = 7;
        }
        for (int dirVec = 0; dirVec < directions.length; dirVec++) {
            int dirRow = directions[dirVec][0];
            int dirCol = directions[dirVec][1];
            if (dirVec == 0) {
                int moveTwo = 2;
                if ((currentRow == startPawn)) {
                    moveTwo = 3;
                }
                for (int i = 1; i < moveTwo; i++) {
                    int checkCol = currentCol + dirCol * i;
                    int checkRow = currentRow + dirRow * i;
                    ChessPosition checkPos = new ChessPosition(checkRow, checkCol);
                    ChessPiece checkPiece = board.getPiece(checkPos);
                    if (checkRow == endBoard && checkPiece == null) {
                        moves.add(new ChessMove(myPosition, checkPos, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, checkPos, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, checkPos, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, checkPos, ChessPiece.PieceType.KNIGHT));
                        break;
                    }
                    if (checkRow <= 0 || checkCol <= 0 || checkRow >= 9 || checkCol >= 9) {
                        break;
                    }
                    if (checkPiece == null) {
                        moves.add(new ChessMove(myPosition, checkPos, null));
                    } else {
                        break;
                    }
                }
            } else {
                for (int i = 1; i < 2; i++) {
                    int checkCol = currentCol + dirCol * i;
                    int checkRow = currentRow + dirRow * i;
                    ChessPosition checkPos = new ChessPosition(checkRow, checkCol);
                    if (checkRow <= 0 || checkCol <= 0 || checkRow >= 9 || checkCol >= 9) {
                        break;
                    }
                    ChessPiece checkPiece = board.getPiece(checkPos);
                    if (checkPiece == null) {
                        break;
                    }
                    if (checkRow == endBoard && checkPiece.getTeamColor() != currentPiece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, checkPos, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, checkPos, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, checkPos, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(myPosition, checkPos, ChessPiece.PieceType.KNIGHT));
                        break;
                    }
                    if (checkPiece.getTeamColor() != currentPiece.getTeamColor()) {
                        moves.add(new ChessMove(myPosition, checkPos, null));
                        break;
                    } else {
                        break;
                    }
                }
            }
        }
        return moves;
    }
}

class KnightMovesCalculator extends PieceMovesCalculator {

    private Collection<ChessMove> moves;
    private ChessBoard board;
    private ChessPosition myPosition;

    public KnightMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
        this.moves = new ArrayList<>();
        this.board = board;
        this.myPosition = myPosition;
    }

    public Collection<ChessMove> getMoves() {
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        ChessPiece currentPiece = board.getPiece(myPosition);
        // Iterate from the piece going from the inside out in all directions available (4 for bishop), stopping when hitting a piece or an edge. If you find a piece that is the enemy's then include it as a possible move (for capture)
        // Append all these possible moves to the Collection of Chess Moves and return the Collection.
        // } else if (checkPiece.getTeamColor() != currentPiece.getTeamColor()) {
        //                    moves.add(new ChessMove(myPosition, checkPos, null));
        int[][] directions = {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{-1,2},{1,-2},{-1,-2}};
        for (int dirVec = 0; dirVec < directions.length; dirVec++) {
            int dirRow = directions[dirVec][0];
            int dirCol = directions[dirVec][1];
            int checkRow = dirRow + currentRow;
            int checkCol = dirCol + currentCol;
            ChessPosition checkPos = new ChessPosition(checkRow, checkCol);
            if (checkRow <= 0 || checkCol <= 0 || checkRow >= 9 || checkCol >= 9 )
                continue;
            ChessPiece checkPiece = board.getPiece(checkPos);
            if (checkPiece == null) {
                moves.add(new ChessMove(myPosition, checkPos, null));
                continue;
            } else if (checkPiece.getTeamColor() != currentPiece.getTeamColor()) {
                moves.add(new ChessMove(myPosition, checkPos, null));
                continue;
            } else {
                continue;
            }
        }
        return moves;
    }
}

