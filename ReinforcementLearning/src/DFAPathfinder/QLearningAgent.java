package DFAPathfinder;

import java.util.Map;

import java.util.HashMap;
import java.util.Random;

public class QLearningAgent {

    private final Map<DfaNode, Map<Character, Double>> qTable;
    private final double alpha;
    private final double gamma;
    private final double epsilon;
    private final Dfa dfa;
    private final Random random;


    public QLearningAgent(Dfa dfa, double alpha, double gamma, double epsilon) {
        this.dfa = dfa;
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
        this.qTable = new HashMap<>();
        this.random = new Random();
    }

    public void train(int episodes) {
        for (int ep = 0; ep < episodes; ep++) {
            dfa.reset();
            DfaNode state = dfa.setPointer();
            int steps = 0;
            while (!dfa.isGoal(state) && !dfa.isDead(state)) {
                ensureStateExists(state);
                char action = chooseAction(state);
                DfaNode nextState = state.transform(action);

                ensureStateExists(nextState);

                double reward = getReward(nextState);

                double oldQ = qTable.get(state).get(action);
                double maxNextQ = getMaxQ(nextState);

                double updatedQ =
                        oldQ + alpha * (reward + gamma * maxNextQ - oldQ);

                qTable.get(state).put(action, updatedQ);

                state = nextState;
                steps++;
            }
        }
    }

    private char chooseAction(DfaNode state) {
        if (random.nextDouble() < epsilon) {
            return random.nextBoolean() ? 'a' : 'b'; // explore
        }
        return getBestAction(state); // exploit
    }

    private char getBestAction(DfaNode state) {
        Map<Character, Double> actions = qTable.get(state);
        return actions.get('a') >= actions.get('b') ? 'a' : 'b';
    }


    private void ensureStateExists(DfaNode state) {
        qTable.putIfAbsent(state, new HashMap<>());
        qTable.get(state).putIfAbsent('a', 0.0);
        qTable.get(state).putIfAbsent('b', 0.0);
    }

    private double getMaxQ(DfaNode state) {
        Map<Character, Double> actions = qTable.get(state);
        return Math.max(actions.get('a'), actions.get('b'));
    }


    private double getReward(DfaNode state) {
        if (dfa.isGoal(state)) return 100.0;
        if (dfa.isDead(state)) return -100.0;
        return -1.0;
    }

    public String generateString() {
        dfa.reset();
        DfaNode state = dfa.setPointer();
        StringBuilder sb = new StringBuilder();
        int steps = 0;

        while (!dfa.isGoal(state) && !dfa.isDead(state)) {
            ensureStateExists(state);
            char action = getBestAction(state);

            sb.append(action);
            state = state.transform(action);
            steps++;
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        Dfa dfa = new Dfa();
        QLearningAgent agent = new QLearningAgent(dfa,0.1,0.9,0.2);
        agent.train(1000);
        System.out.println(agent.generateString());
    }
}
