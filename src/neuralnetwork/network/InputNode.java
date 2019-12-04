package neuralnetwork.network;

import neuralnetwork.nodes.Node1To1;

public class InputNode extends Node1To1 {


    public InputNode(int depth, int width, int height) {
        super(depth, width, height);
    }

    @Override
    protected void abs_calcOutputDim() {

    }

    @Override
    public void abs_genArrays() {
    }

    @Override
    public void abs_feedForward() {
        for(int i = 0; i < this.getOutputSize(); i++){
            this.output_derivative.getData()[i] = 1;
        }
    }

    @Override
    public void abs_feedBackward() {

    }

    @Override
    public void abs_updateWeights(double eta) {

    }

}
