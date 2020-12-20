package com.contracyclix.chuckchess.ui.console;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;

public class PrettyChessboardConsole {
    public static final String ANSI_LIGHT_BACKGROUND = "\u001B[47m";
    public static final String ANSI_DARK_BACKGROUND = "\033[43m";
    public static final String ANSI_RESET = "\u001B[0m";

    private final boolean isWhiteSide;

    public PrettyChessboardConsole(boolean isWhiteSide) {
        this.isWhiteSide = isWhiteSide;
    }

    public void printBoard(Board board) {
        StringBuilder sb = new StringBuilder(" a b c d e f g h\n");

        for (int i = 0 ; i < 8 ; i++) {
            sb.append(8-i);
            for (int j = 0 ; j < 8 ; j++) {
                Piece p = board.getPiece(Square.fromValue((char)('A'+j) + "" + (8-i)));
                if ((i + j) % 2 == 0) {
                    sb.append(ANSI_LIGHT_BACKGROUND);
                } else {
                    sb.append(ANSI_DARK_BACKGROUND);
                }
                switch (p) {
                    case BLACK_BISHOP:
                        sb.append("♝");
                        break;
                    case BLACK_KING:
                        sb.append("♚");
                        break;
                    case BLACK_KNIGHT:
                        sb.append("♞");
                        break;
                    case BLACK_PAWN:
                        sb.append("♟");
                        break;
                    case BLACK_QUEEN:
                        sb.append("♛");
                        break;
                    case BLACK_ROOK:
                        sb.append("♜");
                        break;
                    case WHITE_BISHOP:
                        sb.append("♗");
                        break;
                    case WHITE_KING:
                        sb.append("♔");
                        break;
                    case WHITE_KNIGHT:
                        sb.append("♘");
                        break;
                    case WHITE_PAWN:
                        sb.append("♙");
                        break;
                    case WHITE_QUEEN:
                        sb.append("♕");
                        break;
                    case WHITE_ROOK:
                        sb.append("♖");
                        break;
                    case NONE:
                        sb.append("\u2003");
                        break;
                }
                sb.append(ANSI_RESET);
            }
            sb.append("\n");
        }

        System.out.println(sb);
    }
}
