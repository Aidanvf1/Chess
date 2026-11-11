package ui;

import static ui.EscapeSequences.*;

public class BoardRenderer {
    private static final int BOARD_SIZE = 8;
    private static final String COLUMN_LETTERS = "abcdefgh";
    private static final String EM_SPACE = "\u2003";
    private static final String BORDER_STYLE = SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE;
    private static final String RESET_STYLE = RESET_BG_COLOR + RESET_TEXT_COLOR;

    public String render() {
        StringBuilder output = new StringBuilder();
        appendColumnLabels(output);
        for (int row = BOARD_SIZE; row >= 1; row--) {
            appendRow(output, row);
        }
        appendColumnLabels(output);
        return output.toString();
    }

    private void appendColumnLabels(StringBuilder output) {
        appendBorderCell(output, " ");
        for (int column = 0; column < BOARD_SIZE; column++) {
            appendBorderCell(output, String.valueOf(COLUMN_LETTERS.charAt(column)));
        }
        appendBorderCell(output, " ");
        output.append(RESET_STYLE).append("\n");
    }

    private void appendRow(StringBuilder output, int row) {
        appendBorderCell(output, String.valueOf(row));
        for (int column = 1; column <= BOARD_SIZE; column++) {
            appendSquare(output, row, column);
        }
        appendBorderCell(output, String.valueOf(row));
        output.append(RESET_STYLE).append("\n");
    }

    private void appendBorderCell(StringBuilder output, String label) {
        output.append(BORDER_STYLE).append(" ").append(label).append(EM_SPACE);
    }

    private void appendSquare(StringBuilder output, int row, int column) {
        output.append(isLightSquare(row, column) ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY);
        output.append(EMPTY);
    }

    private boolean isLightSquare(int row, int column) {
        return (row + column) % 2 == 1;
    }
}