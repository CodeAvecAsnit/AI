package dfapathfinder;

import FileHandling.FilePersister;

import java.util.Random;

public class QLearningAgent {

    private double[][] qTable;
    private final double alpha;
    private final double gamma;
    private final double epsilon;
    private final Dfa dfa;
    private final Random random;
    FilePersister filePersister;

    public static final int TRANSITION = 2;


    public QLearningAgent(Dfa dfa, double alpha, double gamma, double epsilon,int totalStates) {
        this.dfa = dfa;
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
        this.qTable = new double[totalStates][TRANSITION];
        this.random = new Random();
        this.filePersister = new FilePersister();
        filePersister.displayQTable(this.qTable);
    }

    public void train(int episodes) {
        for (int ep = 0; ep < episodes; ep++) {
            dfa.reset();
            DfaNode state = dfa.setPointer();
            int steps = 0;
            while (!dfa.isDead(state)&&steps<50) {

                char action = chooseAction(state);
                DfaNode nextState = state.transform(action);
                double reward = getReward(nextState);

                double oldQ = qTable[state.getIndex() - 1][getTransition(action)];
                double maxNextQ = getMaxQ(nextState);

                double updatedQ =
                        oldQ + alpha * (reward + gamma * maxNextQ - oldQ);

                qTable[state.getIndex() - 1][getTransition(action)] = updatedQ;

                state = nextState;
                steps++;
            }

        }
    }

    public void seeQTable(){
        filePersister.displayQTable(this.qTable);
    }

    private int getTransition(char a){
        return (a=='a')?0:1;
    }

    private char chooseAction(DfaNode state) {
        if (random.nextDouble() < epsilon) {
            return random.nextBoolean() ? 'a' : 'b';
        }
        return getBestAction(state);
    }

    private char getBestAction(DfaNode state) {
        int index = state.getIndex();
        return qTable[index-1][0] > qTable[index-1][1] ? 'a' : 'b';
    }

    private double getMaxQ(DfaNode state) {
        int i = state.getIndex()-1;
        return Math.max(qTable[i][0], qTable[i][1]);
    }

    private double getReward(DfaNode state) {
        if (dfa.isGoal(state)) return 100.0;
        if (dfa.isDead(state)) return -100.0;
        return 1.0;
    }

    public String generateStringOfLength(int length){
       dfa.reset();
       DfaNode startNode = dfa.setPointer();
       StringBuilder sb = new StringBuilder();
       for(int i = 0 ; i <length;++i){
           char act = getBestAction(startNode);
           sb.append(act);
           startNode = startNode.transform(act);
       }
       return sb.toString();
    }

    public String generateString() {
        dfa.reset();
        DfaNode state = dfa.setPointer();
        StringBuilder sb = new StringBuilder();

        while (!dfa.isGoal(state) && !dfa.isDead(state)) {
            char action = getBestAction(state);

            sb.append(action);
            state = state.transform(action);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        Dfa dfa = new Dfa();
        QLearningAgent agent = new QLearningAgent(dfa,0.1,0.9,0.2,7);
        agent.train(1000000);
        agent.seeQTable();
        System.out.println(agent.generateStringOfLength(100));
        System.out.println(agent.generateString());
    }
}
