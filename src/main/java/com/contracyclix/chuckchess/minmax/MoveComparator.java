package com.contracyclix.chuckchess.minmax;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.move.Move;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MoveComparator implements Comparator<Move> {
    Board board;

    public final Map<Piece, Integer> scores = new HashMap<>();

    public MoveComparator(Board board) {
        this.board = board;
        scores.put(Piece.BLACK_BISHOP, 3);
        scores.put(Piece.BLACK_KNIGHT, 3);
        scores.put(Piece.BLACK_PAWN, 1);
        scores.put(Piece.BLACK_QUEEN, 9);
        scores.put(Piece.BLACK_ROOK, 5);
        scores.put(Piece.WHITE_BISHOP, 3);
        scores.put(Piece.WHITE_KNIGHT, 3);
        scores.put(Piece.WHITE_PAWN, 1);
        scores.put(Piece.WHITE_QUEEN, 9);
        scores.put(Piece.WHITE_ROOK, 5);
        scores.put(Piece.NONE, 0);
        scores.put(Piece.BLACK_KING, 4);
        scores.put(Piece.WHITE_KING, 4);
    }

    @Override
    public int compare(Move o1, Move o2) {
        int score = 0;
        if (o1.getFrom() != null && board.getPiece(o1.getFrom()) != Piece.NONE) {
            score += scores.get(board.getPiece(o1.getFrom()));
        }
        if (o2.getFrom() != null && board.getPiece(o2.getFrom()) != Piece.NONE) {
            score -= scores.get(board.getPiece(o2.getFrom()));
        }
        if (o1.getTo() != null && board.getPiece(o1.getTo()) != Piece.NONE) {
            score += scores.get(board.getPiece(o1.getTo()));
        }
        if (o2.getTo() != null && board.getPiece(o2.getTo()) != Piece.NONE) {
            score -= scores.get(board.getPiece(o2.getTo()));
        }
        return score;
    }
}
