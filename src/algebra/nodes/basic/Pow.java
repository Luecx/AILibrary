package algebra.nodes.basic;

import algebra.nodes.Dimension;
import algebra.nodes.Node;
import algebra.nodes.NodeCount;
import algebra.nodes.functions.ElementWiseOperation;
import neuralnetwork.builder.BuildException;

public class Pow extends ElementWiseOperation<Pow> {

    private double power;

    public Pow(Node subChilds, double power) {
        super(NodeCount.ONE, NodeCount.UNLIMITED);
        this.addPreviousNode(subChilds);
        this.power = power;
    }

    @Override
    protected Dimension selfCalcOutputDim() throws BuildException {
        return new Dimension(getPreviousNode().getOutputDimension());
    }

    @Override
    protected void selfInit() {

    }

    @Override
    public void calc() {
        outputValue.reset(0);
        functionDerivative[0].reset(-1);

        for(int i = 0; i < getOutputValue().size(); i++){
            getOutputValue().getData()[i] = Math.pow(getInputValue().getData()[i],power);
            getFunctionDerivative(0).getData()[i] = power * Math.pow(getInputValue().getData()[i],power-1);
        }

    }

    @Override
    public void autoDiff() {
        for(int i = 0; i < getOutputGradient().size(); i++){
            getInputGradient().getData()[i] += getOutputGradient().getData()[i] * functionDerivative[0].getData()[i];
        }
    }

    @Override
    public Pow copy() {
        Pow add = new Pow(getPreviousNode().copy(), power);
        return add;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getPreviousNode());
        builder.append("^");
        builder.append(power);
        return builder.toString();
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }
}
