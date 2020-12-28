package com.contracyclix.chuckchess.ui;

import com.contracyclix.chuckchess.ai.AI;
import com.contracyclix.chuckchess.ai.AIFactory;
import com.contracyclix.chuckchess.ai.BoardStateAdapter;
import com.contracyclix.chuckchess.ai.NoAIClassException;
import com.contracyclix.chuckchess.config.Config;
import com.frequentlymisseddeadlines.chessuci.GoParameters;
import com.frequentlymisseddeadlines.chessuci.UciListener;
import com.frequentlymisseddeadlines.chessuci.UciProtocol;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import lombok.Getter;
import lombok.Setter;

public class UciGui implements UciListener, UI {
    private BoardStateAdapter board = new BoardStateAdapter();

    @Getter @Setter
    private String algorithm = Config.get().getString("UCI.Algorithm", "MinMax");

    private AIFactory aiFactory = new AIFactory();

    @Override
    public String getEngineName() {
        return "Chuck Norris Chess";
    }

    @Override
    public String getAuthorName() {
        return "FrequenlyMissedDeadlines";
    }

    private Side playerTurn;

    @Override
    public void setPosition(String fen, String[] moves) {
        board.getBoard().loadFromFen(fen);
        playerTurn = Side.WHITE;
        for (String move : moves) {
            board.getBoard().doMove(new Move(move, playerTurn));
            playerTurn = playerTurn == Side.WHITE ? Side.BLACK : Side.WHITE;
        }
    }

    @Override
    public String go(GoParameters goParameters) {
        try {
            AI ai = aiFactory.getAI(algorithm);
            Move bestMove = ai.getMove(board, playerTurn == Side.WHITE);
            return bestMove.toString();
        } catch (NoAIClassException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void run() throws MoveGeneratorException {
        new UciProtocol(this);
        while (true) {
            try {
                Thread.sleep(10000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
