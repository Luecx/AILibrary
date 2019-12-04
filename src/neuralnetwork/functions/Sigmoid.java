package neuralnetwork.functions;

public class Sigmoid extends Function {
    @Override
    protected double func_val(double x) {
        return 1d / (1 + Math.exp(-x));
    }

    @Override
    protected double func_val_prime(double x) {
        return func_val(x) * (1- func_val(x));
    }
}
