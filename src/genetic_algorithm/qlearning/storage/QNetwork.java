package genetic_algorithm.qlearning.storage;

import core.tensor.Tensor;
import genetic_algorithm.qlearning.statetypes.QInput;
import genetic_algorithm.qlearning.statetypes.QState;
import newalgebra.network.Network;
import newalgebra.network.loss.MSE;

import java.util.ArrayList;
import java.util.LinkedList;


public class QNetwork extends QStorage<QInput> {


    private class QRecord{

        private QInput state;
        private QInput nextState;
        private int action;
        private double reward;

        public QRecord(QInput state, QInput nextState, int action, double reward) {
            this.state = state;
            this.nextState = nextState;
            this.action = action;
            this.reward = reward;
        }

        public QInput getState() {
            return state;
        }

        public void setState(QInput state) {
            this.state = state;
        }

        public QInput getNextState() {
            return nextState;
        }

        public void setNextState(QInput nextState) {
            this.nextState = nextState;
        }

        public int getAction() {
            return action;
        }

        public void setAction(int action) {
            this.action = action;
        }

        public double getReward() {
            return reward;
        }

        public void setReward(double reward) {
            this.reward = reward;
        }
    }

    private int bufferSize;
    private LinkedList<QRecord> records = new LinkedList<>();

    private double discount = 0.9;

    private Network network;

    public QNetwork(Network network, int bufferSize){
        this.bufferSize = bufferSize;

        if(!(network.getLoss() instanceof MSE)){
            throw new RuntimeException();
        }

        this.network = network;
    }


    private double update(QRecord record){

        double maxNextState = this.max(record.getNextState());
        double targetAtAction = record.getReward() + this.discount * maxNextState;

        Tensor target = network.calc(record.getState().getInput())[0];
        target.getData()[record.getAction()] = targetAtAction;

        return network.train(record.getState().getInput(), target);
    }


    @Override
    public void update(QInput state, int decision, QInput nextState, double reward) {

        QRecord record = new QRecord(state, nextState, decision, reward);
        records.addFirst(record);
        if(records.size() > bufferSize){
            records.removeLast();
        }
        double sum = 0;
        for(QRecord rec:records){
            sum += update(rec);
        }
        //System.out.println(sum/records.size());

    }

    @Override
    public double max(QInput state) {
        Tensor out = network.calc(state.getInput())[0];
        return out.max();
    }

    @Override
    public int action(QInput state) {
        Tensor out = network.calc(state.getInput())[0];
        double max = out.max();
        for(int i = 0; i < out.size(); i++){
            if(out.getData()[i] == max){
                return i;
            }
        }
        return 0;
    }

    @Override
    public double get(QInput state, int action) {
        Tensor out = network.calc(state.getInput())[0];
        return out.getData()[action];
    }
}
