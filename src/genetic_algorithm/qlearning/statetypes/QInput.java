package genetic_algorithm.qlearning.statetypes;

import core.tensor.Tensor;

public class QInput extends QState {

    private Tensor input;

    public QInput(Tensor input) {
        this.input = input;
    }

    public Tensor getInput() {
        return input;
    }

    public void setInput(Tensor input) {
        this.input = input;
    }
}
