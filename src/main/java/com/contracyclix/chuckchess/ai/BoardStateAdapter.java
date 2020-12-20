package com.contracyclix.chuckchess.ai;

import com.contracyclix.chuckchess.mcts.State;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class BoardStateAdapter implements State<Move> {

    @Getter
    private Board board = new Board();

    @Override
    public boolean isTerminal() {
        return board.isDraw() || board.isStaleMate() || board.isInsufficientMaterial() || board.isMated() || board.isRepetition();
    }

    @Override
    public Move[] getAvailableActions() {
        try {
            List<Move> moves = MoveGenerator.generateLegalMoves(board);
            return moves.toArray(new Move[moves.size()]);
        } catch (MoveGeneratorException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getPreviousAgent() {
        return board.getSideToMove() == Side.WHITE ? 2 : 1;
    }

    @Override
    public double getRewardFor(int agent) {
        if (board.isDraw() || board.isStaleMate() || board.isInsufficientMaterial() || board.isRepetition())
            return 0.5;
        if ((board.getSideToMove() == Side.WHITE && agent == 1) || (board.getSideToMove() == Side.BLACK && agent == 2))
            return 0.0;

        return 1.0;
    }

    @Override
    public State takeAction(Move action) {
        State copy = copy();
        copy.applyAction(action);
        return copy;
    }

    @Override
    public State copy() {
        BoardStateAdapter copy = new BoardStateAdapter();
        copy.board = board.clone();
        return copy;
    }

    @Override
    public void applyAction(Move action) {
        board.doMove(action);
    }

    @Override
    public int getWinner() {
        if (board.isDraw()) {
            return 0;
        }

        if (board.getSideToMove() == Side.WHITE) {
            return 2;
        }

        return 1;
    }

    public String toString() {
        String[][] unicode = new String[9][9];

        for (int i = 0 ; i < 9 ; i++) {
            for (int j = 0 ; j < 9 ; j++) {
                if (i > 0 && j > 0) {
                    String cell = ((char) ('A' + i - 1)) + "" + (9 - i);
                    switch (board.getPiece(Square.fromValue(cell))) {
                        case NONE:
                            unicode[i][j] = " ";
                            break;
                        case BLACK_BISHOP:
                            unicode[i][j] = "\u265d";
                            break;
                        case BLACK_KING:
                            unicode[i][j] = "\u265a";
                            break;
                        case BLACK_KNIGHT:
                            unicode[i][j] = "\u265e";
                            break;
                        case BLACK_PAWN:
                            unicode[i][j] = "\u265f";
                            break;
                        case BLACK_QUEEN:
                            unicode[i][j] = "\u265b";
                            break;
                        case BLACK_ROOK:
                            unicode[i][j] = "\u265c";
                            break;
                        case WHITE_BISHOP:
                            unicode[i][j] = "\u2657";
                            break;
                        case WHITE_KING:
                            unicode[i][j] = "\u2654";
                            break;
                        case WHITE_KNIGHT:
                            unicode[i][j] = "\u2658";
                            break;
                        case WHITE_PAWN:
                            unicode[i][j] = "\u2659";
                            break;
                        case WHITE_QUEEN:
                            unicode[i][j] = "\u2655";
                            break;
                        case WHITE_ROOK:
                            unicode[i][j] = "\u2656";
                            break;
                    }
                } else {
                    unicode[i][j] = " ";
                }
            }
        }

        for (int i = 1 ; i <= 8 ; i++) {
            unicode[0][i] = "" + (char) ('a' + i - 1);
            unicode[i][0] = "" + (9 - i);
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0 ; i < 9 ; i++) {
            for (int j = 0 ; j < 9 ; j++) {
                sb.append(unicode[i][j]);
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
