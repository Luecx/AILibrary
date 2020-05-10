package newalgebra.element_operators.functions;

import newalgebra.cells.Cell;

import java.io.Serializable;

public class Sigmoid extends ElementFunction<Sigmoid> implements Serializable {

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
