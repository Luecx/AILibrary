package algebra.nodes.basic;

import algebra.nodes.Dimension;
import algebra.nodes.Node;
import algebra.nodes.NodeCount;
import neuralnetwork.builder.BuildException;

public class Add extends Node {

    public Add() {
        super(NodeCount.UNLIMITED, NodeCount.ONE);
    }

    @Override
    protected Dimension selfCalcOutputDim() throws BuildException {
        if(!sameDimension(getPreviousNodes().toArray(new Node[]{}))){
            throw new BuildException(this, "Only accepting inputs with same dimension");
        }
        return new Dimension(getPreviousNode().getOutputDimension());
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
}
