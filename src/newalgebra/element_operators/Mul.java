package newalgebra.element_operators;

import newalgebra.Output;

import java.util.Arrays;

public class Mul extends ElementOperator {

    public Mul(int inputs) {
        super(inputs);
    }

    public Mul(Object... args) {
        super(args);
    }

    @Override
    public boolean canAddInputs() {
        return true;
    }

    @Override
    public void calc() {
        Output o = this.getOutput();

        o.getValue().reset(1);

        for(int i = 0; i < inputCount(); i++){
            o.getValue().self_hadamard(getInput(i).getValue());
        }


        for(int i = 0; i < inputCount(); i++){
            for(int v = 0; v < getOutput().getDimension().size(); v++){
                getFunctionDerivative()[i].getData()[v] = o.getValue().getData()[v] / getInput(i).getValue().getData()[v];
            }
        }
    }


}
