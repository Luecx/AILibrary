package neuralnetwork.network;

import core.tensor.Tensor;
import core.tensor.Tensor2D;
import neuralnetwork.builder.BuildException;
import neuralnetwork.functions.Function;
import neuralnetwork.functions.ReLU;
import neuralnetwork.nodes.Node1To1;

public class DenseNode extends Node1To1 {

    public DenseNode(int height) {
        super(1, 1, height);
    }

    Tensor2D weights;
    Tensor bias;
    double weights_min = Double.NaN;
    double weight_max  = Double.NaN;
    double bias_min  = Double.NaN;
    double bias_max  = Double.NaN;
    Function activation_function;

    public DenseNode setActivationFunction(Function f){
        this.activation_function = f;
        return this;
    }
    public DenseNode setWeightRange(double lower, double upper){
        weight_max = upper;
        weights_min = lower;
        return this;
    }
    public DenseNode setBiasRange(double lower, double upper){
        bias_max = upper;
        bias_min = lower;
        return this;
    }

    public Function getActivation_function() {
        return activation_function;
    }

    public void setWeights(Tensor2D weights) {
        if(weights.size() == this.weights.size())
            this.weights = weights;
    }

    public void setBias(Tensor bias) {
        if(bias.size() == this.bias.size())
            this.bias = bias;
    }

    public Tensor2D getWeights() {
        return weights;
    }

    public Tensor getBias() {
        return bias;
    }

    @Override
    protected void abs_calcOutputDim() throws BuildException {
        if(get_previous_node().getOutputSize() != get_previous_node().getOutputHeight()){
            throw new BuildException(this, "Input must be flat");
        }
    }

    @Override
    public void abs_genArrays() {

        if(this.weights == null){
            weights = new Tensor2D(get_previous_node().getOutputSize(), this.getOutputSize());
            if(!Double.isNaN(weights_min)&& !Double.isNaN(weight_max)){
                weights.randomizeRegular(weights_min, weight_max);
            }else{
                weights.randomizeRegular(
                        -1d / Math.sqrt(get_previous_node().getOutputSize()),
                        1d / Math.sqrt(get_previous_node().getOutputSize()));
            }
        }
        if(this.bias == null){
            bias = new Tensor(this.getOutputSize());
            if(!Double.isNaN(bias_min)&& !Double.isNaN(bias_max)){
                bias.randomizeRegular(bias_min, bias_max);
            }else{
                bias.randomizeRegular(
                        -1d / Math.sqrt(get_previous_node().getOutputSize()),
                        1d / Math.sqrt(get_previous_node().getOutputSize()));
            }
        }
        if(activation_function == null){
            activation_function = new ReLU();
        }
    }

    @Override
    public void abs_feedForward() {
        for(int i = 0; i < this.getOutputSize(); i++) {
            double sum = bias.getData()[i];
            for(int n = 0; n < this.get_previous_node().getOutputSize(); n++) {
                sum += this.getInputValue().getData()[n] * weights.get(n,i);
            }
            this.output_value.getData()[i] = sum;
        }
        this.activation_function.apply(this);
    }

    @Override
    public void abs_feedBackward() {
        for(int i = 0; i < this.get_previous_node().getOutputSize(); i++) {
            double sum = 0;
            for(int n = 0; n < this.getOutputSize(); n++) {
                sum += weights.get(i,n) * output_loss.getData()[n];
            }
            this.input_loss.getData()[i] = this.getInputDerivative().getData()[i] * sum;
        }
    }

    @Override
    public void abs_updateWeights(double eta) {
        for(int i = 0; i < this.getOutputSize(); i++) {
            double delta = - eta * this.output_loss.getData()[i];
            bias.getData()[i] += delta;
            for(int prevNeuron = 0; prevNeuron < this.get_previous_node().getOutputSize(); prevNeuron ++) {
                weights.set(
                    weights.get(prevNeuron, i) + delta * input_value.getData()[prevNeuron],
                        prevNeuron, i);
            }
        }
    }
}
