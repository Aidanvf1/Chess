package chess;

public class ChessPosition {
    private final int row;
    private final int column;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.column = col;
    }

    public int getRow() {
        return row;

    }


    public int getColumn() {
        return column;
    }
}
