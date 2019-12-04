package neuralnetwork.loss;

import core.tensor.Tensor;
import neuralnetwork.network.OutputNode;

public abstract class Error {

    public abstract double calculate_loss(OutputNode out, Tensor exp, double factor);
}
