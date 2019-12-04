package neuralnetwork.loss;

import core.tensor.Tensor;
import neuralnetwork.network.OutputNode;

public class MSE extends Error {

    @Override
    public double calculate_loss(OutputNode out, Tensor exp, double fac) {
        double v = 0;

        for(int i = 0; i < out.getOutputSize(); i++){
            out.getOutputLoss().getData()[i] = fac * (out.getOutputValue().getData()[i] - exp.getData()[i]);
            v += 1/2d * (out.getOutputLoss().getData()[i] * out.getOutputLoss().getData()[i] );
        }

        return v / out.getOutputSize();
    }
}
