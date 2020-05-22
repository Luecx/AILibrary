package neuralnetwork.functions;

public class Swish extends Function {

	@Override
	protected double func_val(double x) {
		return x * sig(x);
	}

	@Override
	protected double func_val_prime(double x) {
		double y = func_val(x);
		return y + sig(x) * (1 - y);
	}
	
	private double sig(double x) {
		return (1 / (1 + Math.exp(-x)));
	}

}
