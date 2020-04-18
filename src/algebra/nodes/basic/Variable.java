package algebra.nodes.basic;

import algebra.nodes.Dimension;
import algebra.nodes.Node;
import algebra.nodes.NodeCount;
import core.tensor.Tensor;
import core.tensor.Tensor3D;
import neuralnetwork.builder.BuildException;

public class Variable extends Node<Variable> {

    protected Tensor value;

    public Variable(String identifier, Dimension dimension) {
        super(new Dimension(dimension), NodeCount.NONE, NodeCount.UNLIMITED);
        this.setIdentifier(identifier);
    }



    @Override
    protected Dimension selfCalcOutputDim() throws BuildException {
        return null;
    }

    @Override
    protected void selfInit() {
        this.value = new Tensor(getOutputValue());
    }

    @Override
    public void calc() {
        this.outputValue = value;
    }

    @Override
    public void autoDiff() {
    }

    @Override
    public Variable copy() {

        Variable copy = new Variable(null, this.getOutputDimension());

        if(value != null){
            copy.setValue(value.copy());
        }
        return copy;
    }

    @Override
    public String toString() {
        return this.getIdentifier();
    }

    public Tensor getValue() {
        return value;
    }

    public void setValue(Tensor value) {
        this.value = value;
    }
}
