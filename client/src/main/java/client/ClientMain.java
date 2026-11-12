package client;

import chess.*;
import ui.BoardRenderer;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var board = new ChessBoard();
        board.resetBoard();
        System.out.print(new BoardRenderer().render(board));
    }
}