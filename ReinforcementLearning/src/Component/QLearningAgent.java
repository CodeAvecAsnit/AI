package Component;

import java.util.Random;

public abstract class QLearningAgent {

    protected double[][] qTable;
    protected final double alpha;
    protected final double gamma;
    protected final double epsilon;
    protected final Random random;

    protected QLearningAgent(double alpha, double gamma, double epsilon,int len,int bre) {
        this.qTable = new double[len][bre];
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
        this.random = new Random();
    }
}


