package service;

import chess.ChessGame;

public record MoveResult(String username, ChessGame game, String moveText, String statusText) {
}
