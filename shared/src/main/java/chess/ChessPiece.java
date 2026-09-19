package chess;

import java.util.ArrayList;
import java.util.Objects;

import java.util.Collection;

public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }


    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    public PieceType getPieceType() {
        return type;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (type) {
            case KNIGHT:
                return knightMoves(board, myPosition);
            case KING:
                return kingMoves(board, myPosition);
            case QUEEN:
                return queenMoves(board, myPosition);
            case ROOK:
                return rookMoves(board, myPosition);
            case BISHOP:
                return bishopMoves(board, myPosition);
            // case PAWN:
                // return pawnMoves(board, myPosition);
            default:
                throw new RuntimeException("Not implemented");

        }

    }

    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] offsets = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        for (int[] offset : offsets) {
            int newRow = myPosition.getRow() + offset[0];
            int newColumn = myPosition.getColumn() + offset[1];
            if (newRow >= 1 && newRow <= 8 && newColumn >= 1 && newColumn <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newColumn);
                ChessPiece occupyingPiece = board.getPiece(newPosition);
                if (occupyingPiece == null || occupyingPiece.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
        return moves;
    }
    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] offsets = {
                {0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };
        for (int[] offset : offsets) {
            int newRow = myPosition.getRow() + offset[0];
            int newColumn = myPosition.getColumn() + offset[1];
            if (newRow >= 1 && newRow <= 8 && newColumn >= 1 && newColumn <= 8) {
                ChessPosition newPosition = new ChessPosition(newRow, newColumn);
                ChessPiece occupyingPiece = board.getPiece(newPosition);
                if (occupyingPiece == null || occupyingPiece.getTeamColor() != this.pieceColor) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
        return moves;
    }
    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}

        };
        for (int[] direction : directions) {
            int newRow = myPosition.getRow();
            int newColumn = myPosition.getColumn();
            while (true) {
                newRow += direction[0];
                newColumn += direction[1];
                if (newRow < 1 || newRow > 8 || newColumn < 1 || newColumn > 8) {
                    break;
                }
                ChessPosition newPosition = new ChessPosition(newRow, newColumn);
                ChessPiece occupyingPiece = board.getPiece(newPosition);
                if (occupyingPiece == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                } else {
                    if (occupyingPiece.getTeamColor() != this.pieceColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }

            }
        }
        return moves;
    }
    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                {-1, 1}, {1, -1}, {1, 1}, {-1, -1}
        };
        for (int[] direction : directions) {
            int newRow = myPosition.getRow();
            int newColumn = myPosition.getColumn();
            while (true) {
                newRow += direction[0];
                newColumn += direction[1];
                if (newRow < 1 || newRow > 8 || newColumn < 1 || newColumn > 8) {
                    break;
                }
                ChessPosition newPosition = new ChessPosition(newRow, newColumn);
                ChessPiece occupyingPiece = board.getPiece(newPosition);
                if (occupyingPiece == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                } else {
                    if (occupyingPiece.getTeamColor() != this.pieceColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }

            }

        }
        return moves;
    }
    public Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition){
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                {-1, 1}, {1, -1}, {1, 1}, {-1, -1}, {1, 0}, {-1, 0}, {0, 1}, {0, -1}
        };
        for (int[] direction : directions){
            int newRow = myPosition.getRow();
            int newColumn = myPosition.getColumn();
            while (true) {
                newRow += direction[0];
                newColumn += direction[1];
                if (newRow < 1 || newRow > 8 || newColumn < 1 || newColumn > 8) {
                    break;
                }
                ChessPosition newPosition = new ChessPosition(newRow, newColumn);
                ChessPiece occupyingPiece = board.getPiece(newPosition);
                if (occupyingPiece == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                } else {
                    if (occupyingPiece.getTeamColor() != this.pieceColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }

            }

        }
        return moves;
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return Objects.equals(this.pieceColor, that.pieceColor)
                && Objects.equals(this.type, that.type);

    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
