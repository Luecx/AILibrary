package newalgebra.network.nodes;

import neuralnetwork.nodes.Node1To1;

public class ShapeNode extends Node1To1 {

    public ShapeNode(int output_depth, int output_width, int output_height) {
        super(output_depth, output_width, output_height);
    }

    protected void abs_calcOutputDim() {
    }

    @Override
    public void abs_genArrays() {
    }

    @Override
    public void abs_feedForward() {
        for(int i = 0; i < getOutputSize(); i++){
            this.output_value.getData()[i] = this.input_value.getData()[i];
            this.output_derivative.getData()[i] = this.input_derivative.getData()[i];
        }
    }

    @Override
    public void abs_feedBackward() {
        for(int i = 0; i < getOutputSize(); i++){
            this.input_loss.getData()[i] = this.output_loss.getData()[i];
        }
    }

    @Override
    public void abs_updateWeights(double eta) {

    }

}
