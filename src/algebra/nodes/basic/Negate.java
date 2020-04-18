package algebra.nodes.basic;

import algebra.nodes.Dimension;
import algebra.nodes.Node;
import algebra.nodes.NodeCount;
import neuralnetwork.builder.BuildException;

public class Negate extends Node<Negate> {

    public Negate(Node subChilds) {
        super(NodeCount.ONE, NodeCount.UNLIMITED);
        this.addPreviousNode(subChilds);
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
        outputDerivative[0].reset(-1);

        outputValue.self_sub(getPreviousNode().getOutputValue());
    }

    @Override
    public void autoDiff() {
        for(int i = 0; i < getOutputGradient().size(); i++){
            getInputGradient().getData()[i] -= getOutputGradient().getData()[i];
        }
    }

    @Override
    public Negate copy() {
        Negate add = new Negate(getPreviousNode().copy());
        return add;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        builder.append("-");
        builder.append(getPreviousNode());
        builder.append(")");
        return builder.toString();
    }
}
