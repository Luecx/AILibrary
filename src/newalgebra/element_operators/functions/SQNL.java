package newalgebra.element_operators.functions;

import java.io.Serializable;

public class SQNL extends ElementFunction<SQNL> implements Serializable{

	@Override
	protected double apply(double x) {
		if(x > 2.0) {
			return 1;
		}
		else if(0 <= x && x <= 2.0) {
			return x - ((x*x)/4);
		}
		else if(-2.0 <= x && x < 0.0) {
			return x + ((x*x)/4);
		}
		return -1;
	}

	@Override
	protected double applyDerivative(double x) {
		if(0 <= x && x <= 2.0) {
			return 1 - (x/2.0);
		}
		else if(-2.0 <= x && x < 0.0) {
			return 1 + (x/2.0);
		}
		return 0;
	}

}
