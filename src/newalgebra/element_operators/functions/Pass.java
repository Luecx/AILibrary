package newalgebra.element_operators.functions;

import newalgebra.Cell;
import newalgebra.Output;
import newalgebra.element_operators.ElementOperator;

public class Pass extends ElementFunction {

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
