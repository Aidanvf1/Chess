package service;

import chess.ChessGame;

public record ConnectResult(String username, String role, ChessGame game) {
}