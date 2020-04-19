package newalgebra.element_operators.functions;

import newalgebra.Cell;
import newalgebra.Input;
import newalgebra.Output;
import newalgebra.element_operators.ElementOperator;

public abstract class ElementFunction extends ElementOperator {

    public ElementFunction() {
        super(1);
    }

    public ElementFunction(Cell previous, int output) {
        super(previous, output, 0);
    }

    public Input getInput() {
        return getInput(0);
    }

    @Override
    public void generateOutputDimension(){
        if(this.inputCount() != 1){
            throw new RuntimeException("Cannot apply function to more than 1 value");
        }
        super.generateOutputDimension();
    }

    @Override
    public final void calc() {
        Output o = this.getOutput();

        for(int c = 0; c < getOutput().getDimension().size(); c++){

            double in = getInput().getValue().getData()[c];

            getFunctionDerivative(0).getData()[c] = applyDerivative(in);
            o.getValue().getData()[c] = apply(in);
        }

    }

    protected abstract double apply(double x);

    protected abstract double applyDerivative(double x);

}
