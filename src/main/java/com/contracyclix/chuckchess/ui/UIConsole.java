package com.contracyclix.chuckchess.ui;

import com.contracyclix.chuckchess.ai.AI;
import com.contracyclix.chuckchess.ai.AIFactory;
import com.contracyclix.chuckchess.ai.BoardStateAdapter;
import com.contracyclix.chuckchess.ai.NoAIClassException;
import com.contracyclix.chuckchess.config.Config;
import com.contracyclix.chuckchess.ui.console.PrettyChessboardConsole;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UIConsole implements UI {

    @Getter @Setter
    private BoardStateAdapter board = new BoardStateAdapter();

    @Getter @Setter
    private AIFactory aiFactory = new AIFactory();

    @Getter @Setter
    private String white = Config.get().getString("White", "Human");

    @Getter @Setter
    private String black = Config.get().getString("Black", "Human");

    @Getter @Setter
    private String initialFen = Config.get().getString("Board.fen", "");

    @Getter @Setter
    private boolean printUTF8 = Config.get().getBoolean("UI.UIConsole.enableUTF8", true);

    private Map<Side, AI> bots = new HashMap<>();

    public UIConsole() throws NoAIClassException {
        AI white = aiFactory.getAI(this.white);
        AI black = aiFactory.getAI(this.black);

        if (white != null) {
            bots.put(Side.WHITE, white);
        }
        if (black != null) {
            bots.put(Side.BLACK, black);
        }

        if (initialFen != null && initialFen.length() > 0) {
            board.getBoard().loadFromFen(initialFen);
        }
    }

    @Override
    public void run() throws MoveGeneratorException {
        clearScreen();
        Scanner scanner = new Scanner(System.in);

        PrettyChessboardConsole pcc = new PrettyChessboardConsole(true);

        while (!board.isTerminal()) {
            if (printUTF8) {
                clearScreen();
                pcc.printBoard(board.getBoard());
            }
            Move move = null;

            Side currentSide = board.getBoard().getSideToMove();
            AI currentBot = bots.get(currentSide);
            if (currentBot == null) {
                String strMove = scanner.next();
                move = new Move(strMove, currentSide);
            } else {
                move = currentBot.getMove(board, currentSide == Side.WHITE);
                System.out.println(move);
            }

            Piece movedPiece = board.getBoard().getPiece(move.getFrom());
            if (movedPiece == Piece.NONE || movedPiece.getPieceSide() != currentSide && !board.getBoard().isMoveLegal(move, true)) {
                continue;
            }

            board.getBoard().doMove(move);
        }

        clearScreen();

        pcc.printBoard(board.getBoard());
        System.out.println("END GAME! " + board.isTerminal() + " " + board.getWinner() + " " + board.getBoard().getSideToMove() + " " + board.getRewardFor(1));
    }

    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
