package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamTurn = TeamColor.WHITE;

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> candidateMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> legalMoves = new ArrayList<>();
        for (ChessMove move : candidateMoves) {
            if (!wouldBeInCheck(move, piece.getTeamColor())) {
                legalMoves.add(move);
            }
        }
        return legalMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("No piece at start position");
        }
        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Not that team's turn");
        }
        Collection<ChessMove> legalMoves = validMoves(move.getStartPosition());
        if (legalMoves == null || !legalMoves.contains(move)) {
            throw new InvalidMoveException("Move is not legal");
        }

        ChessPiece pieceToPlace = piece;
        if (move.getPromotionPiece() != null) {
            pieceToPlace = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }

        board.addPiece(move.getStartPosition(), null);
        board.addPiece(move.getEndPosition(), pieceToPlace);

        teamTurn = (teamTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    private ChessPosition findKing(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null
                        && piece.getPieceType() == ChessPiece.PieceType.KING
                        && piece.getTeamColor() == teamColor) {
                    return position;
                }
            }
        }
        return null;
    }

    private Collection<ChessPosition> findPieces(TeamColor teamColor) {
        Collection<ChessPosition> positions = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    positions.add(position);
                }
            }
        }
        return positions;
    }


    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);
        if (kingPosition == null) {
            return false;
        }
        TeamColor opponent = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
        for (ChessPosition opponentPosition : findPieces(opponent)) {
            ChessPiece opponentPiece = board.getPiece(opponentPosition);
            Collection<ChessMove> moves = opponentPiece.pieceMoves(board, opponentPosition);
            for (ChessMove move : moves) {
                if (move.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean wouldBeInCheck(ChessMove move, TeamColor teamColor) {
        ChessBoard simulatedBoard = new ChessBoard(board);
        ChessPiece movingPiece = simulatedBoard.getPiece(move.getStartPosition());
        simulatedBoard.addPiece(move.getStartPosition(), null);
        simulatedBoard.addPiece(move.getEndPosition(), movingPiece);

        ChessBoard realBoard = board;
        board = simulatedBoard;
        boolean inCheck = isInCheck(teamColor);
        board = realBoard;

        return inCheck;
    }

    private boolean hasNoValidMoves(TeamColor teamColor) {
        for (ChessPosition position : findPieces(teamColor)) {
            Collection<ChessMove> moves = validMoves(position);
            if (moves != null && !moves.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && hasNoValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && hasNoValidMoves(teamColor);

    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame that = (ChessGame) o;
        return Objects.equals(this.board, that.board)
                && this.teamTurn == that.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}
