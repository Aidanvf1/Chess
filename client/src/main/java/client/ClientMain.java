package client;

import chess.*;
import ui.BoardRenderer;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        var board = new ChessBoard();
        board.resetBoard();
        var renderer = new BoardRenderer();
        System.out.print(renderer.render(board, ChessGame.TeamColor.WHITE));
        System.out.println();
        System.out.print(renderer.render(board, ChessGame.TeamColor.BLACK));
    }
}