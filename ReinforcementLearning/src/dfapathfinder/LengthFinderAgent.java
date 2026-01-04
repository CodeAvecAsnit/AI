package dfapathfinder;

import Component.QLearningAgent;
import FileHandling.FilePersister;

import java.io.File;


public class LengthFinderAgent extends QLearningAgent {

    private Dfa dfa;
    private final int maxLength;
    private final FilePersister filePersister;
    public static final int TRANSITIONS = 2;

    private static final String FILE_NAME ;

    static{
        FILE_NAME = "DfaLength.bin";
    }

    public LengthFinderAgent(Dfa dfa, double alpha, double gamma, double epsilon, int totalStates, int maxLength) {
        // State encoding: state_index * maxLength + current_length
        int len = totalStates*(maxLength+1);
        super(alpha,gamma,epsilon,len,TRANSITIONS);
        this.dfa = dfa;
        this.maxLength = maxLength;
        this.filePersister = new FilePersister(new File(FILE_NAME));
    }

    public void seeQTable() {
        filePersister.displayQTable(qTable);
    }

    private int getTransition(char a) {
        return (a == 'a') ? 0 : 1;
    }

    private int encodeState(DfaNode state, int length) {
        // Encode state as: (stateIndex - 1) * (maxLength + 1) + length
        return (state.getIndex() - 1) * (maxLength + 1) + length;
    }

    private char chooseAction(DfaNode state, int length) {
        if (random.nextDouble() < epsilon) {
            return random.nextBoolean() ? 'a' : 'b';
        }
        return getBestAction(state, length);
    }

    private char getBestAction(DfaNode state, int length) {
        int index = encodeState(state, length);
        return qTable[index][0] >= qTable[index][1] ? 'a' : 'b';
    }

    private double getMaxQ(DfaNode state, int length) {
        if (length > maxLength) return 0;
        int index = encodeState(state, length);
        return Math.max(qTable[index][0], qTable[index][1]);
    }

    public void train(int episodes, int targetLen) {
        for (int ep = 0; ep < episodes; ep++) {
            dfa.reset();
            DfaNode state = dfa.setPointer();
            int currentLen = 0;

            while (currentLen < targetLen + 10) {
                if (dfa.isGoal(state) && currentLen == targetLen) {
                    break;
                }
                if (dfa.isDead(state)) {
                    break;
                }
                if (currentLen > maxLength) {
                    break;
                }

                char action = chooseAction(state, currentLen);
                DfaNode nextState = state.transform(action);
                int nextLen = currentLen + 1;

                double reward = getReward(nextState, nextLen, targetLen);

                int currentIndex = encodeState(state, currentLen);
                double oldQ = qTable[currentIndex][getTransition(action)];
                double maxNextQ = getMaxQ(nextState, nextLen);

                double updatedQ = oldQ + alpha * (reward + gamma * maxNextQ - oldQ);
                qTable[currentIndex][getTransition(action)] = updatedQ;

                state = nextState;
                currentLen = nextLen;
            }
        }
    }

    private double getReward(DfaNode s, int currentLen, int targetLen) {
        if (dfa.isDead(s)) return -100;
        if (dfa.isGoal(s) && currentLen == targetLen) {
            return 100;
        }
        if (currentLen > targetLen) {
            return -50;
        }
        return -1;
    }

    public String generateString(int targetLength) {
        dfa.reset();
        DfaNode state = dfa.setPointer();
        StringBuilder sb = new StringBuilder();
        int currentLen = 0;
        while (currentLen < targetLength && !dfa.isDead(state)) {
            char action = getBestAction(state, currentLen);
            sb.append(action);
            state = state.transform(action);
            currentLen++;
            if (dfa.isGoal(state) && currentLen == targetLength) {
                break;
            }
        }
        if (dfa.isValidString(sb.toString()) && sb.length() == targetLength) {
            return sb.toString();
        }
        sb.append(" [INVALID]");
        return sb.toString();
    }

    public static void main(String[] args) {
        Dfa dfa = new Dfa();
        LengthFinderAgent agent = new LengthFinderAgent(dfa, 0.1, 0.9, 0.2, 7, 25);
        System.out.println("Training agent...");
        for (int i = 5; i <= 25; ++i) {
            System.out.println(STR."Training for length \{i}");
            agent.train(100000, i);
        }
        System.out.println("\nGenerating strings:");
        for (int i = 5; i <= 25; ++i) {
            String result = agent.generateString(i);
            System.out.println(STR."Length \{i} String: \{result}");
        }
    }
}