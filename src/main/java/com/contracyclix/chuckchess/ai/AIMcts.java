package com.contracyclix.chuckchess.ai;

import com.contracyclix.chuckchess.mcts.Mcts;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class AIMcts implements AI {

    private Mcts<BoardStateAdapter, Move> mcts;

    @Value("${AI.MCTS.threads}")
    private int numThreads;

    @Value("${AI.MCTS.timePerActionSec}")
    private int timePerActionSec;

    @Value("${AI.MCTS.maxIterations}")
    private long maxIterations;

    @Override
    public Move getMove(BoardStateAdapter board, boolean isBotWhite) throws MoveGeneratorException {
        mcts.setRoot(null, (BoardStateAdapter) board);
        mcts.think();
        mcts.takeAction();
        return mcts.getLastAction();
    }

    @PostConstruct
    public void setupMCTS() {
        ExecutorService service = Executors.newFixedThreadPool(numThreads);
        mcts = new Mcts<>(service, numThreads, timePerActionSec, maxIterations);
    }
}
