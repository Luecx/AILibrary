package neuralnetwork.functions;

public class None extends Function {
    @Override
    protected double func_val(double x) {
        return x;
    }

    @Override
    protected double func_val_prime(double x) {
        return 1;
    }
}
