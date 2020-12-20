package com.contracyclix.chuckchess.ai;

import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AIRandomMove implements AI {

    @Override
    public Move getMove(BoardStateAdapter board, boolean isBotWhite) throws MoveGeneratorException {
        List<Move> moveList = MoveGenerator.generateLegalMoves(board.getBoard());
        return moveList.get((int) (Math.random() * moveList.size()));
    }
}
