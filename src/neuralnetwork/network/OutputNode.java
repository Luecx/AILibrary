package neuralnetwork.network;

import core.tensor.Tensor;
import neuralnetwork.loss.Error;
import neuralnetwork.loss.MSE;
import neuralnetwork.nodes.Node1To1;

public class OutputNode extends Node1To1 {

    public OutputNode() {
        super();
    }

    private Error error_function = new MSE();
    private double error_factor = 1;


    public Error getErrorFunction() {
        return error_function;
    }
    public void setErrorFunction(Error error_function) {
        this.error_function = error_function;
    }
    public double getErrorFactor() {
        return error_factor;
    }
    public void setErrorFactor(double error_factor) {
        this.error_factor = error_factor;
    }

    public double calculateLoss(Tensor exp){
        return error_function.calculate_loss(this, exp, error_factor);
    }

    @Override
    protected void abs_calcOutputDim() {
        this.setOutputDepth(get_previous_node().getOutputDepth());
        this.setOutputWidth(get_previous_node().getOutputWidth());
        this.setOutputHeight(get_previous_node().getOutputHeight());
    }

    @Override
    public void abs_genArrays() {

    }

    @Override
    public void abs_feedForward() {
        for(int i = 0; i < getOutputSize(); i++){
            output_value.getData()[i] = input_value.getData()[i];
        }
    }

    @Override
    public void abs_feedBackward() {
        for(int i = 0; i < getOutputSize(); i++){
            input_loss.getData()[i] = output_loss.getData()[i];
        }
    }

    @Override
    public void abs_updateWeights(double eta) {

    }

}
