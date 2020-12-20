package com.contracyclix.chuckchess.ai;

import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;

public interface AI {
    public Move getMove(BoardStateAdapter board, boolean isBotWhite) throws MoveGeneratorException;
}
