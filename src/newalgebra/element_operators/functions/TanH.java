package newalgebra.element_operators.functions;

import newalgebra.cells.Cell;

import java.io.Serializable;

public class TanH extends ElementFunction<TanH> implements Serializable {

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
