package neuralnetwork.functions;

public class SQNL extends Function {

	@Override
	protected double func_val(double x) {
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
	protected double func_val_prime(double x) {
		if(0 <= x && x <= 2.0) {
			return 1 - (x/2.0);
		}
		else if(-2.0 <= x && x < 0.0) {
			return 1 + (x/2.0);
		}
		return 0;
	}

}
