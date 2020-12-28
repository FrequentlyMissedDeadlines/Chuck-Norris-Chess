package libs;


import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.move.Move;
import org.junit.Test;

import static org.junit.Assert.*;

public class BoardTest {

    @Test
    public void repeatedPositionShouldBeDraw() {
        Board initial = new Board();
        initial.loadFromFen("8/1p2r1k1/3p2pp/r4p2/PR1P1P1P/3K4/8/8 w - - 0 1");

        Board child = initial.clone();
        child.doMove(new Move("d3d2", Side.WHITE));

        assertFalse(child.isDraw());
        assertFalse(child.isRepetition());

        child = child.clone();
        child.doMove(new Move("a5d5", Side.BLACK));

        assertFalse(child.isDraw());
        assertFalse(child.isRepetition());

        child = child.clone();
        child.doMove(new Move("d2d3", Side.WHITE));

        assertFalse(child.isDraw());
        assertFalse(child.isRepetition());

        child = child.clone();
        child.doMove(new Move("d5a5", Side.BLACK));

        assertFalse(child.isDraw());
        assertFalse(child.isRepetition());

        child = child.clone();
        child.doMove(new Move("d3d2", Side.WHITE));

        assertFalse(child.isDraw());
        assertFalse(child.isRepetition());

        child = child.clone();
        child.doMove(new Move("a5d5", Side.BLACK));

        assertFalse(child.isDraw());
        assertFalse(child.isRepetition());

        child = child.clone();
        child.doMove(new Move("d2d3", Side.WHITE));

        assertFalse(child.isDraw());
        assertFalse(child.isRepetition());

        child = child.clone();
        child.doMove(new Move("d5a5", Side.BLACK));

        assertTrue(child.isDraw());
        assertTrue(child.isRepetition());
    }
}
