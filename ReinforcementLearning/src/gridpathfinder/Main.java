package gridpathfinder;

import java.io.IOException;

/**
 * @author : Asnit Bakhati
 */
public class Main {
    public static void main(String[] args) {

        GridWorld env = new GridWorld();
        QLearningAgent agent = new QLearningAgent();

        int episodes = 11000;

        if(agent.qTableExists()){
            try {
                agent.loadQTable();
            }catch (IOException ex){
                System.out.println("Cannot read the file");
            }
        }else System.out.println("file doesn't exist");

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


        try{
            agent.persistQTable();
        }catch (IOException ex){
            System.out.println("Cannot persist Q-Table");
        }
        System.out.println("\n--- DEMO START ---\n");

        env.reset();

        while (!env.isGoal()) {
            env.printGrid();

            int state = env.getState();
            int action = agent.getBestAction(state);

            env.step(action);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        env.printGrid();
        System.out.println("Reached Goal!");
    }
}
