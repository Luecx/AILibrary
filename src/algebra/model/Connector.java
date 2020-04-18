package algebra.model;

import algebra.nodes.Dimension;
import algebra.nodes.Node;
import algebra.nodes.NodeCount;
import neuralnetwork.builder.BuildException;

public class Connector extends Node<Connector> {

    public Connector() {
        super(NodeCount.NONE,NodeCount.UNLIMITED);
    }

    @Override
    protected Dimension selfCalcOutputDim() throws BuildException {
        return null;
    }

    @Override
    protected void selfInit() {
    }

    @Override
    public void calc() {
    }

    @Override
    public void autoDiff() {
    }

    @Override
    public Connector copy() {
        return new Connector();
    }
}
