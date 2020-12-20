package com.contracyclix.chuckchess.mcts;

public interface State<A> {

    boolean isTerminal();

    A[] getAvailableActions();

    int getPreviousAgent();

    double getRewardFor(int agent);

    State takeAction(A action);

    State copy();

    void applyAction(A action);

    int getWinner();

}