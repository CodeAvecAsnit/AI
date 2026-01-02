package dfapathfinder;

import FileHandling.FilePersister;

import java.util.Random;

public class LengthFinderAgent {

    double[][] qTable;
    private final double alpha;
    private final double gamma;
    private final double epsilon;
    private final Dfa dfa;
    private final Random random;
    FilePersister filePersister;

    public static final int TRANSITION = 2;

    public LengthFinderAgent(Dfa dfa, double alpha, double gamma, double epsilon,int totalStates) {
        this.dfa = dfa;
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
        this.qTable = new double[totalStates*10][TRANSITION];
        this.random = new Random();
        this.filePersister=new FilePersister();
        filePersister.displayQTable(this.qTable);
    }




    public void seeQTable(){
        filePersister.displayQTable(this.qTable);
    }

    private int getTransition(char a){
        return (a=='a')?0:1;
    }
    private char chooseAction(DfaNode state,int i) {
        if (random.nextDouble() < epsilon) {
            return random.nextBoolean() ? 'a' : 'b';
        }
        return getBestAction(state,i);
    }

    private char getBestAction(DfaNode state,int i) {
        int index = (state.getIndex()-1)*10+i;
        return qTable[index][0] >= qTable[index][1] ? 'a' : 'b';
    }

    private double getMaxQ(DfaNode state,int i) {
        int index = (state.getIndex()-1)*10+i;
        return Math.max(qTable[index][0], qTable[index][1]);
    }


    public void train(int episodes, int targetLen) {
        for (int ep = 0; ep < episodes; ep++) {
            dfa.reset();
            DfaNode state = dfa.setPointer();
            int currentLen = 0;

            while (currentLen < targetLen + 5) {  // Add max steps limit
                if (dfa.isGoal(state) && currentLen == targetLen) {
                    break;
                }
                if (dfa.isDead(state)) {
                    break;
                }

                char action = chooseAction(state, currentLen);
                DfaNode nextState = state.transform(action);
                currentLen++;

                double reward = getReward(nextState, currentLen, targetLen);

                int currentIndex = (state.getIndex()-1)*10 + Math.min(currentLen-1, 9);
                int nextIndex = (nextState.getIndex()-1)*10 + Math.min(currentLen, 9);

                double oldQ = qTable[currentIndex][getTransition(action)];
                double maxNextQ = (currentLen < 10) ? getMaxQ(nextState, currentLen) : 0;

                double updatedQ = oldQ + alpha * (reward + gamma * maxNextQ - oldQ);
                qTable[currentIndex][getTransition(action)] = updatedQ;

                state = nextState;
            }
        }
    }

    private double getReward(DfaNode s, int currentLen, int targetLen) {
        if (dfa.isDead(s)) return -100;

        if (dfa.isGoal(s) && currentLen == targetLen) {
            return 100;
        }

        if (currentLen >= targetLen) {
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
        return sb.toString();
    }

    public static void main(String[] args) {
        Dfa dfa = new Dfa();
        LengthFinderAgent agent = new LengthFinderAgent(dfa,0.1,0.9,0.9,7);
        for(int i = 0 ; i <= 10;++i) {
            agent.train(100000,i);
        }
        agent.seeQTable();
        for(int i = 0; i <=10;++i){
           System.out.println("Length "+(i+5)+" String : "+agent.generateString(i));
       }
    }
}
