package newalgebra.network.loss;

import core.tensor.Tensor;

import java.io.Serializable;

public class MSE extends Loss implements Serializable {

    Tensor target;

    public MSE() {
        super(1);
    }

    @Override
    public boolean inputCountOK() {
        return inputCount() == 1;
    }

    @Override
    protected void addTarget(Tensor target, int index) {
        this.target = target;
    }

    @Override
    public void calc() {
        double loss = 0;

        for(int i = 0; i < getInput(0).getOutput().getValue().size(); i++){
            loss += 0.5 *
                    (getInput(0).getOutput().getValue().getData()[i] - target.getData()[i]) *
                    (getInput(0).getOutput().getValue().getData()[i] - target.getData()[i]);
        }
        loss /= getInput(0).getOutput().getValue().size();
        this.getOutput(0).getValue().reset(loss);
    }

    @Override
    public void autoDiff() {

        for(int i = 0; i < getInput(0).getOutput().getValue().size(); i++){
            getInput(0).getGradient().getData()[i] += getInput(0).getOutput().getValue().getData()[i] - target.getData()[i];
        }

    }
}
