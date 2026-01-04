package gridpathfinder;

import Component.QLearningAgent;
import FileHandling.FilePersister;

import java.io.*;
import java.util.Random;

class MatrixLearningAgent extends QLearningAgent {

    private final static String FILE_NAME;
    static final int STATES = GridWorld.ROWS * GridWorld.COLS;
    static final int ACTIONS = 4;
    private FilePersister filePersister;

    Random random = new Random();

    static{
        FILE_NAME = "gridPathFinder.bin";
    }

    public MatrixLearningAgent(){
        super(0.1,0.9,0.2,STATES,ACTIONS);
        this.filePersister = new FilePersister(new File(FILE_NAME));
    }

    public int chooseAction(int state) {
        if (random.nextDouble() < epsilon) {
            return random.nextInt(ACTIONS);
        }
        return getBestAction(state);
    }

    public int getBestAction(int state) {
        double max = qTable[state][0];
        int best = 0;

        for (int i = 1; i < ACTIONS; i++) {
            if (qTable[state][i] > max) {
                max = qTable[state][i];
                best = i;
            }
        }
        return best;
    }

    public void update(int state, int action, int reward, int nextState) {
        double maxNext = qTable[nextState][0];
        for (int i = 1; i < ACTIONS; i++) {
            maxNext = Math.max(maxNext, qTable[nextState][i]);
        }
        qTable[state][action] += alpha *
                (reward + gamma * maxNext - qTable[state][action]);
    }
    public boolean qTableExists(){
        return filePersister.fileExists();
    }

    public void loadQTableInAgent() throws IOException {
        filePersister.loadQTable(qTable);
    }

    public void persistQTable() throws IOException {
        filePersister.persistQTable(qTable);
    }

}
