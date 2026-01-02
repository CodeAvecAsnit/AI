package dfapathfinder;

import FileHandling.FilePersister;
import java.util.Random;

public class LengthFinderAgent3d {
    private double[][][] qTable;
    private final double alpha;
    private final double gamma;
    private double epsilon;
    private final Dfa dfa;
    private final Random random;
    FilePersister filePersister;

    public static final int TRANSITION = 2;
    private static final int MAX_LENGTH = 20;
    private static final double OPTIMISTIC_INIT = 20.0;  // Optimistic initialization

    public LengthFinderAgent3d(Dfa dfa, double alpha, double gamma, double epsilon, int totalStates) {
        this.dfa = dfa;
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
        this.qTable = new double[MAX_LENGTH + 1][totalStates][TRANSITION];
        this.random = new Random();
        this.filePersister = new FilePersister();

        // Optimistic initialization - encourage exploration
        for (int len = 0; len <= MAX_LENGTH; len++) {
            for (int state = 0; state < totalStates; state++) {
                qTable[len][state][0] = OPTIMISTIC_INIT;
                qTable[len][state][1] = OPTIMISTIC_INIT;
            }
        }
    }

    public void train(int episodes, int targetLen) {
        int successCount = 0;
        int demonstrationEpisodes = 5000;  // First 5000 episodes with guidance

        for (int ep = 0; ep < episodes; ep++) {
            dfa.reset();
            DfaNode state = dfa.setPointer();
            int currentLen = 0;

            // Slower epsilon decay with higher minimum
            double decayRate = 0.999995;  // Even slower decay
            double minEpsilon = 0.4;      // Higher minimum exploration
            double currentEpsilon = Math.max(minEpsilon, epsilon * Math.pow(decayRate, ep));

            // Use demonstration for first few episodes
            boolean useDemonstration = ep < demonstrationEpisodes;

            boolean success = false;

            while (currentLen < targetLen) {
                if (dfa.isDead(state)) {
                    updateQValue(state, 'a', null, currentLen, -100, true, targetLen);
                    break;
                }

                // Choose action (with demonstration guidance if early)
                char action;
                if (useDemonstration && currentLen < 2) {
                    action = 'b';  // Force 'bb' at start
                } else if (useDemonstration && currentLen >= targetLen - 2) {
                    action = 'a';  // Force 'aa' at end
                } else {
                    action = chooseAction(state, currentLen, currentEpsilon);
                }

                DfaNode nextState = state.transform(action);
                int nextLen = currentLen + 1;

                // Get reward with path shaping
                double reward = getShapedReward(state, action, nextState, currentLen, nextLen, targetLen);

                updateQValue(state, action, nextState, currentLen, reward, false, targetLen);

                state = nextState;
                currentLen = nextLen;

                // Check if reached goal at target length
                if (currentLen == targetLen && dfa.isGoal(state)) {
                    double finalReward = 100;
                    updateQValue(state, 'a', null, currentLen, finalReward, true, targetLen);
                    success = true;
                    successCount++;
                    break;
                } else if (currentLen == targetLen && !dfa.isGoal(state)) {
                    double finalReward = -100;
                    updateQValue(state, 'a', null, currentLen, finalReward, true, targetLen);
                    break;
                }
            }

            // Print progress
            if ((ep + 1) % 10000 == 0) {
                double successRate = (double) successCount / 10000;
                System.out.printf("  Episode %d: Success rate = %.2f%%, epsilon = %.3f%n",
                        ep + 1, successRate * 100, currentEpsilon);
                successCount = 0;
            }
        }
    }

    private void updateQValue(DfaNode state, char action, DfaNode nextState,
                              int currentLen, double reward, boolean terminal, int targetLen) {
        int stateIdx = state.getIndex() - 1;
        int actionIdx = getTransition(action);

        if (currentLen > MAX_LENGTH) return;

        double oldQ = qTable[currentLen][stateIdx][actionIdx];
        double maxNextQ = 0;

        if (!terminal && nextState != null && currentLen + 1 <= MAX_LENGTH) {
            maxNextQ = getMaxQ(nextState, currentLen + 1);
        }

        // Q-learning update: Q(s,a) ← Q(s,a) + α[r + γ·maxQ(s',a') - Q(s,a)]
        double updatedQ = oldQ + alpha * (reward + gamma * maxNextQ - oldQ);
        qTable[currentLen][stateIdx][actionIdx] = updatedQ;
    }

    private int getTransition(char a) {
        return (a == 'a') ? 0 : 1;
    }

    private char chooseAction(DfaNode state, int currentLen, double eps) {
        if (random.nextDouble() < eps) {
            return random.nextBoolean() ? 'a' : 'b';
        }
        return getBestAction(state, currentLen);
    }

    private char getBestAction(DfaNode state, int currentLen) {
        if (currentLen > MAX_LENGTH) return 'a';

        int stateIdx = state.getIndex() - 1;
        double qA = qTable[currentLen][stateIdx][0];
        double qB = qTable[currentLen][stateIdx][1];

        return qA >= qB ? 'a' : 'b';
    }

    private double getMaxQ(DfaNode state, int currentLen) {
        if (currentLen > MAX_LENGTH) return 0;

        int stateIdx = state.getIndex() - 1;
        return Math.max(qTable[currentLen][stateIdx][0], qTable[currentLen][stateIdx][1]);
    }

