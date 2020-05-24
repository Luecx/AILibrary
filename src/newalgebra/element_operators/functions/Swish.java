package newalgebra.element_operators.functions;

import java.io.Serializable;

public class Swish extends ElementFunction<Swish> implements Serializable {

	@Override
	protected double apply(double x) {
		return x * sig(x);
	}

	@Override
	protected double applyDerivative(double x) {
		double y = apply(x);
		return y + sig(x) * (1 - y);
	}

	private double sig(double x) {
		return (1 / (1 + Math.exp(-x)));
	}
	
}
