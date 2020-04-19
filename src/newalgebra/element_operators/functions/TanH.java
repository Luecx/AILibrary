package newalgebra.element_operators.functions;

import newalgebra.Cell;
import newalgebra.Output;
import newalgebra.element_operators.ElementOperator;

public class TanH extends ElementFunction {

    public TanH() {
    }

    public TanH(Cell previous, int output) {
        super(previous, output);
    }

    @Override
    protected double apply(double x) {
        return Math.tanh(x);
    }

    @Override
    protected double applyDerivative(double x) {
        return 1-apply(x)*apply(x);
    }
}
