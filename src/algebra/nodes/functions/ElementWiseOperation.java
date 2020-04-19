package algebra.nodes.functions;

import algebra.nodes.Dimension;
import algebra.nodes.Node;
import algebra.nodes.NodeCount;
import core.tensor.Tensor;
import core.tensor.Tensor3D;
import neuralnetwork.builder.BuildException;

public abstract class ElementWiseOperation<T extends ElementWiseOperation<T>> extends Node<T> {


    protected Tensor[] functionDerivative;

    public ElementWiseOperation(NodeCount maxPrevNodes, NodeCount maxNextNodes) {
        super(maxPrevNodes, maxNextNodes);
    }

    public ElementWiseOperation(Dimension dimension, NodeCount maxPrevNodes, NodeCount maxNextNodes) {
        super(dimension, maxPrevNodes, maxNextNodes);
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
        functionDerivative = new Tensor[getPreviousNodes().size()];
        for(int i = 0; i < getPreviousNodes().size(); i++){
            functionDerivative[i] = this.getOutputDimension().emptyTensor();
        }
    }

    public Tensor getFunctionDerivative(int input){
        return functionDerivative[input];
    }

    public Tensor[] getFunctionDerivative() {
        return functionDerivative;
    }

    public void setFunctionDerivative(Tensor[] functionDerivative) {
        this.functionDerivative = functionDerivative;
    }
}
