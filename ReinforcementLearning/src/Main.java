/**
 * @author : Asnit Bakhati
 */
public class Main {
    public static void main(String[] args) {

        GridWorld env = new GridWorld();
        QLearningAgent agent = new QLearningAgent();

        int episodes = 10000;

        for (int ep = 0; ep < episodes; ep++) {
            env.reset();

            while (!env.isGoal()) {
                int state = env.getState();
                int action = agent.chooseAction(state);
                int reward = env.step(action);
                int nextState = env.getState();

                agent.update(state, action, reward, nextState);
            }
        }

        System.out.println("Training complete.");

        System.out.println("\n--- DEMO START ---\n");

        env.reset();

        while (!env.isGoal()) {
            env.printGrid();

            int state = env.getState();
            int action = agent.getBestAction(state);

            env.step(action);

            try {
                Thread.sleep(500); // slow motion demo
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        env.printGrid();
        System.out.println("Reached Goal!");

    }
}
