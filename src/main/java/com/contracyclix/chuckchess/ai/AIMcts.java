package com.contracyclix.chuckchess.ai;

import com.contracyclix.chuckchess.config.Config;
import com.contracyclix.chuckchess.mcts.Mcts;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AIMcts implements AI {

    private Mcts<BoardStateAdapter, Move> mcts;

    @Getter @Setter
    private int numThreads = Config.get().getInt("AI.MCTS.threads");

    @Getter @Setter
    private int timePerActionSec = Config.get().getInt("AI.MCTS.timePerActionSec");

    @Getter @Setter
    private long maxIterations = Config.get().getLong("AI.MCTS.maxIterations");

    @Override
    public Move getMove(BoardStateAdapter board, boolean isBotWhite) throws MoveGeneratorException {
        mcts.setRoot(null, (BoardStateAdapter) board);
        mcts.think();
        mcts.takeAction();
        return mcts.getLastAction();
    }

    public AIMcts() {
        ExecutorService service = Executors.newFixedThreadPool(numThreads);
        mcts = new Mcts<>(service, numThreads, timePerActionSec, maxIterations);
    }
}
