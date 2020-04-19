package newalgebra.network.loss;

import core.tensor.Tensor;
import newalgebra.Cell;
import newalgebra.Output;

public class MSE extends Loss{

    Tensor target;

    public MSE() {
        super(1);
    }

    @Override
    public void setTarget(Tensor target, int index) {
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
