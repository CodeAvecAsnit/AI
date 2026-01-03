package snakegame;

import java.util.Random;

public class QLearningAgent {
    private double[][] QTable;
    private double alpha;      // Learning rate
    private double gamma;      // Discount factor
    private double epsilon;    // Exploration rate
    private SnakeWorld snakeWorld;
    private Random random;

    private final int STATE_SIZE = 144;
    private final int ACTION_SIZE = 4;

    public QLearningAgent(SnakeWorld snakeWorld, double alpha, double gamma, double epsilon) {
        this.snakeWorld = snakeWorld;
        this.alpha = alpha;
        this.gamma = gamma;
        this.epsilon = epsilon;
        this.random = new Random();

        QTable = new double[STATE_SIZE][ACTION_SIZE];
        for (int i = 0; i < STATE_SIZE; i++) {
            for (int j = 0; j < ACTION_SIZE; j++) {
                QTable[i][j] = random.nextDouble() * 0.01;
            }
        }
    }

    /**
     * Get simplified state index based on snake and apple positions
     * States represent relative positions:
     * - Danger direction (wall or self-collision)
     * - Food direction (relative to snake head)
     * - Current moving direction
     */
    public int getState() {
        int[] arr = snakeWorld.getState();
        int sx = arr[0];
        int sy = arr[1];
        int fx = arr[2];
        int fy = arr[3];

        int dx = Integer.compare(fx, sx) + 1;
        int dy = Integer.compare(fy, sy) + 1;

        int foodState = dy * 3 + dx;

        int danger = 0;
        if (sy <= 0) danger |= 1 << 0;
        if (sy >= SnakeWorld.GridY - 1) danger |= 1 << 1;
        if (sx <= 0) danger |= 1 << 2;
        if (sx >= SnakeWorld.GridX - 1) danger |= 1 << 3;

        return foodState * 16 + danger;
    }



    /**
     * Choose action using epsilon-greedy policy
     */
    public int chooseAction(int state) {
        if (random.nextDouble() < epsilon) {
            return random.nextInt(ACTION_SIZE) + 1;
        }
        return getBestAction(state);
    }

    /**
     * Get the best action for a given state
     */
    private int getBestAction(int state) {
        double maxQ = Double.NEGATIVE_INFINITY;
        int bestAction = 1;

        for (int action = 0; action < ACTION_SIZE; action++) {
            if (QTable[state][action] > maxQ) {
                maxQ = QTable[state][action];
                bestAction = action + 1;
            }
        }

        return bestAction;
    }

    /**
     * Update Q-Table using Q-Learning formula:
     * Q(s,a) = Q(s,a) + α[r + γ * max(Q(s',a')) - Q(s,a)]
     */
    public void updateQTable(int state, int action, double reward, int nextState) {
        int actionIndex = action - 1;
        double maxNextQ = Double.NEGATIVE_INFINITY;
        for (int a = 0; a < ACTION_SIZE; a++) {
            if (QTable[nextState][a] > maxNextQ) {
                maxNextQ = QTable[nextState][a];
            }
        }

        double currentQ = QTable[state][actionIndex];
        double newQ = currentQ + alpha * (reward + gamma * maxNextQ - currentQ);
        QTable[state][actionIndex] = newQ;
    }

    /**
     * Train the agent for a single episode
     */
    public void trainEpisode() {
        snakeWorld.reset();
        int totalReward = 0;
        int steps = 0;

        while (!snakeWorld.isGameOver()) {
            int currentState = getState();
            int action = chooseAction(currentState);
            double reward = snakeWorld.performAction(action);
            totalReward += reward;
            int nextState = getState();
            updateQTable(currentState, action, reward, nextState);
        }

        System.out.println("Episode finished - Steps: " + steps +
                ", Score: " + snakeWorld.getScore() +
                ", Total Reward: " + totalReward);
    }

    /**
     * Train the agent for multiple episodes
     */
    public void train(int episodes) {
        System.out.println("Starting training for " + episodes + " episodes...");
        System.out.println("Alpha: " + alpha + ", Gamma: " + gamma +
                ", Epsilon: " + epsilon);
        System.out.println("----------------------------------------");

        for (int episode = 1; episode <= episodes; episode++) {
            System.out.print("Episode " + episode + " - ");
            trainEpisode();
            if (episode % 100 == 0) {
                epsilon = Math.max(0.01, epsilon * 0.95);
                System.out.println("Epsilon decayed to: " + epsilon);
            }
            if (episode % 50 == 0) {
                System.out.println("========== Completed " + episode + " episodes ==========");
            }
        }

        System.out.println("----------------------------------------");
        System.out.println("Training complete!");
    }

    /**
     * Test the trained agent (no exploration)
     */
    public void test() {
        snakeWorld.reset();
        double oldEpsilon = epsilon;
        epsilon = 0;

        System.out.println("\n=== Testing Agent ===");
        trainEpisode();

        epsilon = oldEpsilon;
    }

    /**
     * Print Q-Table for debugging
     */
    public void printQTable() {
        System.out.println("\n=== Q-Table ===");
        System.out.println("State\\Action\tUP\t\tDOWN\t\tLEFT\t\tRIGHT");
        for (int i = 0; i < STATE_SIZE; i++) {
            System.out.printf("State %d:\t", i);
            for (int j = 0; j < ACTION_SIZE; j++) {
                System.out.printf("%.3f\t\t", QTable[i][j]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        SnakeWorld world = new SnakeWorld();

        // Create Q-Learning agent
        // alpha = 0.1 (learning rate)
        // gamma = 0.9 (discount factor)
        // epsilon = 0.3 (exploration rate)
        QLearningAgent agent = new QLearningAgent(world, 0.1, 0.9, 0.9);

        agent.train(500000);

        agent.test();

        agent.printQTable();
    }
}