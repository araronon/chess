package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition myPosition;
    private Collection<ChessMove> moves;
    private Collection<ChessMove> availableMoves;

    public PieceMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
        this.availableMoves = new ArrayList<>();
        this.moves = new ArrayList<>();
    }

    public Collection<ChessMove> calculateMoves() {
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

    public Collection<ChessMove> sliderMovements(int[][] direction, int maxSteps) {
        int ogRow = myPosition.getRow();
        int ogCol = myPosition.getColumn();
        ChessPiece ogPiece = board.getPiece(myPosition);
        for (int[] dirVec : direction) {
            for (int i = 1; i < maxSteps; i++) {
                int newRow = ogRow + i * dirVec[0];
                int newCol = ogCol + i * dirVec[1];
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                if (newRow <= 0 || newRow >= 9 || newCol <= 0 || newCol >= 9) {
                    break;
                }
                ChessPiece newPiece = board.getPiece(newPosition);
                if (newPiece == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                    continue;
                }
                if (newPiece.getTeamColor() != ogPiece.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                    break;
                }
                if (newPiece.getTeamColor() == ogPiece.getTeamColor()) {
                    break;
                }
            }
        }
        return moves;
    }
}

class BishopMovesCalculator extends PieceMovesCalculator {
    public BishopMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }
    public Collection<ChessMove> getMoves() {
        int[][] direction = {{1,1},{1,-1},{-1,1},{-1,-1}};
        int maxSteps = 8;
        return sliderMovements(direction, maxSteps);
    }
}

class KingMovesCalculator extends PieceMovesCalculator {
    public KingMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }
    public Collection<ChessMove> getMoves() {
        int[][] direction = {{1,1},{1,-1},{-1,1},{-1,-1},{1,0},{-1,0},{0,-1},{0,1}};
        int maxSteps = 2;
        return sliderMovements(direction, maxSteps);
    }
}

class QueenMovesCalculator extends PieceMovesCalculator {
    public QueenMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }
    public Collection<ChessMove> getMoves() {
        int[][] direction = {{1,1},{1,-1},{-1,1},{-1,-1},{1,0},{-1,0},{0,-1},{0,1}};
        int maxSteps = 8;
        return sliderMovements(direction, maxSteps);
    }
}

class RookMovesCalculator extends PieceMovesCalculator {
    public RookMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }
    public Collection<ChessMove> getMoves() {
        int[][] direction = {{1,0},{-1,0},{0,-1},{0,1}};
        int maxSteps = 8;
        return sliderMovements(direction, maxSteps);
    }
}

class KnightMovesCalculator extends PieceMovesCalculator {
    public KnightMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }
    public Collection<ChessMove> getMoves() {
        int[][] direction = {{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{-1,2},{1,-2},{-1,-2}};
        int maxSteps = 2;
        return sliderMovements(direction, maxSteps);
    }
}

class PawnMovesCalculator extends PieceMovesCalculator {
    private ChessBoard board;
    private ChessPosition myPosition;
    private Collection<ChessMove> moves;

    public PawnMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
        this.board = board;
        this.myPosition = myPosition;
        this.moves = new ArrayList<>();
    }

    public Collection<ChessMove> getMoves() {
        int ogRow = myPosition.getRow();
        int ogCol = myPosition.getColumn();
        ChessPiece ogPiece = board.getPiece(myPosition);
        int[][] direction = {{1,0},{1,1},{1,-1}};
        int promRow = 8;
        int startRow = 2;
        int maxSteps = 2;
        if (ogRow == startRow && (ogPiece.getTeamColor() == ChessGame.TeamColor.WHITE)) {
            maxSteps = 3;
        }
        if (ogPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            direction = new int[][]{{-1, 0}, {-1, 1}, {-1, -1}};
            promRow = 1;
            startRow = 7;
            if (ogRow == 7) {
                maxSteps = 3;
            }
        }
        for (int dirVec = 0; dirVec < direction.length; dirVec++) {
            if (dirVec == 0) {
                for (int i = 1; i < maxSteps; i++) {
                    int newRow = ogRow + i * direction[dirVec][0];
                    int newCol = ogCol + i * direction[dirVec][1];
                    ChessPosition newPosition = new ChessPosition(newRow, newCol);
                    if (newRow <= 0 || newRow >= 9 || newCol <= 0 || newCol >= 9) {
                        break;
                    }
                    ChessPiece newPiece = board.getPiece(newPosition);
                    if (newRow == promRow && (newPiece == null)) {
                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
                        break;
                    }
                    if (newPiece != null) {
                        break;
                    }
                    if (newPiece == null) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                        continue;
                    }
                }
            } else {
                int newRow = ogRow + direction[dirVec][0];
                int newCol = ogCol + direction[dirVec][1];
                ChessPosition newPosition = new ChessPosition(newRow, newCol);
                if (newRow <= 0 || newRow >= 9 || newCol <= 0 || newCol >= 9) {
                    continue;
                }
                ChessPiece newPiece = board.getPiece(newPosition);
                if (newPiece == null) {
                    continue;
                }
                if (newRow == promRow && (newPiece.getTeamColor() != ogPiece.getTeamColor())) {
                    moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
                    moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(myPosition, newPosition, ChessPiece.PieceType.BISHOP));
                    continue;
                }
                if (newPiece.getTeamColor() != ogPiece.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                    continue;
                }
                if (newPiece.getTeamColor() == ogPiece.getTeamColor()) {
                    continue;
                }
            }
        }
        return moves;
    }
}

