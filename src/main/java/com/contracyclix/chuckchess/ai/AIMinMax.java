package com.contracyclix.chuckchess.ai;

import com.contracyclix.chuckchess.config.Config;
import com.contracyclix.chuckchess.minmax.AlphaBeta;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import lombok.Getter;
import lombok.Setter;

public class AIMinMax implements AI {
    @Getter @Setter
    private int maxDepth = Config.get().getInt("AI.MinMax.Depth");

    @Getter @Setter
    private int numThreads = Config.get().getInt("AI.MinMax.threads");

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
