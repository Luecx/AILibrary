package algebra.nodes.basic;

import algebra.nodes.Dimension;
import algebra.nodes.Node;
import algebra.nodes.NodeCount;
import algebra.nodes.functions.ElementWiseOperation;
import neuralnetwork.builder.BuildException;

public class Hadamard extends ElementWiseOperation<Hadamard> {

    public Hadamard(Node... subChilds) {
        super(NodeCount.UNLIMITED, NodeCount.UNLIMITED);
        for(Node n:subChilds){
            this.addPreviousNode(n);
        }
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
        outputValue.reset(1);
        for(int i = 0; i < getPreviousNodes().size(); i++){
            functionDerivative[i].reset(1);
        }
        for(int i = 0; i < getPreviousNodes().size(); i++){
            for(int n = 0; n < outputValue.size(); n++){
                outputValue.getData()[n] *= getInputValue(i).getData()[n];
            }
        }
        for(int i = 0; i < getPreviousNodes().size(); i++){
            for(int n = 0; n < outputValue.size(); n++){
                functionDerivative[i].getData()[n] = outputValue.getData()[n] / getInputValue(i).getData()[n];
            }
        }
    }

    @Override
    public void autoDiff() {
        for(int inp = 0; inp < getPreviousNodes().size(); inp++){
            for(int i = 0; i < this.outputGradient.size(); i++){
                getInputGradient(inp).getData()[i] += outputGradient.getData()[i] * functionDerivative[inp].getData()[i];
            }
        }
    }

    @Override
    public Hadamard copy() {
        Hadamard add = new Hadamard();
        for(Node n:getPreviousNodesCopy()){
            add.addPreviousNode(n);
        }
        return add;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for(Node c:getPreviousNodes()){
            builder.append(c.toString());
            if(c != getPreviousNodes().get(getPreviousNodes().size()-1)){
                builder.append("*");
            }
        }
        builder.append(")");
        return builder.toString();
    }
}
