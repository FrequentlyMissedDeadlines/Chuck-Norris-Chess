package com.contracyclix.chuckchess.ai;

import com.contracyclix.chuckchess.minmax.AlphaBeta;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AIMinMax implements AI {

    @Value("${AI.MinMax.Depth}")
    private Integer maxDepth;

    @Value("${AI.MinMax.threads}")
    private int numThreads;

    @Override
    public Move getMove(BoardStateAdapter board, boolean isBotWhite) throws MoveGeneratorException {
        AlphaBeta alphaBeta = new AlphaBeta(maxDepth, isBotWhite, numThreads);
        try {
            return alphaBeta.getBestMove(board.getBoard());
        } catch (MoveConversionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
