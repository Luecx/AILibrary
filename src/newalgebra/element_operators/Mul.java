package newalgebra.element_operators;

import newalgebra.cells.Output;

import java.io.Serializable;

public class Mul extends ElementOperator<Mul> implements Serializable {

    public Mul() {
        this(0);
    }

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
                if(getInput(i).getValue().getData()[v] == 0){
                    double der = 1;
                    for(int k = 0; k < inputCount(); k++){
                        if(k == i) continue;
                        der *= getInput(k).getValue().getData()[v];
                    }
                }else{
                    getFunctionDerivative()[i].getData()[v] = o.getValue().getData()[v] / getInput(i).getValue().getData()[v];
                }
            }
        }
    }


}
