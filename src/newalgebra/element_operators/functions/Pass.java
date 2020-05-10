package newalgebra.element_operators.functions;

import newalgebra.cells.Cell;

import java.io.Serializable;

public class Pass extends ElementFunction<Pass> implements Serializable {

    public Pass() {
    }

    public Pass(Cell previous, int output) {
        super(previous, output);
    }



    @Override
    protected double apply(double x) {
        return x;
    }

    @Override
    protected double applyDerivative(double x) {
        return 1;
    }


}
