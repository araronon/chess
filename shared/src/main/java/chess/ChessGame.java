package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currentTeamColor;
    private ChessBoard currentBoard;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeamColor;
    }
    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeamColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece checkPiece = currentBoard.getPiece(startPosition);
        ChessBoard checkBoard = currentBoard.clone();
        Collection<ChessMove> currentMovesCollection = checkPiece.pieceMoves(currentBoard, startPosition);
        for (ChessMove currentMove : currentMovesCollection) {
            ChessPosition currentEndPosition = currentMove.getEndPosition();
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition checkPos = new ChessPosition(row, col);
                ChessPiece checkPiece = currentBoard.getPiece(checkPos);
                if (checkPiece == null) {
                    continue;
                }
                if (checkPiece.getTeamColor() != teamColor) {
                    Collection<ChessMove> currentMovesCollection = checkPiece.pieceMoves(currentBoard, checkPos);
                    for (ChessMove currentMove : currentMovesCollection) {
                        ChessPosition currentEndPosition = currentMove.getEndPosition();
                        if (currentEndPosition == checkPos) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public ChessPosition findKing(ChessGame.TeamColor teamColor) {
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition checkPos = new ChessPosition(row, col);
                ChessPiece checkPiece = currentBoard.getPiece(checkPos);
                if ((checkPiece.getPieceType() == ChessPiece.PieceType.KING) && (checkPiece.getTeamColor() == teamColor)) {
                    return checkPos;
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        currentBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
    }
}
