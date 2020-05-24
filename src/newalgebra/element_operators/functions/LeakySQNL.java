package newalgebra.element_operators.functions;

import java.io.Serializable;

public class LeakySQNL extends ElementFunction<LeakySQNL> implements Serializable {

	@Override
	protected double apply(double x) {
		if(1 <= x) {
			return ((x-0.5)/x)+.25;
		}
		else if(x <= -1) {
			return ((x-0.5)/x)-2.25;
		}
		else if(0 <= x && x < 1) {
			return x - (x*x)/4;
		}
		return x + (x*x)/4;
	}

	@Override
	protected double applyDerivative(double x) {
		if(0 <= x && x < 1) {
			return 1 - (x/2);
		}
		else if(-1 < x && x < 0.0) {
			return 1 + (x/2);
		}
		return 1/(2*x*x);
	}

}
