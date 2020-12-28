package com.contracyclix.chuckchess.ai;

import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;

import java.util.List;

public class AIRandomMove implements AI {

    @Override
    public Move getMove(BoardStateAdapter board, boolean isBotWhite) throws MoveGeneratorException {
        List<Move> moveList = MoveGenerator.generateLegalMoves(board.getBoard());
        return moveList.get((int) (Math.random() * moveList.size()));
    }
}
