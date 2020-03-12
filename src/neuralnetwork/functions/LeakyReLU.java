package neuralnetwork.functions;

public class LeakyReLU extends Function {
    @Override
    protected double func_val(double x) {
        return x > 0 ? x:x*0.1;
    }

    @Override
    protected double func_val_prime(double x) {
        return x > 0 ? 1:0.1;
    }
}