    /**
     * Shaped reward function with intermediate rewards for correct path
     * Pattern: bb(a+b)*a(a+b)aa
     * States: q1 -> q2 -> q3 -> q4 -> q5 -> q6 (accepting), q7 (dead)
     */
    private double getShapedReward(DfaNode currentState, char action, DfaNode nextState,
                                   int currentLen, int nextLen, int targetLen) {
        int currentStateIdx = currentState.getIndex();
        int nextStateIdx = nextState.getIndex();
        int stepsRemaining = targetLen - nextLen;

        // Dead state - severe penalty
        if (nextStateIdx == 7) {
            return -100;
        }

        // Terminal state (reached target length)
        if (nextLen == targetLen) {
            return (nextStateIdx == 6) ? 100 : -100;
        }

        // ===== PATH SHAPING REWARDS =====

        // Reward correct transitions in the required path

        // q1 --b--> q2: First 'b' (correct start)
        if (currentStateIdx == 1 && action == 'b' && nextStateIdx == 2) {
            return 10;
        }

        // q1 --a--> q7: Wrong start, goes to dead state
        if (currentStateIdx == 1 && action == 'a') {
            return -20;
        }

        // q2 --b--> q3: Second 'b' (completing "bb" prefix)
        if (currentStateIdx == 2 && action == 'b' && nextStateIdx == 3) {
            return 10;
        }

        // q2 --a--> q7: Wrong move from q2
        if (currentStateIdx == 2 && action == 'a') {
            return -20;
        }

        // q3 --a--> q4: Transition out of loop (critical move)
        if (currentStateIdx == 3 && action == 'a' && nextStateIdx == 4) {
            // Reward more if close to target (need to exit loop)
            return (stepsRemaining <= 4) ? 15 : 8;
        }

        // q3 --b--> q3: Staying in loop
        if (currentStateIdx == 3 && action == 'b' && nextStateIdx == 3) {
            // Penalize if too close to target (should exit loop)
            if (stepsRemaining <= 3) {
                return -10;  // Strong penalty - must exit now!
            } else if (stepsRemaining <= 5) {
                return -3;   // Mild penalty - should consider exiting
            }
            return -1;  // Small penalty - loop is OK for now
        }

        // q4 --a--> q5: Moving forward
        if (currentStateIdx == 4 && action == 'a' && nextStateIdx == 5) {
            return 8;
        }

        // q4 --b--> q4: Staying in q4 loop
        if (currentStateIdx == 4 && action == 'b' && nextStateIdx == 4) {
            return (stepsRemaining <= 2) ? -10 : -2;
        }

        // q5 --a--> q6: Reaching accepting state
        if (currentStateIdx == 5 && action == 'a' && nextStateIdx == 6) {
            return 10;
        }

        // q5 --b--> q4: Going back to q4
        if (currentStateIdx == 5 && action == 'b' && nextStateIdx == 4) {
            return (stepsRemaining <= 2) ? -10 : -2;
        }

        // q6 --a--> q6: Staying in accepting state
        if (currentStateIdx == 6 && action == 'a' && nextStateIdx == 6) {
            return 3;  // Good - can extend from accepting state
        }

        // q6 --b--> q4: Leaving accepting state
        if (currentStateIdx == 6 && action == 'b' && nextStateIdx == 4) {
            return (stepsRemaining <= 2) ? -10 : -1;
        }

        // Default: small step penalty
        return -1;
    }

    public String generateString(int targetLength) {
        dfa.reset();
        DfaNode state = dfa.setPointer();
        StringBuilder sb = new StringBuilder();
        int currentLen = 0;

        while (currentLen < targetLength) {
            if (dfa.isDead(state)) {
                break;
            }

            char action = getBestAction(state, currentLen);
            sb.append(action);
            state = state.transform(action);
            currentLen++;
        }

        return sb.toString();
    }

    public void seeQTable() {
        for (int len = 5; len <= 15; len++) {
            System.out.println("\n=== Q-Table for Length " + len + " ===");
            for (int s = 0; s < 7; s++) {
                char bestAction = qTable[len][s][0] >= qTable[len][s][1] ? 'a' : 'b';
                System.out.printf("State q%d: a=%7.2f, b=%7.2f -> '%c'%n",
                        s + 1, qTable[len][s][0], qTable[len][s][1], bestAction);
            }
        }
    }

    public static void main(String[] args) {
        Dfa dfa = new Dfa();

        // Key parameters:
        // α = 0.4 (high learning rate for fast updates)
        // γ = 0.95 (high discount for long-term planning)
        // ε₀ = 1.0 (start with full exploration)
        LengthFinderAgent agent = new LengthFinderAgent(dfa, 0.4, 0.95, 1.0, 7);

        System.out.println("=== Q-Learning DFA Path Finder ===");
        System.out.println("Pattern: bb(a+b)*a(a+b)aa");
        System.out.println("\nParameters:");
        System.out.println("  α (learning rate) = 0.4");
        System.out.println("  γ (discount factor) = 0.95");
        System.out.println("  ε (exploration) = 1.0 → 0.4 (with decay 0.999995)");
        System.out.println("  Optimistic init = 20.0");
        System.out.println("  Shaped rewards = enabled");
        System.out.println("  Demonstration = first 5000 episodes\n");

        // Train each length
        for (int targetLen = 5; targetLen <= 15; targetLen++) {
            int episodes = (targetLen <= 8) ? 1000000 : 500000;
            System.out.println("Training for length " + targetLen + " (" + episodes + " episodes)");
            agent.train(episodes, targetLen);
        }

        System.out.println("\n=== Generated Strings ===");
        int validCount = 0;
        for (int len = 5; len <= 15; len++) {
            String result = agent.generateString(len);
            boolean valid = dfa.isValidString(result) && result.length() == len;
            if (valid) validCount++;
            System.out.printf("Length %2d: %-20s [%s]%n",
                    len, result.isEmpty() ? "(empty)" : result,
                    valid ? "✓ VALID" : "✗ INVALID");
        }

        System.out.printf("\nSuccess rate: %d/11 (%.1f%%)%n", validCount, validCount * 100.0 / 11);

    }
}