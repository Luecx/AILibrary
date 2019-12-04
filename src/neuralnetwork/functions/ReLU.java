package neuralnetwork.functions;

public class ReLU extends Function {
    @Override
    protected double func_val(double x) {
        return x > 0 ? x:0;
    }

    @Override
    protected double func_val_prime(double x) {
        return x > 0 ? 1:0;
    }
}
