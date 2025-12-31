package gridpathfinder;

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

    Random random = new Random();

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

    public void loadQTable() throws IOException {
        FileInputStream fis = new FileInputStream(file);
        DataInputStream dis = new DataInputStream(fis);
        for(int i = 0; i < Q.length;++i){
            for(int j = 0; j < Q[0].length;++j){
                Q[i][j]=dis.readDouble();
            }
        }
    }

    public void persistQTable() throws IOException {
        FileOutputStream fis = new FileOutputStream(file);
        DataOutputStream dis = new DataOutputStream(fis);
        for (int i = 0; i < Q.length; ++i) {
            for (int j = 0; j < Q[0].length; ++j) {
                dis.writeDouble(Q[i][j]);
            }
        }
        System.out.println("File saved");
    }

    public void displayQTable(){
        System.out.println("Q - Table");
        System.out.println();
        for(int i = 0; i < Q.length;++i){
            for(int j = 0; j < Q[0].length;++j){
                System.out.print(Q[i][j]+", ");
            }
            System.out.println();
        }
    }
}
