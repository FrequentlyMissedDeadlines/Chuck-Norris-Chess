package com.contracyclix.chuckchess.minmax;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import lombok.AllArgsConstructor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@AllArgsConstructor
public class AlphaBeta {
    private final int maxDepth;
    private final boolean isWhite;
    private final int numThreads;

    private static final Map<Piece, Integer> piecesScores = new MoveComparator(null).scores;

    private final static List<Square> allSquares = new LinkedList<>();
    static {
        for (Square s : Square.values()) {
            if (s != Square.NONE) {
                allSquares.add(s);
            }
        }
    }

    private static boolean isTerminal(Board board) {
        return board.isDraw() || board.isMated();
    }

    private static long getPieceValue(Piece p, Side playerSide) {
        long val = 0;

        if (piecesScores.containsKey(p)) {
            val += piecesScores.get(p);
        }

        if (p.getPieceSide() == playerSide) {
            return val;
        } else {
            return -val;
        }
    }

    private long evaluate(Board board, int currentDepth) {
        Piece[] pieces = board.boardToArray();
        Side playerSide = isWhite ? Side.WHITE : Side.BLACK;

        if (board.isDraw()) {
            return 0;
        }

        if (board.isMated()) {
            if (playerSide == board.getSideToMove()) {
                return currentDepth - 1000000;
            } else {
                return 1000000 - currentDepth;
            }
        }

        long score = 0;
        for (Piece p : pieces) {
            score += getPieceValue(p, playerSide);
        }

        score *= 100;

        //List<Move> allMoves = board.pseudoLegalMoves();
        //score += 2 * allMoves.stream().filter(m -> board.getPiece(m.getFrom()).getPieceSide() == playerSide).count() - allMoves.size();

        for (Square s : allSquares) {
            if (squareAttackedBy(s, Side.BLACK, board)) {
                if (playerSide == Side.BLACK) {
                    score += 1;
                } else {
                    score -= 1;
                }
            }
            if (squareAttackedBy(s, Side.WHITE, board)) {
                if (playerSide == Side.WHITE) {
                    score += 1;
                } else {
                    score -= 1;
                }
            }
        }

        return score;
    }

    private boolean squareAttackedBy(Square square, Side side, Board board) {
        long occ = board.getBitboard();
        return (Bitboard.getPawnAttacks(side.flip(), square) & board.getBitboard(Piece.make(side, PieceType.PAWN)) & occ) != 0 ||
                (Bitboard.getKnightAttacks(square, occ) & board.getBitboard(Piece.make(side, PieceType.KNIGHT))) != 0 ||
                (Bitboard.getBishopAttacks(occ, square) & (board.getBitboard(Piece.make(side, PieceType.BISHOP)) | board.getBitboard(Piece.make(side, PieceType.QUEEN)))) != 0 ||
                (Bitboard.getRookAttacks(occ, square) & (board.getBitboard(Piece.make(side, PieceType.ROOK)) | board.getBitboard(Piece.make(side, PieceType.QUEEN)))) != 0 ||
                (Bitboard.getKingAttacks(square, occ) & board.getBitboard(Piece.make(side, PieceType.KING))) != 0;
    }

    public Move getBestMove(Board initial) throws MoveConversionException {
        List<Move> moves = initial.legalMoves();

        ExecutorService service = Executors.newFixedThreadPool(numThreads);
        long[] scores = new long[moves.size()];
        IntStream.range(0, moves.size()).forEach(i -> {
            service.submit(() -> {
                Board child = initial.clone();
                child.doMove(moves.get(i));
                long score = 0;
                try {
                    score = alphaBeta(child, 0 , Long.MIN_VALUE, Long.MAX_VALUE, false);
                } catch (MoveGeneratorException e) {
                    e.printStackTrace();
                }
                scores[i] = score;
                return;
            });
        });

        long bestScore = Long.MIN_VALUE;
        Move bestMove = null;
        service.shutdown();
        try {
            boolean terminated = service.awaitTermination(600, TimeUnit.SECONDS);
            for (int i = 0 ; i < moves.size() ; i++) {
                if (scores[i] > bestScore) {
                    bestScore = scores[i];
                    bestMove = moves.get(i);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return bestMove;
    }

    public long alphaBeta(Board node, int depth, long alpha, long beta, boolean isMax) throws MoveGeneratorException {
        if (depth >= maxDepth || isTerminal(node)) {
            return evaluate(node, depth);
        }

        List<Move> moves = node.legalMoves();

        if (isMax) {
            long score = Long.MIN_VALUE;
            for (Move move : moves) {
                Board child = node.clone();
                child.doMove(move);
                score = Math.max(score, alphaBeta(child,depth + 1, alpha, beta, !isMax));
                alpha = Math.max(alpha, score);
                if (alpha >= beta) {
                    break;
                }
            }
            return score;
        } else {
            long score = Long.MAX_VALUE;
            for (Move move : moves) {
                Board child = node.clone();
                child.doMove(move);
                score = Math.min(score, alphaBeta(child,depth + 1, alpha, beta, !isMax));
                beta = Math.min(beta, score);
                if (alpha >= beta) {
                    break;
                }
            }
            return score;
        }
    }

    public long negaMax(Board node, long minScore, int depth) throws MoveGeneratorException {
        if (depth >= maxDepth || isTerminal(node)) {
            return evaluate(node, depth);
        }
        long score = Long.MIN_VALUE;
        List<Move> moves = node.legalMoves().stream().sorted(new MoveComparator(node)).collect(Collectors.toList());

        for (Move move : moves) {
            Board child = node.clone();
            child.doMove(move);
            score = Math.max(score, negaMax(child, score, depth + 1));
            if (-score <= minScore) {
                return -score;
            }
        }
        return -score;
    }
}
