package newalgebra.element_operators.functions;

import java.io.Serializable;

public class LeakyReLU extends ElementFunction<LeakyReLU> implements Serializable {
    public LeakyReLU() {
    }

    @Override
    protected double apply(double x) {
        if(x > 0) return x;
        return x*0.05;
    }

    @Override
    protected double applyDerivative(double x) {
        if(x > 0) return 1;
        return 0.05;
    }
}
