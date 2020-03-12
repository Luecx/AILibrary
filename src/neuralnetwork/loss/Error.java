package neuralnetwork.loss;

import core.tensor.Tensor;
import neuralnetwork.network.OutputNode;
import neuralnetwork.nodes.Node;

public abstract class Error {

    public abstract double calculate_loss(Node out, Tensor exp, double factor);
}
