package neuralnetwork.network;

import neuralnetwork.nodes.Node1To1;

public class FlattenNode extends Node1To1 {


    @Override
    protected void abs_calcOutputDim() {
        this.setOutputDepth(1);
        this.setOutputWidth(1);
        this.setOutputHeight(this.get_previous_node().getOutputSize());
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
