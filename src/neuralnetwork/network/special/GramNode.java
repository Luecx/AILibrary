package neuralnetwork.network.special;

import neuralnetwork.builder.BuildException;
import neuralnetwork.nodes.Node1To1;

public class GramNode extends Node1To1 {


    @Override
    protected void abs_calcOutputDim() throws BuildException {
        this.setOutputDepth(1);
        this.setOutputWidth(getInputDepth());
        this.setOutputHeight(getInputDepth());
    }

    @Override
    public void abs_genArrays() { }

    @Override
    public void abs_feedForward() {
        for(int x = 0;x < getInputDepth(); x++){
            for(int y = 0; y < getInputDepth(); y++){
                double v = 0;
                for(int w = 0; w < getInputWidth(); w++){
                    for(int h = 0; h < getInputHeight(); h++){
                        v += input_value.get(x,w,h) * input_value.get(y,w,h);
                    }
                }
                getOutputValue().set(v / (getInputWidth() * getInputHeight()),0,x,y);
                getOutputDerivative().set(1,0,x,y);
            }
        }
    }

    @Override
    public void abs_feedBackward() {
        for(int i = 0; i < this.getInputSize(); i++){
            this.input_loss.getData()[i] = 0;
        }
        for(int i = 0; i < getOutputWidth(); i++){
            for(int n = 0; n < getOutputHeight(); n++){
                for(int a = 0; a < getInputWidth(); a++){
                    for(int b = 0; b < getInputHeight(); b++){

                        int depthA = i;
                        int depthB = n;
                        this.input_loss.getData()[this.input_loss.index(depthA,a,b)] +=
                                this.input_value.get(depthB, a, b) * this.output_loss.get(0,i,n) *
                                this.input_derivative.get(depthB, a, b) / (getInputWidth() * getInputHeight());
                        this.input_loss.getData()[this.input_loss.index(depthB,a,b)] +=
                                this.input_value.get(depthA, a, b) * this.output_loss.get(0,i,n)*
                                        this.input_derivative.get(depthA, a, b) / (getInputWidth() * getInputHeight());
                    }
                }
            }
        }
    }

    @Override
    public void abs_updateWeights(double eta) {

    }
}
