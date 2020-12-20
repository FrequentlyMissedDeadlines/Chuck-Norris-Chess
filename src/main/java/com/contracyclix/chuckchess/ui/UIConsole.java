package com.contracyclix.chuckchess.ui;

import com.contracyclix.chuckchess.ai.AI;
import com.contracyclix.chuckchess.ai.AIFactory;
import com.contracyclix.chuckchess.ai.BoardStateAdapter;
import com.contracyclix.chuckchess.ai.NoAIClassException;
import com.contracyclix.chuckchess.ui.console.PrettyChessboardConsole;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Service
public class UIConsole implements UI {

    @Getter @Setter
    private BoardStateAdapter board = new BoardStateAdapter();

    @Autowired
    private AIFactory aiFactory;

    @Value("${White}")
    private String white = "Human";

    @Value("${Black}")
    private String black = "Human";

    @Value("${Board.fen:}")
    private String initialFen;

    @Value("${UI.UIConsole.enableUTF8:true}")
    private boolean printUTF8;

    private Map<Side, AI> bots = new HashMap<>();

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

    @PostConstruct
    public void postConstruct() throws NoAIClassException {
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

    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
