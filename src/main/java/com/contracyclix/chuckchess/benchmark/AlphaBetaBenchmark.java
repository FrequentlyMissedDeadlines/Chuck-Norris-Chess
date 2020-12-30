package com.contracyclix.chuckchess.benchmark;

import com.contracyclix.chuckchess.minmax.AlphaBeta;
import com.github.bhlangonijr.chesslib.Board;
import org.openjdk.jmh.annotations.Benchmark;

public class AlphaBetaBenchmark {

    @Benchmark
    public void alphaBeta() {
        AlphaBeta alphaBeta = new AlphaBeta(3, true, 3);

        alphaBeta.alphaBeta(new Board(), 0, Long.MIN_VALUE, Long.MAX_VALUE, true);
    }

    @Benchmark
    public void negaMax() {
        AlphaBeta alphaBeta = new AlphaBeta(3, true, 3);

        alphaBeta.negaMax(new Board(), Long.MIN_VALUE, 0);
    }
}
