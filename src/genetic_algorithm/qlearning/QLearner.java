package genetic_algorithm.qlearning;

import genetic_algorithm.qlearning.statetypes.QState;
import genetic_algorithm.qlearning.storage.QStorage;

public class QLearner<T extends QState> {

    private QController<T> controller;
    private QStorage<T> qStorage;

    public QLearner(QController<T> controller, QStorage<T> qStorage) {
        this.controller = controller;
        this.qStorage = qStorage;
    }

    public void train(int actions){
        /**
         * how many actions to perform
         */
        for(int i = 0; i < actions; i++){

            T state = controller.getState();

            /**
             * for stability reasons and to now get trapped, the first moves can be random
             */
            boolean random = Math.random() > i/(double)actions;
            int action = qStorage.action(state);
            if(random){
                action = (int)(Math.random() * controller.actionCount(state));
            }

            double reward = controller.perform(action);
            T nextState = controller.getState();

            /**
             * update q table
             */
            qStorage.update(state, action, nextState, reward);
        }
    }

    public void play(int actions){
        for(int i = 0; i < actions; i++){
            T state = controller.getState();

            int action = qStorage.action(state);

            System.out.println(controller);

            controller.perform(action);
        }
    }
}
