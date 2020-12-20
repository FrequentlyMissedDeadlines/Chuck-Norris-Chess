package com.contracyclix.chuckchess.mcts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class Mcts<S extends State<A>, A> {
    private final A NO_ACTION = null;

    private final AtomicLong totalIterations = new AtomicLong();

    private final ExecutorService executor;
    private final long timePerActionMillis;
    private final long maxIterations;
    private final int threads;

    private Node<S, A> root;
    private A lastAction;

    public Mcts(
            ExecutorService executor,
            int threads,
            long timePerActionMillis,
            long maxIterations) {
        this.executor = executor;
        this.threads = threads;
        this.timePerActionMillis = timePerActionMillis;
        this.maxIterations = maxIterations;
    }

    public A getLastAction() {
        return lastAction;
    }

    public long getTotalIterations() {
        return totalIterations.get();
    }

    public void setRoot(A action, S state) {
        if (root != null) {
            Node<S, A> child = root.findChildFor(action);
            if (child != null) {
                root = child;
                root.releaseParent();
                return;
            }
        }
        root = new Node<S, A>(null, NO_ACTION, state);
    }

    public void think() {
        if (threads == 1) {
            doThink();
            return;
        }

        Collection<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < threads; i++)
            tasks.add(() -> {
                doThink();
                return null;
            });

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    private void doThink() {
        long started = System.currentTimeMillis();
        long i = 0;
        Random random = ThreadLocalRandom.current();
        while (i++ < maxIterations && System.currentTimeMillis() - started < timePerActionMillis
                || !root.isExpanded()) {

            growTree(random);
            totalIterations.incrementAndGet();
        }
    }

    public State takeAction() {
        Node<S, A> actionNode = root.childToExploit();
        lastAction = actionNode.getAction();
        root = actionNode;
        root.releaseParent();
        return actionNode.getState();
    }

    private void growTree(Random random) {
        Node<S, A> child = selectOrExpand();
        S terminalState = simulate(child, random);
        backPropagate(child, terminalState);
    }

    private Node<S, A> selectOrExpand() {
        Node<S, A> node = root;
        while (!node.isTerminal()) {
            if (!node.isExpanded()) {
                Node<S, A> expandedNode = node.expand();
                if (expandedNode != null)
                    return expandedNode;
            }
            node = node.childToExplore();
        }
        return node;
    }

    @SuppressWarnings("unchecked")
    private S simulate(Node<S, A> node, Random random) {
        S state = (S) node.getState().copy();
        while (!state.isTerminal()) {
            A[] actions = state.getAvailableActions();
            int randomIdx = random.nextInt(actions.length);
            A action = actions[randomIdx];
            state.applyAction(action);
        }
        return state;
    }

    private void backPropagate(Node<S, A> node, S terminalState) {
        while (node != null) {
            double reward = terminalState.getRewardFor(node.getPreviousAgent());
            node.updateRewards(reward);
            node = node.getParent();
        }
    }

}