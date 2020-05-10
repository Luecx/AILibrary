package newalgebra.element_operators.functions;

import newalgebra.cells.Cell;
import newalgebra.cells.Input;
import newalgebra.cells.Output;
import newalgebra.element_operators.ElementOperator;

import java.io.Serializable;

public abstract class ElementFunction<T extends ElementFunction<T>> extends ElementOperator<T> implements Serializable {




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

    @Override
    public boolean inputCountOK() {
        return this.inputCount() == 1;
    }

    protected abstract double apply(double x);

    protected abstract double applyDerivative(double x);

}
