package newalgebra.element_operators.functions;

import newalgebra.Cell;

public class Sigmoid extends ElementFunction {

    public Sigmoid() {
    }

    public Sigmoid(Cell previous, int output) {
        super(previous, output);
    }

    @Override
    protected double apply(double x) {
        return 1d / (1+Math.exp(-x));
    }

    @Override
    protected double applyDerivative(double x) {
        return apply(x) * (1-apply(x));
    }
}
