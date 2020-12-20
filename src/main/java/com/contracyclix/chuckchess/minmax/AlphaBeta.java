package com.contracyclix.chuckchess.minmax;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.*;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@AllArgsConstructor
public class AlphaBeta {
    private final int maxDepth;
    private final boolean isWhite;
    private final int numThreads;


    private static boolean isTerminal(Board board) {
        return board.isDraw() || board.isStaleMate() || board.isInsufficientMaterial() || board.isMated() || board.isRepetition();
    }

    private static long getPieceValue(Piece p, Side playerSide) {
        long val = 0;
        if (p == null || p.value() == "NONE") {
            return 0;
        }
        switch (p.getPieceType()) {
            case BISHOP:
                val+=3;
                break;
            case KNIGHT:
                val+=3;
                break;
            case PAWN:
                val+=1;
                break;
            case QUEEN:
                val+=9;
                break;
            case ROOK:
                val+=5;
                break;
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

        if (board.isDraw() || board.isInsufficientMaterial() || board.isRepetition()) {
            return 0;
        }

        if (board.isStaleMate() || board.isMated()) {
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

        for (Square s : Square.values()) {
            if (s != Square.NONE) {
                if (board.squareAttackedBy(s, Side.BLACK) != 0) {
                    if (playerSide == Side.BLACK) {
                        score += 1;
                    } else {
                        score -= 1;
                    }
                }
                if (board.squareAttackedBy(s, Side.WHITE) != 0) {
                    if (playerSide == Side.WHITE) {
                        score += 1;
                    } else {
                        score -= 1;
                    }
                }
            }
        }

        return score;
    }

    public Move getBestMove(Board initial) throws MoveGeneratorException, MoveConversionException {
        List<Move> moves = MoveGenerator.generateLegalMoves(initial);

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
            boolean terminated = service.awaitTermination(60, TimeUnit.SECONDS);
            for (int i = 0 ; i < moves.size() ; i++) {
                if (scores[i] > bestScore) {
                    bestScore = scores[i];
                    bestMove = moves.get(i);
                }
            }
            //System.out.println("Current eval: " + evaluate(initial, 0));
            //System.out.println("Best move: " + bestMove.toString() + " " + bestScore);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return bestMove;
    }

    public long alphaBeta(Board node, int depth, long alpha, long beta, boolean isMax) throws MoveGeneratorException {
        if (depth >= maxDepth || isTerminal(node)) {
            return evaluate(node, depth);
        }

        List<Move> moves = MoveGenerator.generateLegalMoves(node);

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
        List<Move> moves = MoveGenerator.generateLegalMoves(node);

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
