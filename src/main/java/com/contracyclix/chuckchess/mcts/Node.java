package com.contracyclix.chuckchess.mcts;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

class Node<S extends State<A>, A> {
    private static final double EXPLORATION_CONSTANT = Math.sqrt(2);
    private static final double NO_EXPLORATION = 0;

    private final AtomicInteger untakenIndex = new AtomicInteger();
    private final AtomicInteger visits = new AtomicInteger();

    private final AtomicReferenceArray<Node<S, A>> children;
    private final A[] untakenActions;
    private final S state;
    private final A action;

    private volatile Node<S, A> parent;
    private volatile double rewards;

    Node(Node<S, A> parent, A action, S state) {
        this.parent = parent;
        this.action = action;
        this.state = state;
        this.untakenActions = state.getAvailableActions();
        this.children = new AtomicReferenceArray<>(untakenActions.length);
        this.untakenIndex.set(untakenActions.length - 1);
    }

    Node<S, A> findChildFor(A action) {
        for (int i = 0; i < children.length(); i++) {
            Node<S, A> child = children.get(i);
            if (child == null)
                continue;
            if (child.action == action)
                return child;
        }
        return null;
    }

    void releaseParent() {
        parent = null;
    }

    A getAction() {
        return action;
    }

    boolean isTerminal() {
        return state.isTerminal();
    }

    boolean isExpanded() {
        return untakenIndex.get() < 0;
    }

    @SuppressWarnings("unchecked")
    Node<S, A> expand() {
        int untakenIdx = untakenIndex.getAndDecrement();
        if (untakenIdx < 0)
            return null;

        A untakenAction = untakenActions[untakenIdx];
        S actionState = (S) state.takeAction(untakenAction);
        Node<S, A> child = new Node<S, A>(this, untakenAction, actionState);
        children.set(untakenIdx, child);
        return child;
    }

    Node<S, A> getParent() {
        return parent;
    }

    int getPreviousAgent() {
        return state.getPreviousAgent();
    }

    void updateRewards(double reward) {
        visits.incrementAndGet();
        this.rewards += reward;
    }

    boolean isVisited() {
        return visits.get() > 0;
    }

    double getUctValue(double c) {
        int visits1 = visits.get();
        return rewards / visits1 + c * Math.sqrt(Math.log(parent.visits.get()) / visits1);
    }

    Node<S, A> childToExploit() {
        return getBestChild(NO_EXPLORATION);
    }

    Node<S, A> childToExplore() {
        return getBestChild(EXPLORATION_CONSTANT);
    }

    private Node<S, A> getBestChild(double c) {
        assert isExpanded();
        Node<S, A> best = null;
        while (best == null) {
            double bestValue = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < children.length(); i++) {
                // non spinlocking: seemed worse
                // Node<Action, StateT> child = children.get(i);
                // if (child == null || !child.isVisited())
                // continue;

                Node<S, A> child = null;
                while (child == null)
                    child = children.get(i);
                while (!child.isVisited()) {}

                double childrenValue = child.getUctValue(c);
                if (childrenValue > bestValue) {
                    best = child;
                    bestValue = childrenValue;
                }
            }
        }
        return best;
    }

    S getState() {
        return state;
    }

    @Override
    public String toString() {
        return "Node [visits=" + visits + ", rewards=" + rewards + ", v="
                + getUctValue(NO_EXPLORATION) + "]";
    }

}
