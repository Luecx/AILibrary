package newalgebra.element_operators.functions;

import java.io.Serializable;

public class ReLU extends ElementFunction<ReLU> implements Serializable {

    public ReLU() {
    }

    @Override
    protected double apply(double x) {
        if(x > 0) return x;
        return 0;
    }

    @Override
    protected double applyDerivative(double x) {
        if(x > 0) return 1;
        return 0;
    }
}
