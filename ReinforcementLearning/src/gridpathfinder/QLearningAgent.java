package gridpathfinder;

import FileHandling.FilePersister;

import java.io.*;
import java.util.Random;

class QLearningAgent {

    static final int STATES = GridWorld.ROWS * GridWorld.COLS;
    static final int ACTIONS = 4;

    File file = new File("q_table.bin");
    double[][] Q = new double[STATES][ACTIONS];

    double alpha = 0.1;
    double gamma = 0.9;
    double epsilon = 0.2;
    private FilePersister filePersister;

    Random random = new Random();

    public QLearningAgent(FilePersister filePersister){
        this.filePersister = new FilePersister();
    }

    public int chooseAction(int state) {
        if (random.nextDouble() < epsilon) {
            return random.nextInt(ACTIONS);
        }
        return getBestAction(state);
    }

    public int getBestAction(int state) {
        double max = Q[state][0];
        int best = 0;

        for (int i = 1; i < ACTIONS; i++) {
            if (Q[state][i] > max) {
                max = Q[state][i];
                best = i;
            }
        }
        return best;
    }

    public void update(int state, int action, int reward, int nextState) {
        double maxNext = Q[nextState][0];
        for (int i = 1; i < ACTIONS; i++) {
            maxNext = Math.max(maxNext, Q[nextState][i]);
        }
        Q[state][action] += alpha *
                (reward + gamma * maxNext - Q[state][action]);
    }
    public boolean qTableExists(){
        return file.exists();
    }

    public void loadQTableInAgent() throws IOException {
        filePersister.loadQTable(file,Q);
    }

    public void persistQTable() throws IOException {
        filePersister.persistQTable(file,Q);
    }

}
