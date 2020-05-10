package genetic_algorithm.qlearning.storage;

import genetic_algorithm.qlearning.statetypes.QIndex;

public class QTable extends QStorage<QIndex> {


    private double[][] data;

    private double discount = 0.9;
    private double learningRate = 0.1;

    public QTable(int states, int actions){
        this.data = new double[states][actions];
    }

    public QTable(int states, int actions, double discount, double learningRate) {
        this(states, actions);
        this.discount = discount;
        this.learningRate = learningRate;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public double[] getState(int state){
        return data[state];
    }

    @Override
    public double get(QIndex state, int action) {
        return getState(state.getIndex())[action];
    }

    @Override
    public int action(QIndex state){
        int index = 0;
        double[] decisions = getState(state.getIndex());
        for(int i = 1; i < decisions.length; i++){
            if(decisions[index] < decisions[i]){
                index = i;
            }
        }
        return index;
    }

    @Override
    public double max(QIndex state) {
        return data[state.getIndex()][action(state)];
    }

    @Override
    public void update(QIndex state, int decision, QIndex nextState, double reward) {
        double oldValue =get(state, decision);
        double optimalFutureValue = max(nextState);
        double newValue = oldValue + learningRate * (reward + discount * optimalFutureValue - oldValue);

        data[state.getIndex()][decision] = newValue;
    }
}
